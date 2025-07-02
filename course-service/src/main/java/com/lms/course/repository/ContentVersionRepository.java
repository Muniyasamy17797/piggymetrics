package com.lms.course.repository;

import com.lms.course.model.ContentVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContentVersionRepository extends JpaRepository<ContentVersion, Long> {
    List<ContentVersion> findByLessonIdOrderByVersionDesc(Long lessonId);
    Optional<ContentVersion> findByLessonIdAndVersion(Long lessonId, Long version);
}