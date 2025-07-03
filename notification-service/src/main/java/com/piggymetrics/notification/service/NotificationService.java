package com.piggymetrics.notification.service;

import com.piggymetrics.notification.domain.Announcement;
import com.piggymetrics.notification.domain.ChatMessage;
import com.piggymetrics.notification.domain.QuizParticipation;

public interface NotificationService {
    void saveChatMessage(ChatMessage message);
    
    void processQuizAnswer(QuizParticipation participation);
    
    void saveAnnouncement(Announcement announcement);
    
    void notifyStudents(Announcement announcement);
    
    void handleProgressUpdate(String progressUpdate);
}