package com.piggymetrics.notification.service;

import com.piggymetrics.notification.domain.Announcement;
import com.piggymetrics.notification.domain.ChatMessage;
import com.piggymetrics.notification.domain.QuizParticipation;
import com.piggymetrics.notification.repository.AnnouncementRepository;
import com.piggymetrics.notification.repository.ChatMessageRepository;
import com.piggymetrics.notification.repository.QuizParticipationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private QuizParticipationRepository quizParticipationRepository;

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Override
    public void saveChatMessage(ChatMessage message) {
        chatMessageRepository.save(message);
    }

    @Override
    public void processQuizAnswer(QuizParticipation participation) {
        // TODO: Implement quiz answer validation logic
        quizParticipationRepository.save(participation);
        
        // Notify the student about their result
        messagingTemplate.convertAndSendToUser(
            participation.getUserId(),
            "/queue/quiz-results",
            participation
        );
    }

    @Override
    public void saveAnnouncement(Announcement announcement) {
        announcementRepository.save(announcement);
    }

    @Override
    public void notifyStudents(Announcement announcement) {
        // TODO: Get enrolled students for the course and send individual notifications
        // This would involve calling the enrollment-service to get the list of enrolled students
        
        // For now, we'll just broadcast to the course topic
        messagingTemplate.convertAndSend(
            "/topic/announcements." + announcement.getCourseId(),
            announcement
        );
    }

    @Override
    public void handleProgressUpdate(String progressUpdate) {
        // TODO: Parse progress update and notify relevant users
        // This would typically involve updating the student's progress
        // and notifying both the student and instructor
    }
}