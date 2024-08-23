package org.nevermined.worldevents.core;

import org.nevermined.worldevents.api.core.*;

import java.time.Instant;
import java.util.function.Supplier;

public class WorldEventFactory implements WorldEventSelfFactoryApi {

    private final EventData eventData;
    private final Supplier<WorldEventAction> worldEventActionSupplier;

    public WorldEventFactory(EventData eventData, Supplier<WorldEventAction> worldEventActionSupplier) {
        this.eventData = eventData;
        this.worldEventActionSupplier = worldEventActionSupplier;
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
        return create(eventData, worldEventActionSupplier.get());
    }

    @Override
    public WorldEventApi create(Instant expireTime)
    {
        return create(eventData, worldEventActionSupplier.get(), expireTime);
    }

    @Override
    public EventData getEventData() {
        return eventData;
    }

    @Override
    public WorldEventAction getAction() {
        return worldEventActionSupplier.get();
    }
}
