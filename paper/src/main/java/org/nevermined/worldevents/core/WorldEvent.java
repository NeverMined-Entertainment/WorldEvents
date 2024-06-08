package org.nevermined.worldevents.core;

import java.time.Instant;

public class WorldEvent {

    private final EventData eventData;

    private Instant expirationTime;

    public WorldEvent(EventData eventData)
    {
        this.eventData = eventData;
        this.expirationTime = Instant.now().plusSeconds(eventData.durationSeconds());
    }

    public EventData getEventData() {
        return eventData;
    }

    public Instant getExpirationTime() {
        return expirationTime;
    }
}
