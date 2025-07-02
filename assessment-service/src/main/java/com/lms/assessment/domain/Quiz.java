package com.lms.assessment.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "quizzes")
public class Quiz {
    @Id
    private String id;
    
    @NotEmpty
    private String title;
    
    private String description;
    
    @NotNull
    private String courseId;
    
    @NotEmpty
    private List<Question> questions;
    
    @NotNull
    private LocalDateTime startDate;
    
    @NotNull
    private LocalDateTime endDate;
    
    private int timeLimit; // in minutes
    
    private int maxAttempts;
    
    private boolean shuffleQuestions;
    
    private double passingScore;
}