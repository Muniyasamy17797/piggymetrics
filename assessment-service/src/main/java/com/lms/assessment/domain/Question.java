package com.lms.assessment.domain;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class Question {
    @NotEmpty
    private String text;
    
    private QuestionType type;
    
    @NotEmpty
    private List<String> options;
    
    @NotEmpty
    private List<String> correctAnswers;
    
    private double points;
    
    private String explanation;
    
    public enum QuestionType {
        MULTIPLE_CHOICE,
        MULTIPLE_ANSWER,
        TRUE_FALSE,
        SHORT_ANSWER,
        ESSAY
    }
}