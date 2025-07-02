package com.lms.enrollment.controller;

import com.lms.enrollment.model.Enrollment;
import com.lms.enrollment.model.LessonProgress;
import com.lms.enrollment.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/students/{studentId}/courses/{courseId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Enrollment> enrollStudent(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        return enrollmentService.enrollStudent(studentId, courseId);
    }

    @GetMapping("/{enrollmentId}")
    public Mono<Enrollment> getEnrollment(@PathVariable Long enrollmentId) {
        return enrollmentService.getEnrollment(enrollmentId);
    }

    @GetMapping("/students/{studentId}/courses/{courseId}")
    public Mono<Enrollment> getEnrollmentByStudentAndCourse(
            @PathVariable Long studentId,
            @PathVariable Long courseId) {
        return enrollmentService.getEnrollmentByStudentAndCourse(studentId, courseId);
    }

    @GetMapping("/students/{studentId}")
    public Flux<Enrollment> getStudentEnrollments(@PathVariable Long studentId) {
        return enrollmentService.getStudentEnrollments(studentId);
    }

    @GetMapping("/students/{studentId}/paged")
    public Mono<Page<Enrollment>> getStudentEnrollmentsPaged(
            @PathVariable Long studentId,
            Pageable pageable) {
        return enrollmentService.getStudentEnrollmentsPaged(studentId, pageable);
    }

    @PutMapping("/{enrollmentId}/lessons/{lessonId}/progress")
    public Mono<LessonProgress> updateLessonProgress(
            @PathVariable Long enrollmentId,
            @PathVariable Long lessonId,
            @RequestBody LessonProgress progress) {
        return enrollmentService.updateLessonProgress(enrollmentId, lessonId, progress);
    }

    @PutMapping("/{enrollmentId}/progress")
    public Mono<Enrollment> updateEnrollmentProgress(@PathVariable Long enrollmentId) {
        return enrollmentService.updateEnrollmentProgress(enrollmentId);
    }

    @PostMapping("/{enrollmentId}/drop")
    public Mono<Void> dropEnrollment(@PathVariable Long enrollmentId) {
        return enrollmentService.dropEnrollment(enrollmentId);
    }

    @PostMapping("/{enrollmentId}/complete")
    public Mono<Enrollment> completeEnrollment(@PathVariable Long enrollmentId) {
        return enrollmentService.completeEnrollment(enrollmentId);
    }
}