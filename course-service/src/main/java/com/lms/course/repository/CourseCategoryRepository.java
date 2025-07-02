package com.lms.course.repository;

import com.lms.course.model.CourseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseCategoryRepository extends JpaRepository<CourseCategory, Long> {
    Optional<CourseCategory> findBySlug(String slug);
    Optional<CourseCategory> findByName(String name);
    List<CourseCategory> findByParentIsNull();
    
    @Query("SELECT c FROM CourseCategory c WHERE c.parent.id = :parentId")
    List<CourseCategory> findSubcategories(Long parentId);
}