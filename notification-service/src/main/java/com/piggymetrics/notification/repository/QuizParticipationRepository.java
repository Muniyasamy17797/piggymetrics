package com.piggymetrics.notification.repository;

import com.piggymetrics.notification.domain.QuizParticipation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizParticipationRepository extends MongoRepository<QuizParticipation, String> {
    List<QuizParticipation> findByQuizId(String quizId);
    List<QuizParticipation> findByUserId(String userId);
}