package com.lms.enrollment.service;

import com.lms.enrollment.model.Enrollment;
import com.lms.enrollment.model.EnrollmentStatus;
import com.lms.enrollment.model.LessonProgress;
import com.lms.enrollment.model.LessonStatus;
import com.lms.enrollment.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    @Override
    @Transactional
    public Mono<Enrollment> enrollStudent(Long studentId, Long courseId) {
        return Mono.fromCallable(() -> {
            if (enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId).isPresent()) {
                throw new RuntimeException("Student is already enrolled in this course");
            }

            Enrollment enrollment = new Enrollment();
            enrollment.setStudentId(studentId);
            enrollment.setCourseId(courseId);
            return enrollmentRepository.save(enrollment);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Enrollment> getEnrollment(Long enrollmentId) {
        return Mono.fromCallable(() ->
            enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"))
        ).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Enrollment> getEnrollmentByStudentAndCourse(Long studentId, Long courseId) {
        return Mono.fromCallable(() ->
            enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"))
        ).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<Enrollment> getStudentEnrollments(Long studentId) {
        return Flux.fromIterable(enrollmentRepository.findByStudentId(studentId))
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Page<Enrollment>> getStudentEnrollmentsPaged(Long studentId, Pageable pageable) {
        return Mono.fromCallable(() ->
            enrollmentRepository.findByStudentId(studentId, pageable)
        ).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional
    public Mono<LessonProgress> updateLessonProgress(Long enrollmentId, Long lessonId, LessonProgress progress) {
        return Mono.fromCallable(() -> {
            Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                    .orElseThrow(() -> new RuntimeException("Enrollment not found"));

            LessonProgress lessonProgress = enrollment.getLessonProgresses().stream()
                    .filter(lp -> lp.getLessonId().equals(lessonId))
                    .findFirst()
                    .orElseGet(() -> {
                        LessonProgress newProgress = new LessonProgress();
                        newProgress.setEnrollment(enrollment);
                        newProgress.setLessonId(lessonId);
                        enrollment.getLessonProgresses().add(newProgress);
                        return newProgress;
                    });

            lessonProgress.setStatus(progress.getStatus());
            lessonProgress.setScore(progress.getScore());

            if (progress.getStatus() == LessonStatus.COMPLETED) {
                lessonProgress.setCompletedAt(LocalDateTime.now());
            }

            enrollmentRepository.save(enrollment);
            return lessonProgress;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional
    public Mono<Enrollment> updateEnrollmentProgress(Long enrollmentId) {
        return Mono.fromCallable(() -> {
            Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                    .orElseThrow(() -> new RuntimeException("Enrollment not found"));

            long completedLessons = enrollment.getLessonProgresses().stream()
                    .filter(lp -> lp.getStatus() == LessonStatus.COMPLETED)
                    .count();

            double progress = (double) completedLessons / enrollment.getLessonProgresses().size() * 100;
            enrollment.setProgress(progress);

            if (progress >= 100) {
                enrollment.setStatus(EnrollmentStatus.COMPLETED);
                enrollment.setCompletedAt(LocalDateTime.now());
            }

            return enrollmentRepository.save(enrollment);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    @Transactional
    public Mono<Void> dropEnrollment(Long enrollmentId) {
        return Mono.fromCallable(() -> {
            Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                    .orElseThrow(() -> new RuntimeException("Enrollment not found"));
            enrollment.setStatus(EnrollmentStatus.DROPPED);
            enrollmentRepository.save(enrollment);
            return null;
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }

    @Override
    @Transactional
    public Mono<Enrollment> completeEnrollment(Long enrollmentId) {
        return Mono.fromCallable(() -> {
            Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                    .orElseThrow(() -> new RuntimeException("Enrollment not found"));
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
            enrollment.setProgress(100.0);
            enrollment.setCompletedAt(LocalDateTime.now());
            return enrollmentRepository.save(enrollment);
        }).subscribeOn(Schedulers.boundedElastic());
    }
}