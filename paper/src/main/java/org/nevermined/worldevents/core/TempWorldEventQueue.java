package org.nevermined.worldevents.core;

import me.lucko.helper.promise.Promise;
import org.nevermined.worldevents.api.core.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class TempWorldEventQueue extends WorldEventQueue {

    private final WorldEventManagerApi worldEventManager;

    public TempWorldEventQueue(WorldEventManagerApi worldEventManager, QueueData queueData, Map<String, WorldEventSelfFactoryApi> eventSet)
    {
        super(queueData);
        this.worldEventManager = worldEventManager;
        this.eventSet.putAll(eventSet);
        generateQueue();
    }

    @Override
    public WorldEventApi pollEvent() {
        return eventQueue.poll();
    }

    @Override
    protected void startNextSilently()
    {
        if (peekEvent() == null)
        {
            worldEventManager.getEventQueueMap().remove(queueData.key());
            if (eventCyclePromise != null && !eventCyclePromise.isClosed())
                eventCyclePromise.closeSilently();
            return;
        }

        WorldEventApi event = peekEvent();
        setExpireTime();
        if (!event.isActive())
            event.startEvent(this);
        isActive = true;
        eventCyclePromise = ((Promise<Void>) event.getStopPromise()).thenRunDelayedSync(() -> {
            pollEvent();
            startNextSilently();
        }, event.getEventData().cooldownSeconds(), TimeUnit.SECONDS);
    }

    private void generateQueue()
    {
        for (WorldEventSelfFactoryApi eventFactory : eventSet.values())
        {
            eventQueue.add(eventFactory.create());
        }
    }
}
