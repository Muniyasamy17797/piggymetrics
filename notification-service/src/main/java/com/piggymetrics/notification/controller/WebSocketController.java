package com.piggymetrics.notification.controller;

import com.piggymetrics.notification.domain.Announcement;
import com.piggymetrics.notification.domain.ChatMessage;
import com.piggymetrics.notification.domain.QuizParticipation;
import com.piggymetrics.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private NotificationService notificationService;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setTimestamp(LocalDateTime.now());
        messagingTemplate.convertAndSend("/topic/chat." + chatMessage.getLessonId(), chatMessage);
        // Save chat message to database
        notificationService.saveChatMessage(chatMessage);
    }

    @MessageMapping("/chat.join")
    public void joinChat(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSenderName());
        headerAccessor.getSessionAttributes().put("lessonId", chatMessage.getLessonId());
        chatMessage.setType(ChatMessage.MessageType.JOIN);
        chatMessage.setTimestamp(LocalDateTime.now());
        messagingTemplate.convertAndSend("/topic/chat." + chatMessage.getLessonId(), chatMessage);
    }

    @MessageMapping("/quiz.answer")
    public void handleQuizAnswer(@Payload QuizParticipation participation) {
        participation.setTimestamp(LocalDateTime.now());
        // Process the answer and update isCorrect field
        notificationService.processQuizAnswer(participation);
        // Broadcast to all participants
        messagingTemplate.convertAndSend("/topic/quiz." + participation.getQuizId(), participation);
    }

    @MessageMapping("/announcement.send")
    public void sendAnnouncement(@Payload Announcement announcement) {
        announcement.setTimestamp(LocalDateTime.now());
        // Save announcement
        notificationService.saveAnnouncement(announcement);
        // Broadcast to course participants
        messagingTemplate.convertAndSend("/topic/announcements." + announcement.getCourseId(), announcement);
        // Send individual notifications to enrolled students
        notificationService.notifyStudents(announcement);
    }

    @MessageMapping("/progress.update")
    public void updateProgress(@Payload String progressUpdate) {
        // Handle progress updates and notify relevant users
        notificationService.handleProgressUpdate(progressUpdate);
    }
}