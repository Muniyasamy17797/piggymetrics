package com.lms.enrollment.service;

import com.lms.enrollment.model.Enrollment;
import com.lms.enrollment.model.LessonProgress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EnrollmentService {
    Mono<Enrollment> enrollStudent(Long studentId, Long courseId);
    Mono<Enrollment> getEnrollment(Long enrollmentId);
    Mono<Enrollment> getEnrollmentByStudentAndCourse(Long studentId, Long courseId);
    Flux<Enrollment> getStudentEnrollments(Long studentId);
    Mono<Page<Enrollment>> getStudentEnrollmentsPaged(Long studentId, Pageable pageable);
    Mono<LessonProgress> updateLessonProgress(Long enrollmentId, Long lessonId, LessonProgress progress);
    Mono<Enrollment> updateEnrollmentProgress(Long enrollmentId);
    Mono<Void> dropEnrollment(Long enrollmentId);
    Mono<Enrollment> completeEnrollment(Long enrollmentId);
}