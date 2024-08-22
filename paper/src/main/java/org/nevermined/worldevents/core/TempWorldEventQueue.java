package org.nevermined.worldevents.core;

import me.lucko.helper.promise.Promise;
import org.nevermined.worldevents.api.core.*;
import org.nevermined.worldevents.api.core.exceptions.AlreadyActiveException;
import org.nevermined.worldevents.api.core.exceptions.AlreadyInactiveException;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class TempWorldEventQueue implements WorldEventQueueApi {

    private final WorldEventManagerApi worldEventManager;
    private final QueueData queueData;

    private final Map<String, WorldEventSelfFactoryApi> eventSet;
    private final Queue<WorldEventApi> eventQueue = new LinkedList<>();

    private boolean isActive = false;
    private Promise<Void> eventCyclePromise;

    public TempWorldEventQueue(WorldEventManagerApi worldEventManager, QueueData queueData, Map<String, WorldEventSelfFactoryApi> eventSet)
    {
        this.worldEventManager = worldEventManager;
        this.queueData = queueData;
        this.eventSet = eventSet;
        generateQueue();
    }

    @Override
    public WorldEventApi peekEvent() {
        return eventQueue.peek();
    }

    @Override
    public WorldEventApi pollEvent() {
        return eventQueue.poll();
    }

    @Override
    public void startNext() {
        if (isActive)
            throw new AlreadyActiveException("Queue " + queueData.key() + " is already active");
        startNextSilently();
    }

    private void startNextSilently()
    {
        WorldEventApi event = peekEvent();
        setExpireTime();
        event.startEvent(this);
        isActive = true;
        eventCyclePromise = event.getStopPromise().thenRunDelayedSync(() -> {
            pollEvent();
            if (peekEvent() == null)
            {
                worldEventManager.getEventQueueMap().remove(queueData.key());
                if (eventCyclePromise != null && !eventCyclePromise.isClosed())
                    eventCyclePromise.closeSilently();
                return;
            }
            startNextSilently();
        }, event.getEventData().cooldownSeconds(), TimeUnit.SECONDS);
    }

    private void setExpireTime()
    {
        WorldEventApi previousEvent = null;

        for (WorldEventApi event : eventQueue)
        {
            if (previousEvent == null)
                event.setExpireTime(Instant.now().plusSeconds(event.getEventData().durationSeconds()));
            else
                event.setExpireTime(previousEvent.getExpireTime().get()
                        .plusSeconds(previousEvent.getEventData().cooldownSeconds())
                        .plusSeconds(event.getEventData().durationSeconds()));
            previousEvent = event;
        }
    }

    @Override
    public void stopCurrent() {
        if (!isActive)
            throw new AlreadyInactiveException("Queue " + queueData.key() + " is already inactive");

        WorldEventApi event = pollEvent();
        removeExpireTime();
        event.stopEvent(this);
        isActive = false;
        if (eventCyclePromise != null && !eventCyclePromise.isClosed())
            eventCyclePromise.closeSilently();
        if (!event.getStopPromise().isClosed())
            event.getStopPromise().closeSilently();
    }

    private void removeExpireTime()
    {
        eventQueue.forEach(event -> event.setExpireTime(null));
    }

    @Override
    public void replaceEvent(int index, WorldEventApi event) {
        getEventQueueAsList().set(index, event);
        setExpireTime();
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public Map<String, WorldEventSelfFactoryApi> getEventSet() {
        return eventSet;
    }

    @Override
    public Queue<WorldEventApi> getEventQueue()
    {
        return eventQueue;
    }

    @Override
    public List<WorldEventApi> getEventQueueAsList() {
        return (LinkedList<WorldEventApi>)eventQueue;
    }

    @Override
    public QueueData getQueueData() {
        return queueData;
    }

    private void generateQueue()
    {
        for (WorldEventSelfFactoryApi eventFactory : eventSet.values())
        {
            eventQueue.add(eventFactory.create());
        }
    }
}
