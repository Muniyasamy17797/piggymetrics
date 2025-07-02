package com.lms.course.repository;

import com.lms.course.model.CourseInstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseInstructorRepository extends JpaRepository<CourseInstructor, Long> {
    List<CourseInstructor> findByCourseId(Long courseId);
    List<CourseInstructor> findByInstructorId(Long instructorId);
    Optional<CourseInstructor> findByCourseIdAndInstructorId(Long courseId, Long instructorId);
    void deleteByCourseIdAndInstructorId(Long courseId, Long instructorId);
}