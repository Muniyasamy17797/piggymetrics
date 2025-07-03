package com.piggymetrics.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WebSocketMessageServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private WebSocketMessageService webSocketMessageService;

    @Before
    public void setUp() {
        webSocketMessageService = new WebSocketMessageService(messagingTemplate, redisTemplate, objectMapper);
    }

    @Test
    public void shouldBroadcastMessageAndPublishToRedis() {
        String message = "test message";

        webSocketMessageService.broadcastMessage(message);

        verify(messagingTemplate).convertAndSend("/topic/notifications", message);
        verify(redisTemplate).convertAndSend("notifications", message);
    }

    @Test
    public void shouldSendMessageToSpecificUser() {
        String username = "testUser";
        String message = "test message";

        webSocketMessageService.sendToUser(username, message);

        verify(messagingTemplate).convertAndSendToUser(
            username,
            "/queue/notifications",
            message
        );
    }

    @Test
    public void shouldHandleRabbitMessage() {
        String message = "test message";

        webSocketMessageService.handleRabbitMessage(message);

        verify(messagingTemplate).convertAndSend("/topic/notifications", message);
        verify(redisTemplate).convertAndSend("notifications", message);
    }
}