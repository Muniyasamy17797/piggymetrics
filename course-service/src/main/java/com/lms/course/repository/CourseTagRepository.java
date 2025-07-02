package com.lms.course.repository;

import com.lms.course.model.CourseTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface CourseTagRepository extends JpaRepository<CourseTag, Long> {
    Optional<CourseTag> findByName(String name);
    Set<CourseTag> findByNameIn(Set<String> names);
}