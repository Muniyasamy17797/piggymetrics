package com.lms.assessment.repository;

import com.lms.assessment.domain.Grade;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GradeRepository extends MongoRepository<Grade, String> {
    List<Grade> findByStudentId(String studentId);
    List<Grade> findByCourseId(String courseId);
    List<Grade> findByStudentIdAndCourseId(String studentId, String courseId);
    Grade findByStudentIdAndAssessmentId(String studentId, String assessmentId);
}