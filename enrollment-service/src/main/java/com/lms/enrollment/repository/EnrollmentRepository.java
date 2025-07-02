package com.lms.enrollment.repository;

import com.lms.enrollment.model.Enrollment;
import com.lms.enrollment.model.EnrollmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    Optional<Enrollment> findByStudentIdAndCourseId(Long studentId, Long courseId);
    List<Enrollment> findByStudentId(Long studentId);
    Page<Enrollment> findByStudentId(Long studentId, Pageable pageable);
    List<Enrollment> findByCourseId(Long courseId);
    List<Enrollment> findByStudentIdAndStatus(Long studentId, EnrollmentStatus status);

    @Query("SELECT AVG(e.progress) FROM Enrollment e WHERE e.courseId = :courseId")
    Double getAverageCourseProgress(@Param("courseId") Long courseId);

    @Query("SELECT AVG(e.averageScore) FROM Enrollment e WHERE e.courseId = :courseId AND e.status = 'COMPLETED'")
    Double getAverageCourseScore(@Param("courseId") Long courseId);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.courseId = :courseId AND e.status = :status")
    Long countByCourseIdAndStatus(@Param("courseId") Long courseId, @Param("status") EnrollmentStatus status);

    @Query("SELECT new com.lms.enrollment.dto.CourseProgressStats(e.courseId, " +
           "COUNT(e), AVG(e.progress), AVG(e.averageScore)) " +
           "FROM Enrollment e WHERE e.courseId IN :courseIds GROUP BY e.courseId")
    List<CourseProgressStats> getCourseProgressStats(@Param("courseIds") List<Long> courseIds);
}