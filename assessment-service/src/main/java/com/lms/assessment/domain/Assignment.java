package com.lms.assessment.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "assignments")
public class Assignment {
    @Id
    private String id;
    
    @NotEmpty
    private String title;
    
    private String description;
    
    @NotNull
    private String courseId;
    
    @NotNull
    private LocalDateTime dueDate;
    
    private double totalPoints;
    
    private List<String> attachments;
    
    private String rubric;
    
    private boolean allowLateSubmissions;
    
    private double latePenalty; // percentage per day
    
    private SubmissionType submissionType;
    
    public enum SubmissionType {
        FILE_UPLOAD,
        TEXT_ENTRY,
        URL,
        MEDIA_RECORDING
    }
}