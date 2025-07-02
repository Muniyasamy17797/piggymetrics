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
@CacheConfig(cacheNames = {"enrollments"})
public class EnrollmentServiceImpl implements EnrollmentService {

    private final ApplicationEventPublisher eventPublisher;
    private final NotificationServiceClient notificationClient;

    private final EnrollmentRepository enrollmentRepository;
    private final ProgressAnalyticsService progressAnalyticsService;
    private final WebClient certificateServiceClient;

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
    @Cacheable(key = "#enrollmentId")
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
    @CacheEvict(key = "#enrollmentId")
    @Async("asyncExecutor")
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
            lessonProgress.setTimeSpentMinutes(
                lessonProgress.getTimeSpentMinutes() + progress.getTimeSpentMinutes()
            );
            lessonProgress.setAttemptsCount(
                lessonProgress.getAttemptsCount() + 1
            );
            
            if (progress.getCompletedActivities() != null) {
                lessonProgress.getCompletedActivities().addAll(progress.getCompletedActivities());
            }
            
            if (progress.getCompletedQuizzes() != null) {
                lessonProgress.getCompletedQuizzes().addAll(progress.getCompletedQuizzes());
            }
            
            if (progress.getSubmittedAssignments() != null) {
                lessonProgress.getSubmittedAssignments().addAll(progress.getSubmittedAssignments());
            }

            if (progress.getStatus() == LessonStatus.COMPLETED) {
                lessonProgress.setCompletedAt(LocalDateTime.now());
                
                // Send notification for lesson completion
                notificationClient.sendNotification(
                    enrollment.getStudentId(),
                    "Lesson Completed",
                    "You have completed lesson " + lessonId + " in the course!"
                ).subscribe();
            }

            // Update overall enrollment progress
            progressAnalyticsService.updateEnrollmentAnalytics(enrollment);
            enrollment = enrollmentRepository.save(enrollment);
            
            // Publish progress update event
            eventPublisher.publishEvent(new ProgressUpdateEvent(
                enrollment.getId(),
                enrollment.getStudentId(),
                enrollment.getCourseId(),
                enrollment.getProgress(),
                enrollment.getStatus().toString(),
                enrollment.getAverageScore()
            ));
            
            return lessonProgress;
        }).subscribeOn(Schedulers.boundedElastic());
    }
    }

    @Override
    @Transactional
    public Mono<Enrollment> updateEnrollmentProgress(Long enrollmentId) {
        return Mono.fromCallable(() -> {
            Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                    .orElseThrow(() -> new RuntimeException("Enrollment not found"));

            progressAnalyticsService.updateEnrollmentAnalytics(enrollment);

            if (enrollment.getProgress() >= 100 && enrollment.getStatus() != EnrollmentStatus.COMPLETED) {
                enrollment.setStatus(EnrollmentStatus.COMPLETED);
                enrollment.setCompletedAt(LocalDateTime.now());
                
                // Generate certificate
                certificateServiceClient.post()
                    .uri("/api/certificates")
                    .bodyValue(new CertificateRequest(
                        enrollment.getStudentId(),
                        enrollment.getCourseId(),
                        enrollment.getAverageScore()
                    ))
                    .retrieve()
                    .bodyToMono(CertificateResponse.class)
                    .subscribe(response -> {
                        enrollment.setCertificateId(response.getCertificateId());
                        enrollmentRepository.save(enrollment);
                    });
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