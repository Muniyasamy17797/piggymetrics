package com.piggymetrics.notification.controller;

import com.piggymetrics.notification.domain.Announcement;
import com.piggymetrics.notification.domain.ChatMessage;
import com.piggymetrics.notification.domain.QuizParticipation;
import com.piggymetrics.notification.service.NotificationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WebSocketControllerTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private NotificationService notificationService;

    @Mock
    private SimpMessageHeaderAccessor headerAccessor;

    @InjectMocks
    private WebSocketController webSocketController;

    private Map<String, Object> sessionAttributes;

    @Before
    public void setup() {
        sessionAttributes = new HashMap<>();
        when(headerAccessor.getSessionAttributes()).thenReturn(sessionAttributes);
    }

    @Test
    public void shouldHandleChatMessage() {
        ChatMessage message = new ChatMessage();
        message.setLessonId("lesson123");
        message.setContent("Hello");
        message.setSenderName("John");

        webSocketController.sendMessage(message);

        verify(messagingTemplate).convertAndSend(eq("/topic/chat.lesson123"), any(ChatMessage.class));
        verify(notificationService).saveChatMessage(any(ChatMessage.class));
    }

    @Test
    public void shouldHandleQuizParticipation() {
        QuizParticipation participation = new QuizParticipation();
        participation.setQuizId("quiz123");
        participation.setUserId("user123");
        participation.setAnswer("42");

        webSocketController.handleQuizAnswer(participation);

        verify(notificationService).processQuizAnswer(any(QuizParticipation.class));
        verify(messagingTemplate).convertAndSend(eq("/topic/quiz.quiz123"), any(QuizParticipation.class));
    }

    @Test
    public void shouldHandleAnnouncement() {
        Announcement announcement = new Announcement();
        announcement.setCourseId("course123");
        announcement.setTitle("Important Update");
        announcement.setContent("Class cancelled");

        webSocketController.sendAnnouncement(announcement);

        verify(notificationService).saveAnnouncement(any(Announcement.class));
        verify(notificationService).notifyStudents(any(Announcement.class));
    }

    @Test
    public void shouldHandleUserJoiningChat() {
        ChatMessage joinMessage = new ChatMessage();
        joinMessage.setLessonId("lesson123");
        joinMessage.setSenderName("John");

        webSocketController.joinChat(joinMessage, headerAccessor);

        verify(messagingTemplate).convertAndSend(eq("/topic/chat.lesson123"), any(ChatMessage.class));
        assert sessionAttributes.get("username").equals("John");
        assert sessionAttributes.get("lessonId").equals("lesson123");
    }
}