package org.nevermined.worldevents.core;

import org.nevermined.worldevents.api.core.*;

import java.time.Instant;

public class WorldEventFactory implements WorldEventSelfFactoryApi {

    private final EventData eventData;
    private final WorldEventAction worldEventAction;

    public WorldEventFactory(EventData eventData, WorldEventAction worldEventAction) {
        this.eventData = eventData;
        this.worldEventAction = worldEventAction;
    }

    @Override
    public WorldEventApi create(EventData eventData, WorldEventAction worldEventAction)
    {
        return new WorldEvent(eventData, worldEventAction);
    }

    @Override
    public WorldEventApi create(EventData eventData, WorldEventAction worldEventAction, Instant expireTime) {
        return new WorldEvent(eventData, worldEventAction, expireTime);
    }

    @Override
    public WorldEventApi create()
    {
        return create(eventData, worldEventAction);
    }

    @Override
    public WorldEventApi create(Instant expireTime)
    {
        return create(eventData, worldEventAction, expireTime);
    }

    @Override
    public EventData getEventData() {
        return eventData;
    }

    @Override
    public WorldEventAction getAction() {
        return worldEventAction;
    }
}
