package com.piggymetrics.course.event;

import com.piggymetrics.common.event.Event;
import com.piggymetrics.course.domain.Course;

public class CourseCreatedEvent extends Event<Course> {
    private static final String TYPE = "COURSE_CREATED";

    public CourseCreatedEvent(Course course) {
        super(TYPE, course);
    }
}