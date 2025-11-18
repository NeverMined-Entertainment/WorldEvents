package org.nevermined.worldevents.core;

import me.lucko.helper.Schedulers;
import me.lucko.helper.promise.Promise;
import org.jetbrains.annotations.Nullable;
import org.nevermined.worldevents.api.core.EventData;
import org.nevermined.worldevents.api.core.WorldEventAction;
import org.nevermined.worldevents.api.core.WorldEventApi;
import org.nevermined.worldevents.api.core.WorldEventQueueApi;
import org.nevermined.worldevents.api.core.exception.AlreadyActiveException;
import org.nevermined.worldevents.api.core.exception.AlreadyInactiveException;
import org.nevermined.worldevents.api.event.WorldEventStop;
import org.nevermined.worldevents.api.event.WorldEventStart;
import org.nevermined.worldevents.api.wrapper.PromiseWrapper;
import org.nevermined.worldevents.wrapper.PromiseWrapperImpl;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class WorldEvent implements WorldEventApi {

    private final EventData eventData;
    private final WorldEventAction action;

    private boolean isActive = false;
    private Instant startTime;
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
        isActive = true;
        stopPromise = Schedulers.sync().runLater(() -> {
            if (!isActive && !stopPromise.isClosed()) {
                stopPromise.closeSilently();
                return;
            }
            stopEvent(queue);
        }, eventData.durationSeconds(), TimeUnit.SECONDS);
        action.startEvent(this, queue);
    }

    @Override
    public void stopEvent(WorldEventQueueApi queue) throws AlreadyInactiveException
    {
        if (!isActive)
            throw new AlreadyInactiveException("Event " + eventData.key() + " is already inactive");

        WorldEventStop stopEvent = new WorldEventStop(this, queue);
        stopEvent.callEvent();
        action.stopEvent(this, queue);
        isActive = false;
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
    @Nullable
    public PromiseWrapper<Void> getStopPromise() {
        return stopPromise == null ? null : new PromiseWrapperImpl<>(stopPromise);
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public Optional<Instant> getStartTime() {
        return Optional.ofNullable(startTime);
    }

    @Override
    public Optional<Instant> getExpireTime() {
        return Optional.ofNullable(expireTime);
    }

    @Override
    public void setStartTime(@Nullable Instant startTime) {
        this.startTime = startTime;
    }

    @Override
    public void setExpireTime(@Nullable Instant expireTime) {
        this.expireTime = expireTime;
    }
}
