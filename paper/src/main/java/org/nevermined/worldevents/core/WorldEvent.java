package org.nevermined.worldevents.core;

import me.lucko.helper.Schedulers;
import me.lucko.helper.promise.Promise;
import org.nevermined.worldevents.api.core.EventData;
import org.nevermined.worldevents.api.core.WorldEventAction;
import org.nevermined.worldevents.api.core.WorldEventApi;
import org.nevermined.worldevents.api.core.WorldEventQueueApi;
import org.nevermined.worldevents.api.core.exceptions.AlreadyActiveException;
import org.nevermined.worldevents.api.core.exceptions.AlreadyInactiveException;
import org.nevermined.worldevents.api.events.WorldEventStop;
import org.nevermined.worldevents.api.events.WorldEventStart;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class WorldEvent implements WorldEventApi {

    private final EventData eventData;
    private final WorldEventAction action;

    private boolean isActive = false;
    private Instant expireTime;

    private Promise<Void> stopPromise;

    public WorldEvent(EventData eventData, WorldEventAction action)
    {
        this.eventData = eventData;
        this.action = action;
    }

    public WorldEvent(EventData eventData, WorldEventAction action, Instant expireTime)
    {
        this(eventData, action);
        this.expireTime = expireTime;
    }

    @Override
    public void startEvent(WorldEventQueueApi queue) throws AlreadyActiveException
    {
        if (isActive)
            throw new AlreadyActiveException("Event " + eventData.key() + " is already active");

        WorldEventStart startEvent = new WorldEventStart(this, queue);
        startEvent.callEvent();
        if (startEvent.isCancelled())
            return;
        action.startEvent(eventData);
        expireTime = Instant.now().plusSeconds(eventData.durationSeconds());
        isActive = true;
        stopPromise = Schedulers.sync().runLater(() -> stopEvent(queue), eventData.durationSeconds(), TimeUnit.SECONDS);
    }

    @Override
    public void stopEvent(WorldEventQueueApi queue) throws AlreadyInactiveException
    {
        if (!isActive)
            throw new AlreadyInactiveException("Event " + eventData.key() + " is already inactive");

        WorldEventStop stopEvent = new WorldEventStop(this, queue);
        stopEvent.callEvent();
        action.stopEvent(eventData);
        isActive = false;
        if (stopPromise != null && !stopPromise.isClosed())
            stopPromise.closeSilently();
    }

    @Override
    public EventData getEventData() {
        return eventData;
    }

    @Override
    public WorldEventAction getAction() {
        return action;
    }

    @Override
    public Promise<Void> getStopPromise() {
        return stopPromise;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public Optional<Instant> getExpireTime() {
        return Optional.ofNullable(expireTime);
    }

    @Override
    public void setExpireTime(Instant expireTime) {
        this.expireTime = expireTime;
    }
}
