package com.lms.assessment.service;

import com.lms.assessment.domain.Grade;
import com.lms.assessment.repository.GradeRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GradingServiceTest {

    @Mock
    private GradeRepository gradeRepository;

    @InjectMocks
    private GradingService gradingService;

    @Test
    public void whenSubmitGrade_thenReturnGrade() {
        Grade grade = new Grade();
        grade.setStudentId("student1");
        grade.setScore(85.0);
        
        when(gradeRepository.save(grade)).thenReturn(grade);
        
        Grade submitted = gradingService.submitGrade(grade);
        
        assertEquals(grade.getScore(), submitted.getScore(), 0.01);
        verify(gradeRepository).save(grade);
    }

    @Test
    public void whenGetStudentGrades_thenReturnGradeList() {
        Grade grade1 = new Grade();
        grade1.setStudentId("student1");
        Grade grade2 = new Grade();
        grade2.setStudentId("student1");
        List<Grade> grades = Arrays.asList(grade1, grade2);
        
        when(gradeRepository.findByStudentId("student1")).thenReturn(grades);
        
        List<Grade> found = gradingService.getStudentGrades("student1");
        
        assertEquals(2, found.size());
        verify(gradeRepository).findByStudentId("student1");
    }

    @Test
    public void whenGetStudentCourseGrades_thenReturnGradeList() {
        Grade grade1 = new Grade();
        grade1.setStudentId("student1");
        grade1.setCourseId("course1");
        Grade grade2 = new Grade();
        grade2.setStudentId("student1");
        grade2.setCourseId("course1");
        List<Grade> grades = Arrays.asList(grade1, grade2);
        
        when(gradeRepository.findByStudentIdAndCourseId("student1", "course1")).thenReturn(grades);
        
        List<Grade> found = gradingService.getStudentCourseGrades("student1", "course1");
        
        assertEquals(2, found.size());
        verify(gradeRepository).findByStudentIdAndCourseId("student1", "course1");
    }
}