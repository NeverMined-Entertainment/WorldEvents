package org.nevermined.worldevents.core;

import me.lucko.helper.Schedulers;
import me.lucko.helper.promise.Promise;
import org.nevermined.worldevents.api.core.EventData;
import org.nevermined.worldevents.api.core.WorldEventAction;
import org.nevermined.worldevents.api.core.WorldEventApi;
import org.nevermined.worldevents.api.core.WorldEventQueueApi;
import org.nevermined.worldevents.api.events.WorldEventFinish;
import org.nevermined.worldevents.api.events.WorldEventStart;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class WorldEvent implements WorldEventApi {

    private final EventData eventData;
    private final WorldEventAction action;

    private boolean isActive = false;
    private Instant expireTime;

    private Promise<Void> finishPromise;

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
    public void startEvent(WorldEventQueueApi queue)
    {
        WorldEventStart startEvent = new WorldEventStart(this, queue);
        startEvent.callEvent();
        if (startEvent.isCancelled())
            return;
        action.startEvent(eventData);
        expireTime = Instant.now().plusSeconds(eventData.durationSeconds());
        isActive = true;
        finishPromise = Schedulers.sync().runLater(() -> finishEvent(queue), eventData.durationSeconds(), TimeUnit.SECONDS);
    }

    @Override
    public void finishEvent(WorldEventQueueApi queue)
    {
        WorldEventFinish finishEvent = new WorldEventFinish(this, queue);
        finishEvent.callEvent();
        action.finishEvent(eventData);
        isActive = false;
        if (!finishPromise.isClosed())
            finishPromise.closeSilently();
        queue.pollEvent();
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
