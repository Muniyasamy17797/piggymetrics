package com.lms.course.controller;

import com.lms.course.dto.CourseDTO;
import com.lms.course.dto.CourseCreateDTO;
import com.lms.course.model.CourseStatus;
import com.lms.course.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
@Validated
@Tag(name = "Course Management", description = "APIs for managing courses")
public class CourseController {

    private final CourseService courseService;

    @GetMapping("/{id}")
    @Operation(summary = "Get course by ID", description = "Retrieves a course by its unique identifier")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Course found"),
        @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public ResponseEntity<CourseDTO> getCourse(
            @Parameter(description = "Course ID", required = true)
            @PathVariable @NotNull Long id) {
        return ResponseEntity.ok(courseService.findById(id));
    }

    @GetMapping("/slug/{slug}")
    @Operation(summary = "Get course by slug", description = "Retrieves a course by its URL-friendly slug")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Course found"),
        @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public ResponseEntity<CourseDTO> getCourseBySlug(
            @Parameter(description = "Course slug", required = true)
            @PathVariable @NotBlank String slug) {
        return ResponseEntity.ok(courseService.findBySlug(slug));
    }

    @PostMapping
    @Operation(summary = "Create course", description = "Creates a new course")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Course created"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "409", description = "Course with same slug already exists")
    })
    public ResponseEntity<CourseDTO> createCourse(
            @Parameter(description = "Course creation data", required = true)
            @RequestBody @Valid CourseCreateDTO createDTO) {
        CourseDTO created = courseService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update course", description = "Updates an existing course")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Course updated"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Course not found"),
        @ApiResponse(responseCode = "409", description = "Course with same slug already exists")
    })
    public ResponseEntity<CourseDTO> updateCourse(
            @Parameter(description = "Course ID", required = true)
            @PathVariable @NotNull Long id,
            @Parameter(description = "Updated course data", required = true)
            @RequestBody @Valid CourseDTO courseDTO) {
        return ResponseEntity.ok(courseService.update(id, courseDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete course", description = "Deletes an existing course")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Course deleted"),
        @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public ResponseEntity<Void> deleteCourse(
            @Parameter(description = "Course ID", required = true)
            @PathVariable @NotNull Long id) {
        courseService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "List courses", description = "Retrieves a paginated list of courses")
    @ApiResponse(responseCode = "200", description = "Courses retrieved successfully")
    public ResponseEntity<Page<CourseDTO>> listCourses(
            @Parameter(description = "Pagination parameters")
            Pageable pageable) {
        return ResponseEntity.ok(courseService.findAll(pageable));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "List courses by status", description = "Retrieves courses filtered by their status")
    @ApiResponse(responseCode = "200", description = "Courses retrieved successfully")
    public ResponseEntity<Page<CourseDTO>> listCoursesByStatus(
            @Parameter(description = "Course status", required = true)
            @PathVariable @NotNull CourseStatus status,
            @Parameter(description = "Pagination parameters")
            Pageable pageable) {
        return ResponseEntity.ok(courseService.findByStatus(status, pageable));
    }

    @GetMapping("/instructor/{instructorId}")
    @Operation(summary = "List courses by instructor", description = "Retrieves courses taught by a specific instructor")
    @ApiResponse(responseCode = "200", description = "Courses retrieved successfully")
    public ResponseEntity<Page<CourseDTO>> listCoursesByInstructor(
            @Parameter(description = "Instructor ID", required = true)
            @PathVariable @NotNull Long instructorId,
            @Parameter(description = "Pagination parameters")
            Pageable pageable) {
        return ResponseEntity.ok(courseService.findByInstructor(instructorId, pageable));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "List courses by category", description = "Retrieves courses in a specific category")
    @ApiResponse(responseCode = "200", description = "Courses retrieved successfully")
    public ResponseEntity<Page<CourseDTO>> listCoursesByCategory(
            @Parameter(description = "Category ID", required = true)
            @PathVariable @NotNull Long categoryId,
            @Parameter(description = "Pagination parameters")
            Pageable pageable) {
        return ResponseEntity.ok(courseService.findByCategory(categoryId, pageable));
    }

    @GetMapping("/tags")
    @Operation(summary = "List courses by tags", description = "Retrieves courses that have all specified tags")
    @ApiResponse(responseCode = "200", description = "Courses retrieved successfully")
    public ResponseEntity<Page<CourseDTO>> listCoursesByTags(
            @Parameter(description = "Tag IDs", required = true)
            @RequestParam Set<Long> tagIds,
            @Parameter(description = "Pagination parameters")
            Pageable pageable) {
        return ResponseEntity.ok(courseService.findByTags(tagIds, pageable));
    }

    @GetMapping("/recent")
    @Operation(summary = "List recently updated courses", description = "Retrieves courses updated after a specific date")
    @ApiResponse(responseCode = "200", description = "Courses retrieved successfully")
    public ResponseEntity<List<CourseDTO>> listRecentlyUpdatedCourses(
            @Parameter(description = "Date threshold", required = true)
            @RequestParam @NotNull LocalDateTime since) {
        return ResponseEntity.ok(courseService.findRecentlyUpdated(since));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update course status", description = "Updates the status of an existing course")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Status updated"),
        @ApiResponse(responseCode = "400", description = "Invalid status transition"),
        @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public ResponseEntity<CourseDTO> updateCourseStatus(
            @Parameter(description = "Course ID", required = true)
            @PathVariable @NotNull Long id,
            @Parameter(description = "New status", required = true)
            @RequestParam @NotNull CourseStatus status) {
        return ResponseEntity.ok(courseService.updateStatus(id, status));
    }

    @PostMapping("/{id}/instructors/{instructorId}")
    @Operation(summary = "Add instructor to course", description = "Adds an instructor to an existing course")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Instructor added"),
        @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public ResponseEntity<CourseDTO> addInstructor(
            @Parameter(description = "Course ID", required = true)
            @PathVariable @NotNull Long id,
            @Parameter(description = "Instructor ID", required = true)
            @PathVariable @NotNull Long instructorId) {
        return ResponseEntity.ok(courseService.addInstructor(id, instructorId));
    }

    @DeleteMapping("/{id}/instructors/{instructorId}")
    @Operation(summary = "Remove instructor from course", description = "Removes an instructor from an existing course")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Instructor removed"),
        @ApiResponse(responseCode = "404", description = "Course not found")
    })
    public ResponseEntity<CourseDTO> removeInstructor(
            @Parameter(description = "Course ID", required = true)
            @PathVariable @NotNull Long id,
            @Parameter(description = "Instructor ID", required = true)
            @PathVariable @NotNull Long instructorId) {
        return ResponseEntity.ok(courseService.removeInstructor(id, instructorId));
    }

    @GetMapping("/count/{status}")
    @Operation(summary = "Count courses by status", description = "Counts the number of courses with a specific status")
    @ApiResponse(responseCode = "200", description = "Count retrieved successfully")
    public ResponseEntity<Long> countCoursesByStatus(
            @Parameter(description = "Course status", required = true)
            @PathVariable @NotNull CourseStatus status) {
        return ResponseEntity.ok(courseService.countByStatus(status));
    }

    @GetMapping("/exists/{slug}")
    @Operation(summary = "Check if slug exists", description = "Checks if a course with the given slug exists")
    @ApiResponse(responseCode = "200", description = "Check completed successfully")
    public ResponseEntity<Boolean> checkSlugExists(
            @Parameter(description = "Course slug", required = true)
            @PathVariable @NotBlank String slug) {
        return ResponseEntity.ok(courseService.existsBySlug(slug));
    }
}