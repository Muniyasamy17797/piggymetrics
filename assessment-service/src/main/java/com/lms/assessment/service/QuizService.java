package com.lms.assessment.service;

import com.lms.assessment.domain.Quiz;
import com.lms.assessment.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;

    public Quiz createQuiz(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    public Optional<Quiz> getQuiz(String id) {
        return quizRepository.findById(id);
    }

    public List<Quiz> getQuizzesByCourse(String courseId) {
        return quizRepository.findByCourseId(courseId);
    }

    public Quiz updateQuiz(String id, Quiz quiz) {
        quiz.setId(id);
        return quizRepository.save(quiz);
    }

    public void deleteQuiz(String id) {
        quizRepository.deleteById(id);
    }
}