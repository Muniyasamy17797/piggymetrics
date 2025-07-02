package com.lms.enrollment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseProgressStats {
    private Long courseId;
    private Long enrollmentCount;
    private Double averageProgress;
    private Double averageScore;
}