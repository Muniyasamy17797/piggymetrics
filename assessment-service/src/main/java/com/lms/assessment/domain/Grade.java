package com.lms.assessment.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Document(collection = "grades")
public class Grade {
    @Id
    private String id;
    
    @NotNull
    private String studentId;
    
    @NotNull
    private String assessmentId; // can be either quiz or assignment id
    
    @NotNull
    private String courseId;
    
    private AssessmentType assessmentType;
    
    private double score;
    
    private double maxScore;
    
    private String feedback;
    
    private LocalDateTime submissionDate;
    
    private LocalDateTime gradedDate;
    
    private String gradedBy;
    
    public enum AssessmentType {
        QUIZ,
        ASSIGNMENT
    }
}