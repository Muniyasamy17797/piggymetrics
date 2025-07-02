package com.piggymetrics.course.service;

import com.piggymetrics.course.domain.Course;
import com.piggymetrics.course.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CourseServiceTest {

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
        course.setDescription("Test Description");

        Course savedCourse = new Course();
        savedCourse.setId(1L);
        savedCourse.setTitle("Test Course");
        savedCourse.setDescription("Test Description");

        when(courseRepository.save(any(Course.class))).thenReturn(savedCourse);

        // Act
        Course result = courseService.createCourse(course);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Course", result.getTitle());
        verify(courseRepository).save(any(Course.class));
        verify(rabbitTemplate).convertAndSend(anyString(), anyString(), any());
    }

    @Test
    void getCourse_WhenExists_ShouldReturnCourse() {
        // Arrange
        Course course = new Course();
        course.setId(1L);
        course.setTitle("Test Course");
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        // Act
        Optional<Course> result = courseService.getCourse(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Course", result.get().getTitle());
    }

    @Test
    void getCourse_WhenNotExists_ShouldReturnEmpty() {
        // Arrange
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<Course> result = courseService.getCourse(1L);

        // Assert
        assertFalse(result.isPresent());
    }
}