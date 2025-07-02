package com.lms.assessment.service;

import com.lms.assessment.domain.Assignment;
import com.lms.assessment.repository.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;

    public Assignment createAssignment(Assignment assignment) {
        return assignmentRepository.save(assignment);
    }

    public Optional<Assignment> getAssignment(String id) {
        return assignmentRepository.findById(id);
    }

    public List<Assignment> getAssignmentsByCourse(String courseId) {
        return assignmentRepository.findByCourseId(courseId);
    }

    public Assignment updateAssignment(String id, Assignment assignment) {
        assignment.setId(id);
        return assignmentRepository.save(assignment);
    }

    public void deleteAssignment(String id) {
        assignmentRepository.deleteById(id);
    }
}