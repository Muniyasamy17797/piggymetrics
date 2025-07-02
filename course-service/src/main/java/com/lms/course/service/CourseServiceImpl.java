package com.lms.course.service;

import com.lms.course.dto.CourseDTO;
import com.lms.course.dto.CourseCreateDTO;
import com.lms.course.exception.CourseNotFoundException;
import com.lms.course.exception.DuplicateSlugException;
import com.lms.course.exception.InvalidStatusTransitionException;
import com.lms.course.mapper.CourseMapper;
import com.lms.course.model.Course;
import com.lms.course.model.CourseInstructor;
import com.lms.course.model.CourseStatus;
import com.lms.course.repository.CourseRepository;
import com.lms.course.repository.CourseInstructorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseInstructorRepository instructorRepository;
    private final CourseMapper courseMapper;

    @Override
    @Transactional(readOnly = true)
    public CourseDTO findById(@NotNull Long id) {
        log.debug("Finding course by id: {}", id);
        return courseRepository.findById(id)
                .map(courseMapper::toDto)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDTO findBySlug(@NotBlank String slug) {
        log.debug("Finding course by slug: {}", slug);
        return courseRepository.findBySlug(slug)
                .map(courseMapper::toDto)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with slug: " + slug));
    }

    @Override
    @Transactional
    public CourseDTO create(@Valid CourseCreateDTO createDTO) {
        log.debug("Creating new course with title: {}", createDTO.getTitle());
        
        if (courseRepository.existsBySlug(createDTO.getSlug())) {
            throw new DuplicateSlugException("Course with slug already exists: " + createDTO.getSlug());
        }

        Course course = courseMapper.toEntity(createDTO);
        course.setStatus(CourseStatus.DRAFT);
        
        Course savedCourse = courseRepository.save(course);
        
        // Add instructors
        createDTO.getInstructorIds().forEach(instructorId -> {
            CourseInstructor courseInstructor = new CourseInstructor();
            courseInstructor.setCourse(savedCourse);
            courseInstructor.setInstructorId(instructorId);
            instructorRepository.save(courseInstructor);
        });

        return courseMapper.toDto(savedCourse);
    }

    @Override
    @Transactional
    public CourseDTO update(@NotNull Long id, @Valid CourseDTO courseDTO) {
        log.debug("Updating course with id: {}", id);
        
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + id));

        if (!course.getSlug().equals(courseDTO.getSlug()) && 
            courseRepository.existsBySlug(courseDTO.getSlug())) {
            throw new DuplicateSlugException("Course with slug already exists: " + courseDTO.getSlug());
        }

        courseMapper.updateEntityFromDto(courseDTO, course);
        Course savedCourse = courseRepository.save(course);
        return courseMapper.toDto(savedCourse);
    }

    @Override
    @Transactional
    public void delete(@NotNull Long id) {
        log.debug("Deleting course with id: {}", id);
        
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + id));
        
        courseRepository.delete(course);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseDTO> findAll(@NotNull Pageable pageable) {
        log.debug("Finding all courses with pagination");
        return courseRepository.findAll(pageable).map(courseMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseDTO> findByStatus(@NotNull CourseStatus status, @NotNull Pageable pageable) {
        log.debug("Finding courses by status: {}", status);
        return courseRepository.findByStatus(status, pageable).map(courseMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseDTO> findByInstructor(@NotNull Long instructorId, @NotNull Pageable pageable) {
        log.debug("Finding courses by instructor id: {}", instructorId);
        return courseRepository.findByInstructorId(instructorId, pageable).map(courseMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseDTO> findByCategory(@NotNull Long categoryId, @NotNull Pageable pageable) {
        log.debug("Finding courses by category id: {}", categoryId);
        return courseRepository.findByCategoryId(categoryId, pageable).map(courseMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseDTO> findByTags(@NotNull Set<Long> tagIds, @NotNull Pageable pageable) {
        log.debug("Finding courses by tag ids: {}", tagIds);
        return courseRepository.findByTagIds(tagIds, pageable).map(courseMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> findRecentlyUpdated(@NotNull LocalDateTime since) {
        log.debug("Finding courses updated since: {}", since);
        return courseRepository.findByUpdatedAtAfter(since).stream()
                .map(courseMapper::toDto)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    @Transactional
    public CourseDTO updateStatus(@NotNull Long id, @NotNull CourseStatus newStatus) {
        log.debug("Updating status of course {} to {}", id, newStatus);
        
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + id));
        
        validateStatusTransition(course.getStatus(), newStatus);
        
        course.setStatus(newStatus);
        Course savedCourse = courseRepository.save(course);
        return courseMapper.toDto(savedCourse);
    }

    @Override
    @Transactional
    public CourseDTO addInstructor(@NotNull Long courseId, @NotNull Long instructorId) {
        log.debug("Adding instructor {} to course {}", instructorId, courseId);
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + courseId));
        
        if (instructorRepository.existsByCourseIdAndInstructorId(courseId, instructorId)) {
            return courseMapper.toDto(course);
        }
        
        CourseInstructor courseInstructor = new CourseInstructor();
        courseInstructor.setCourse(course);
        courseInstructor.setInstructorId(instructorId);
        instructorRepository.save(courseInstructor);
        
        return courseMapper.toDto(course);
    }

    @Override
    @Transactional
    public CourseDTO removeInstructor(@NotNull Long courseId, @NotNull Long instructorId) {
        log.debug("Removing instructor {} from course {}", instructorId, courseId);
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + courseId));
        
        instructorRepository.deleteByCourseIdAndInstructorId(courseId, instructorId);
        
        return courseMapper.toDto(course);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByStatus(@NotNull CourseStatus status) {
        log.debug("Counting courses with status: {}", status);
        return courseRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsBySlug(@NotBlank String slug) {
        log.debug("Checking if course exists with slug: {}", slug);
        return courseRepository.existsBySlug(slug);
    }

    private void validateStatusTransition(CourseStatus currentStatus, CourseStatus newStatus) {
        if (currentStatus == CourseStatus.PUBLISHED && newStatus == CourseStatus.DRAFT) {
            throw new InvalidStatusTransitionException("Cannot change status from PUBLISHED to DRAFT");
        }
        // Add more status transition validations as needed
    }
}