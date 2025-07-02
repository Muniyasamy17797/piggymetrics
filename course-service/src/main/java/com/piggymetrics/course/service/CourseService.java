package com.piggymetrics.course.service;

import com.piggymetrics.course.domain.Course;
import java.util.List;
import java.util.Optional;

public interface CourseService {
    Course createCourse(Course course);
    Optional<Course> getCourse(Long id);
    List<Course> getAllCourses();
    Course updateCourse(Long id, Course course);
    void deleteCourse(Long id);
    List<Course> getCoursesByInstructor(Long instructorId);
}