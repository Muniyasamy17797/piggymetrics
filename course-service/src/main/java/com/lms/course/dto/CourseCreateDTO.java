package com.lms.course.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseCreateDTO {
    
    @NotBlank(message = "Course title cannot be empty")
    @Size(min = 3, max = 100, message = "Course title must be between 3 and 100 characters")
    private String title;
    
    @NotBlank(message = "Course description cannot be empty")
    @Size(min = 10, max = 5000, message = "Course description must be between 10 and 5000 characters")
    private String description;
    
    @NotEmpty(message = "Course must have at least one instructor")
    private List<Long> instructorIds = new ArrayList<>();
    
    private List<ModuleCreateDTO> modules = new ArrayList<>();
    
    @NotEmpty(message = "Course must belong to at least one category")
    private Set<Long> categoryIds = new HashSet<>();
    
    private Set<Long> tagIds = new HashSet<>();
    
    private String thumbnail;
    
    @Min(value = 1, message = "Course duration must be at least 1 minute")
    private Integer duration;
    
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must contain only lowercase letters, numbers and hyphens")
    private String slug;
}