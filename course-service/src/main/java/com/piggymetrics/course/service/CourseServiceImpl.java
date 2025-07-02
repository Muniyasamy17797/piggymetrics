package com.piggymetrics.course.service;

import com.piggymetrics.course.config.RabbitMQConfig;
import com.piggymetrics.course.domain.Course;
import com.piggymetrics.course.event.CourseCreatedEvent;
import com.piggymetrics.course.repository.CourseRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository, RabbitTemplate rabbitTemplate) {
        this.courseRepository = courseRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    @Transactional
    public Course createCourse(Course course) {
        Course savedCourse = courseRepository.save(course);
        
        // Publish course created event
        CourseCreatedEvent event = new CourseCreatedEvent(savedCourse);
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.COURSE_EXCHANGE,
            RabbitMQConfig.COURSE_CREATED_ROUTING_KEY,
            event
        );
        
        return savedCourse;
    }

    @Override
    public Optional<Course> getCourse(Long id) {
        return courseRepository.findById(id);
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    @Transactional
    public Course updateCourse(Long id, Course course) {
        return courseRepository.findById(id)
            .map(existingCourse -> {
                existingCourse.setTitle(course.getTitle());
                existingCourse.setDescription(course.getDescription());
                existingCourse.setTopics(course.getTopics());
                return courseRepository.save(existingCourse);
            })
            .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
    }

    @Override
    @Transactional
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    @Override
    public List<Course> getCoursesByInstructor(Long instructorId) {
        return courseRepository.findByInstructorId(instructorId);
    }
}