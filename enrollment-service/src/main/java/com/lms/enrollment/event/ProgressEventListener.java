package com.lms.enrollment.event;

import com.lms.enrollment.service.ProgressAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProgressEventListener {

    private final ProgressAnalyticsService progressAnalyticsService;

    @Async
    @EventListener
    public void handleProgressUpdateEvent(ProgressUpdateEvent event) {
        log.info("Received progress update event for enrollment {}", event.getEnrollmentId());
        
        // Update analytics asynchronously
        progressAnalyticsService.processProgressUpdate(
            event.getEnrollmentId(),
            event.getCourseId(),
            event.getProgress(),
            event.getScore()
        );
    }
}