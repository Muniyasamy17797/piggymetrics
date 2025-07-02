package com.lms.enrollment.service;

import com.lms.enrollment.model.Enrollment;
import com.lms.enrollment.model.EnrollmentStatus;
import com.lms.enrollment.model.LessonProgress;
import com.lms.enrollment.model.LessonStatus;
import com.lms.enrollment.repository.EnrollmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceImplTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    private Enrollment testEnrollment;
    private LessonProgress testProgress;

    @BeforeEach
    void setUp() {
        testEnrollment = new Enrollment();
        testEnrollment.setId(1L);
        testEnrollment.setStudentId(1L);
        testEnrollment.setCourseId(1L);
        testEnrollment.setStatus(EnrollmentStatus.IN_PROGRESS);
        testEnrollment.setProgress(0.0);
        testEnrollment.setLessonProgresses(new ArrayList<>());

        testProgress = new LessonProgress();
        testProgress.setId(1L);
        testProgress.setLessonId(1L);
        testProgress.setStatus(LessonStatus.COMPLETED);
        testProgress.setScore(85.0);
    }

    @Test
    void enrollStudent_Success() {
        when(enrollmentRepository.findByStudentIdAndCourseId(1L, 1L))
            .thenReturn(Optional.empty());
        when(enrollmentRepository.save(any(Enrollment.class)))
            .thenReturn(testEnrollment);

        Mono<Enrollment> result = enrollmentService.enrollStudent(1L, 1L);

        StepVerifier.create(result)
            .expectNext(testEnrollment)
            .verifyComplete();
    }

    @Test
    void enrollStudent_AlreadyEnrolled() {
        when(enrollmentRepository.findByStudentIdAndCourseId(1L, 1L))
            .thenReturn(Optional.of(testEnrollment));

        Mono<Enrollment> result = enrollmentService.enrollStudent(1L, 1L);

        StepVerifier.create(result)
            .expectError(RuntimeException.class)
            .verify();
    }

    @Test
    void getEnrollment_Success() {
        when(enrollmentRepository.findById(1L))
            .thenReturn(Optional.of(testEnrollment));

        Mono<Enrollment> result = enrollmentService.getEnrollment(1L);

        StepVerifier.create(result)
            .expectNext(testEnrollment)
            .verifyComplete();
    }

    @Test
    void updateLessonProgress_Success() {
        when(enrollmentRepository.findById(1L))
            .thenReturn(Optional.of(testEnrollment));
        when(enrollmentRepository.save(any(Enrollment.class)))
            .thenReturn(testEnrollment);

        Mono<LessonProgress> result = enrollmentService.updateLessonProgress(1L, 1L, testProgress);

        StepVerifier.create(result)
            .expectNextMatches(progress -> 
                progress.getLessonId().equals(1L) &&
                progress.getStatus() == LessonStatus.COMPLETED &&
                progress.getScore() == 85.0)
            .verifyComplete();
    }

    @Test
    void completeEnrollment_Success() {
        when(enrollmentRepository.findById(1L))
            .thenReturn(Optional.of(testEnrollment));
        when(enrollmentRepository.save(any(Enrollment.class)))
            .thenReturn(testEnrollment);

        Mono<Enrollment> result = enrollmentService.completeEnrollment(1L);

        StepVerifier.create(result)
            .expectNextMatches(enrollment -> 
                enrollment.getStatus() == EnrollmentStatus.COMPLETED &&
                enrollment.getProgress() == 100.0 &&
                enrollment.getCompletedAt() != null)
            .verifyComplete();
    }
}