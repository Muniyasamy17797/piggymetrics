package com.lms.enrollment.service;

import com.lms.enrollment.model.Enrollment;
import com.lms.enrollment.model.LessonProgress;
import com.lms.enrollment.dto.CourseProgressStats;
import com.lms.enrollment.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"analytics"})
public class ProgressAnalyticsService {

    private final EnrollmentRepository enrollmentRepository;
    
    @Async("asyncExecutor")
    public void processProgressUpdate(Long enrollmentId, Long courseId, Double progress, Double score) {
        // Process analytics asynchronously
        updateCourseAnalytics(courseId);
    }
    
    public void updateEnrollmentAnalytics(Enrollment enrollment) {
        int totalLessons = enrollment.getLessonProgresses().size();
        int completedLessons = 0;
        int totalQuizzes = 0;
        int totalAssignments = 0;
        int totalTimeSpent = 0;
        double totalScore = 0.0;
        int scoredItems = 0;
        
        for (LessonProgress progress : enrollment.getLessonProgresses()) {
            if (progress.getCompletedAt() != null) {
                completedLessons++;
            }
            
            totalQuizzes += progress.getCompletedQuizzes().size();
            totalAssignments += progress.getSubmittedAssignments().size();
            totalTimeSpent += progress.getTimeSpentMinutes();
            
            if (progress.getScore() != null) {
                totalScore += progress.getScore();
                scoredItems++;
            }
        }
        
        enrollment.setCompletedLessons(completedLessons);
        enrollment.setCompletedQuizzes(totalQuizzes);
        enrollment.setSubmittedAssignments(totalAssignments);
        enrollment.setTotalTimeSpentMinutes(totalTimeSpent);
        
        if (scoredItems > 0) {
            enrollment.setAverageScore(totalScore / scoredItems);
        }
        
        if (totalLessons > 0) {
            enrollment.setProgress((double) completedLessons / totalLessons * 100);
        }
    }

    @Cacheable(key = "#courseId")
    public CourseProgressStats getCourseAnalytics(Long courseId) {
        List<CourseProgressStats> stats = enrollmentRepository.getCourseProgressStats(List.of(courseId));
        return stats.isEmpty() ? new CourseProgressStats(courseId, 0L, 0.0, 0.0) : stats.get(0);
    }

    @Async("asyncExecutor")
    public void updateCourseAnalytics(Long courseId) {
        Double avgProgress = enrollmentRepository.getAverageCourseProgress(courseId);
        Double avgScore = enrollmentRepository.getAverageCourseScore(courseId);
        // Additional analytics processing can be added here
    }
}