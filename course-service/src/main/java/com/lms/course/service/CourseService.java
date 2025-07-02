package com.lms.course.service;

import com.lms.course.dto.CourseDTO;
import com.lms.course.dto.CourseCreateDTO;
import com.lms.course.model.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Validated
public interface CourseService {
    
    CourseDTO findById(@NotNull Long id);
    
    CourseDTO findBySlug(@NotBlank String slug);
    
    CourseDTO create(@Valid CourseCreateDTO createDTO);
    
    CourseDTO update(@NotNull Long id, @Valid CourseDTO courseDTO);
    
    void delete(@NotNull Long id);
    
    Page<CourseDTO> findAll(@NotNull Pageable pageable);
    
    Page<CourseDTO> findByStatus(@NotNull CourseStatus status, @NotNull Pageable pageable);
    
    Page<CourseDTO> findByInstructor(@NotNull Long instructorId, @NotNull Pageable pageable);
    
    Page<CourseDTO> findByCategory(@NotNull Long categoryId, @NotNull Pageable pageable);
    
    Page<CourseDTO> findByTags(@NotEmpty Set<Long> tagIds, @NotNull Pageable pageable);
    
    List<CourseDTO> findRecentlyUpdated(@NotNull LocalDateTime since);
    
    CourseDTO updateStatus(@NotNull Long id, @NotNull CourseStatus newStatus);
    
    CourseDTO addInstructor(@NotNull Long courseId, @NotNull Long instructorId);
    
    CourseDTO removeInstructor(@NotNull Long courseId, @NotNull Long instructorId);
    
    long countByStatus(@NotNull CourseStatus status);
    
    boolean existsBySlug(@NotBlank String slug);
}