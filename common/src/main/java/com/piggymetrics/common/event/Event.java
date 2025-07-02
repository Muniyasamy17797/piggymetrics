package com.piggymetrics.common.event;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Event<T> {
    private final String eventId;
    private final LocalDateTime timestamp;
    private final String type;
    private final T payload;

    protected Event(String type, T payload) {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.type = type;
        this.payload = payload;
    }

    public String getEventId() {
        return eventId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public T getPayload() {
        return payload;
    }
}