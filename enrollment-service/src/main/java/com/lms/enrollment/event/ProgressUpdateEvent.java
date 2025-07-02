package com.lms.enrollment.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProgressUpdateEvent {
    private Long enrollmentId;
    private Long studentId;
    private Long courseId;
    private Double progress;
    private String status;
    private Double score;
}