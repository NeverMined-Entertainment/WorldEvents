package org.nevermined.worldevents.core;

import me.lucko.helper.promise.Promise;
import org.nevermined.worldevents.api.core.*;

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
        WorldEventApi event = peekEvent();
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
            startNext();
        }, event.getEventData().cooldownSeconds(), TimeUnit.SECONDS);
    }

    @Override
    public void stopCurrent() {
        WorldEventApi event = pollEvent();
        event.stopEvent(this);
        isActive = false;
        if (eventCyclePromise != null && !eventCyclePromise.isClosed())
            eventCyclePromise.closeSilently();
    }

    @Override
    public void replaceEvent(int index, WorldEventApi event) {
        getEventQueueAsList().set(index, event);
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
