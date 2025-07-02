package com.lms.course.mapper;

import com.lms.course.model.Course;
import com.lms.course.dto.CourseDTO;
import com.lms.course.dto.CourseCreateDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {ModuleMapper.class, CourseCategoryMapper.class, CourseTagMapper.class})
public interface CourseMapper {
    
    @Mapping(target = "instructors", ignore = true)
    CourseDTO toDto(Course course);
    
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "currentVersion", constant = "1L")
    Course toEntity(CourseCreateDTO createDTO);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    void updateEntityFromDto(CourseDTO dto, @MappingTarget Course course);
    
    @AfterMapping
    default void setInstructors(@MappingTarget CourseDTO dto, Course course) {
        dto.setInstructorIds(course.getInstructors().stream()
            .map(instructor -> instructor.getInstructor().getId())
            .collect(java.util.stream.Collectors.toList()));
    }
}