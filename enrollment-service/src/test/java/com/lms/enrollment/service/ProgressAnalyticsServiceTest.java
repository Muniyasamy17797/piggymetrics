package com.lms.enrollment.service;

import com.lms.enrollment.model.Enrollment;
import com.lms.enrollment.model.LessonProgress;
import com.lms.enrollment.model.LessonStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class ProgressAnalyticsServiceTest {

    private ProgressAnalyticsService progressAnalyticsService;

    @BeforeEach
    void setUp() {
        progressAnalyticsService = new ProgressAnalyticsService();
    }

    @Test
    void updateEnrollmentAnalytics_shouldCalculateCorrectly() {
        // Given
        Enrollment enrollment = new Enrollment();
        
        LessonProgress progress1 = new LessonProgress();
        progress1.setStatus(LessonStatus.COMPLETED);
        progress1.setScore(90.0);
        progress1.setTimeSpentMinutes(30);
        progress1.setCompletedAt(LocalDateTime.now());
        progress1.getCompletedQuizzes().add(1L);
        progress1.getSubmittedAssignments().add(1L);
        
        LessonProgress progress2 = new LessonProgress();
        progress2.setStatus(LessonStatus.IN_PROGRESS);
        progress2.setScore(80.0);
        progress2.setTimeSpentMinutes(20);
        progress2.getCompletedQuizzes().add(2L);
        
        enrollment.setLessonProgresses(Arrays.asList(progress1, progress2));

        // When
        progressAnalyticsService.updateEnrollmentAnalytics(enrollment);

        // Then
        assertEquals(50.0, enrollment.getProgress());
        assertEquals(85.0, enrollment.getAverageScore());
        assertEquals(50, enrollment.getTotalTimeSpentMinutes());
        assertEquals(1, enrollment.getCompletedLessons());
        assertEquals(2, enrollment.getCompletedQuizzes());
        assertEquals(1, enrollment.getSubmittedAssignments());
    }
}