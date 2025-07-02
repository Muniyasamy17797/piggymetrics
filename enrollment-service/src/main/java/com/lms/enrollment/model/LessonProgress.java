package com.lms.enrollment.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "lesson_progress")
@EntityListeners(AuditingEntityListener.class)
public class LessonProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id")
    private Enrollment enrollment;

    private Long lessonId;

    @Enumerated(EnumType.STRING)
    private LessonStatus status = LessonStatus.NOT_STARTED;

    private Double score;
    
    private Integer timeSpentMinutes = 0;
    
    private Integer attemptsCount = 0;
    
    @ElementCollection
    private List<Long> completedActivities = new ArrayList<>();
    
    @ElementCollection
    private List<Long> completedQuizzes = new ArrayList<>();
    
    @ElementCollection
    private List<Long> submittedAssignments = new ArrayList<>();

    private LocalDateTime completedAt;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}