package com.lms.course.repository;

import com.lms.course.model.Course;
import com.lms.course.model.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    Optional<Course> findBySlug(String slug);
    
    boolean existsBySlug(String slug);
    
    Page<Course> findByStatus(CourseStatus status, Pageable pageable);
    
    @Query("SELECT DISTINCT c FROM Course c JOIN c.instructors i WHERE i.instructorId = :instructorId")
    Page<Course> findByInstructorId(@Param("instructorId") Long instructorId, Pageable pageable);
    
    @Query("SELECT DISTINCT c FROM Course c JOIN c.categories cat WHERE cat.id = :categoryId")
    Page<Course> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);
    
    @Query("SELECT DISTINCT c FROM Course c JOIN c.tags t WHERE t.id IN :tagIds")
    Page<Course> findByTagIds(@Param("tagIds") Set<Long> tagIds, Pageable pageable);
    
    List<Course> findByUpdatedAtAfter(LocalDateTime since);
    
    long countByStatus(CourseStatus status);
    
    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.modules WHERE c.id = :id")
    Optional<Course> findByIdWithModules(@Param("id") Long id);
    
    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.instructors WHERE c.id = :id")
    Optional<Course> findByIdWithInstructors(@Param("id") Long id);
    
    @Query("SELECT DISTINCT c FROM Course c " +
           "LEFT JOIN FETCH c.modules m " +
           "LEFT JOIN FETCH c.instructors i " +
           "LEFT JOIN FETCH c.categories cat " +
           "LEFT JOIN FETCH c.tags t " +
           "WHERE c.id = :id")
    Optional<Course> findByIdWithAllRelations(@Param("id") Long id);
    
    @Query("SELECT COUNT(c) FROM Course c WHERE c.status = :status AND c.updatedAt < :lastUpdateTime")
    long countStaleCoursesWithStatus(
        @Param("status") CourseStatus status,
        @Param("lastUpdateTime") LocalDateTime lastUpdateTime
    );
    
    @Query("SELECT c FROM Course c WHERE c.duration <= :maxDuration AND c.status = :status")
    Page<Course> findShortCourses(
        @Param("maxDuration") Integer maxDuration,
        @Param("status") CourseStatus status,
        Pageable pageable
    );
}