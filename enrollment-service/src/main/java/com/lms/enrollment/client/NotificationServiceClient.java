package com.lms.enrollment.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class NotificationServiceClient {
    private final WebClient notificationWebClient;

    public Mono<Void> sendNotification(Long userId, String title, String message) {
        return notificationWebClient.post()
            .uri("/api/notifications")
            .bodyValue(new NotificationRequest(userId, title, message))
            .retrieve()
            .bodyToMono(Void.class);
    }

    @Data
    @AllArgsConstructor
    private static class NotificationRequest {
        private Long userId;
        private String title;
        private String message;
    }
}