package com.lms.course.service;

import com.lms.course.model.*;
import com.lms.course.repository.*;
import com.lms.course.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseCategoryRepository categoryRepository;

    @Mock
    private CourseTagRepository tagRepository;

    @Mock
    private ContentVersionRepository contentVersionRepository;

    @InjectMocks
    private CourseServiceImpl courseService;

    private Course testCourse;
    private Module testModule;
    private Lesson testLesson;
    private CourseInstructor testInstructor;

    @BeforeEach
    void setUp() {
        testCourse = new Course();
        testCourse.setId(1L);
        testCourse.setTitle("Test Course");
        testCourse.setDescription("Test Description");
        testCourse.setStatus(CourseStatus.DRAFT);

        testModule = new Module();
        testModule.setId(1L);
        testModule.setTitle("Test Module");
        
        testLesson = new Lesson();
        testLesson.setId(1L);
        testLesson.setTitle("Test Lesson");
        
        testModule.getLessons().add(testLesson);
        testCourse.getModules().add(testModule);

        testInstructor = new CourseInstructor();
        testInstructor.setId(1L);
        testInstructor.setRole(InstructorRole.MAIN_INSTRUCTOR);
        testCourse.getInstructors().add(testInstructor);
    }

    @Test
    void createCourse_ValidCourse_ReturnsSavedCourse() {
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        Course savedCourse = courseService.createCourse(testCourse);

        assertNotNull(savedCourse);
        assertEquals("Test Course", savedCourse.getTitle());
        assertEquals(CourseStatus.DRAFT, savedCourse.getStatus());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void createCourse_InvalidCourse_ThrowsException() {
        Course invalidCourse = new Course();
        assertThrows(IllegalArgumentException.class, () -> courseService.createCourse(invalidCourse));
    }

    @Test
    void updateCourse_ExistingCourse_ReturnsUpdatedCourse() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        Course updatedCourse = courseService.updateCourse(1L, testCourse);

        assertNotNull(updatedCourse);
        assertEquals("Test Course", updatedCourse.getTitle());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void updateCourse_NonexistentCourse_ThrowsException() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courseService.updateCourse(1L, testCourse));
    }

    @Test
    void publishCourse_ValidCourse_ReturnsPublishedCourse() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        Course publishedCourse = courseService.publishCourse(1L);

        assertNotNull(publishedCourse);
        assertEquals(CourseStatus.PUBLISHED, publishedCourse.getStatus());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void publishCourse_CourseWithoutModules_ThrowsException() {
        testCourse.getModules().clear();
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));

        assertThrows(InvalidOperationException.class, () -> courseService.publishCourse(1L));
    }

    @Test
    void searchCourses_WithQuery_ReturnsMatchingCourses() {
        List<Course> courseList = new ArrayList<>();
        courseList.add(testCourse);
        Page<Course> coursePage = new PageImpl<>(courseList);
        Pageable pageable = PageRequest.of(0, 10);

        when(courseRepository.findByTitleContainingOrDescriptionContaining(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(coursePage);

        Page<Course> result = courseService.searchCourses("test", null, null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(courseRepository).findByTitleContainingOrDescriptionContaining(anyString(), anyString(), any(Pageable.class));
    }
}