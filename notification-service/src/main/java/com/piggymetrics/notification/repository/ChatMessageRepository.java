package com.piggymetrics.notification.repository;

import com.piggymetrics.notification.domain.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByLessonIdOrderByTimestampDesc(String lessonId);
}