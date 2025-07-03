package com.piggymetrics.notification.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class WebSocketMessageService {

    private static final Logger log = LoggerFactory.getLogger(WebSocketMessageService.class);
    private static final String REDIS_NOTIFICATION_TOPIC = "notifications";

    private final SimpMessagingTemplate messagingTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public WebSocketMessageService(
            SimpMessagingTemplate messagingTemplate,
            RedisTemplate<String, Object> redisTemplate,
            ObjectMapper objectMapper) {
        this.messagingTemplate = messagingTemplate;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Listen for messages from RabbitMQ and broadcast them via WebSocket
     */
    @RabbitListener(queues = "${notification.rabbitmq.queue}")
    public void handleRabbitMessage(String message) {
        try {
            // Convert message to appropriate format if needed
            broadcastMessage(message);
        } catch (Exception e) {
            log.error("Error processing RabbitMQ message: {}", e.getMessage());
        }
    }

    /**
     * Publish message to Redis for other instances
     */
    public void publishToRedis(String message) {
        try {
            redisTemplate.convertAndSend(REDIS_NOTIFICATION_TOPIC, message);
        } catch (Exception e) {
            log.error("Error publishing to Redis: {}", e.getMessage());
        }
    }

    /**
     * Broadcast message to all connected WebSocket clients
     */
    public void broadcastMessage(String message) {
        try {
            // Broadcast to all subscribers
            messagingTemplate.convertAndSend("/topic/notifications", message);
            // Also publish to Redis for other instances
            publishToRedis(message);
        } catch (Exception e) {
            log.error("Error broadcasting message: {}", e.getMessage());
        }
    }

    /**
     * Send message to specific user
     */
    public void sendToUser(String username, String message) {
        try {
            messagingTemplate.convertAndSendToUser(
                username,
                "/queue/notifications",
                message
            );
        } catch (Exception e) {
            log.error("Error sending message to user {}: {}", username, e.getMessage());
        }
    }
}