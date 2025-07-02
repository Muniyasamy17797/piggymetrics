package com.lms.assessment.service;

import com.lms.assessment.domain.Quiz;
import com.lms.assessment.repository.QuizRepository;
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
public class QuizServiceTest {

    @Mock
    private QuizRepository quizRepository;

    @InjectMocks
    private QuizService quizService;

    @Test
    public void whenCreateQuiz_thenReturnQuiz() {
        Quiz quiz = new Quiz();
        quiz.setTitle("Test Quiz");
        
        when(quizRepository.save(quiz)).thenReturn(quiz);
        
        Quiz created = quizService.createQuiz(quiz);
        
        assertEquals(quiz.getTitle(), created.getTitle());
        verify(quizRepository).save(quiz);
    }

    @Test
    public void whenGetQuiz_thenReturnQuiz() {
        Quiz quiz = new Quiz();
        quiz.setId("1");
        quiz.setTitle("Test Quiz");
        
        when(quizRepository.findById("1")).thenReturn(Optional.of(quiz));
        
        Optional<Quiz> found = quizService.getQuiz("1");
        
        assertEquals(quiz.getTitle(), found.get().getTitle());
        verify(quizRepository).findById("1");
    }

    @Test
    public void whenGetQuizzesByCourse_thenReturnQuizList() {
        Quiz quiz1 = new Quiz();
        quiz1.setCourseId("course1");
        Quiz quiz2 = new Quiz();
        quiz2.setCourseId("course1");
        List<Quiz> quizzes = Arrays.asList(quiz1, quiz2);
        
        when(quizRepository.findByCourseId("course1")).thenReturn(quizzes);
        
        List<Quiz> found = quizService.getQuizzesByCourse("course1");
        
        assertEquals(2, found.size());
        verify(quizRepository).findByCourseId("course1");
    }
}