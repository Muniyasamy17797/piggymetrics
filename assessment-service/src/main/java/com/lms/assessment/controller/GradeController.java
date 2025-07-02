package com.lms.assessment.controller;

import com.lms.assessment.domain.Grade;
import com.lms.assessment.service.GradingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {
    private final GradingService gradingService;

    @PostMapping
    public ResponseEntity<Grade> submitGrade(@Valid @RequestBody Grade grade) {
        return ResponseEntity.ok(gradingService.submitGrade(grade));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Grade> getGrade(@PathVariable String id) {
        return gradingService.getGrade(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Grade>> getStudentGrades(@PathVariable String studentId) {
        return ResponseEntity.ok(gradingService.getStudentGrades(studentId));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Grade>> getCourseGrades(@PathVariable String courseId) {
        return ResponseEntity.ok(gradingService.getCourseGrades(courseId));
    }

    @GetMapping("/student/{studentId}/course/{courseId}")
    public ResponseEntity<List<Grade>> getStudentCourseGrades(
            @PathVariable String studentId,
            @PathVariable String courseId) {
        return ResponseEntity.ok(gradingService.getStudentCourseGrades(studentId, courseId));
    }

    @GetMapping("/student/{studentId}/assessment/{assessmentId}")
    public ResponseEntity<Grade> getStudentAssessmentGrade(
            @PathVariable String studentId,
            @PathVariable String assessmentId) {
        Grade grade = gradingService.getStudentAssessmentGrade(studentId, assessmentId);
        return grade != null ? ResponseEntity.ok(grade) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Grade> updateGrade(@PathVariable String id, @Valid @RequestBody Grade grade) {
        return ResponseEntity.ok(gradingService.updateGrade(id, grade));
    }
}