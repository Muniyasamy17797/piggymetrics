package com.piggymetrics.course.service;

import com.piggymetrics.course.domain.Course;
import com.piggymetrics.course.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    private CourseService courseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        courseService = new CourseServiceImpl(courseRepository, rabbitTemplate);
    }

    @Test
    void createCourse_ShouldSaveAndPublishEvent() {
        // Arrange
        Course course = new Course();
        course.setTitle("Test Course");
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        // Act
        Course result = courseService.createCourse(course);

        // Assert
        assertNotNull(result);
        assertEquals("Test Course", result.getTitle());
        verify(courseRepository).save(course);
        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), any());
    }

    @Test
    void getCourse_WhenExists_ShouldReturnCourse() {
        // Arrange
        Course course = new Course();
        course.setId(1L);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        // Act
        Optional<Course> result = courseService.getCourse(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void getAllCourses_ShouldReturnList() {
        // Arrange
        List<Course> courses = Arrays.asList(new Course(), new Course());
        when(courseRepository.findAll()).thenReturn(courses);

        // Act
        List<Course> result = courseService.getAllCourses();

        // Assert
        assertEquals(2, result.size());
        verify(courseRepository).findAll();
    }

    @Test
    void updateCourse_WhenExists_ShouldUpdate() {
        // Arrange
        Course existingCourse = new Course();
        existingCourse.setId(1L);
        existingCourse.setTitle("Old Title");

        Course updatedCourse = new Course();
        updatedCourse.setTitle("New Title");

        when(courseRepository.findById(1L)).thenReturn(Optional.of(existingCourse));
        when(courseRepository.save(any(Course.class))).thenReturn(updatedCourse);

        // Act
        Course result = courseService.updateCourse(1L, updatedCourse);

        // Assert
        assertEquals("New Title", result.getTitle());
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    void deleteCourse_ShouldDelete() {
        // Arrange
        Long courseId = 1L;

        // Act
        courseService.deleteCourse(courseId);

        // Assert
        verify(courseRepository).deleteById(courseId);
    }
}