package com.lms.assessment.service;

import com.lms.assessment.domain.Grade;
import com.lms.assessment.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GradingService {
    private final GradeRepository gradeRepository;

    public Grade submitGrade(Grade grade) {
        grade.setGradedDate(LocalDateTime.now());
        return gradeRepository.save(grade);
    }

    public Optional<Grade> getGrade(String id) {
        return gradeRepository.findById(id);
    }

    public List<Grade> getStudentGrades(String studentId) {
        return gradeRepository.findByStudentId(studentId);
    }

    public List<Grade> getCourseGrades(String courseId) {
        return gradeRepository.findByCourseId(courseId);
    }

    public List<Grade> getStudentCourseGrades(String studentId, String courseId) {
        return gradeRepository.findByStudentIdAndCourseId(studentId, courseId);
    }

    public Grade getStudentAssessmentGrade(String studentId, String assessmentId) {
        return gradeRepository.findByStudentIdAndAssessmentId(studentId, assessmentId);
    }

    public Grade updateGrade(String id, Grade grade) {
        grade.setId(id);
        grade.setGradedDate(LocalDateTime.now());
        return gradeRepository.save(grade);
    }
}