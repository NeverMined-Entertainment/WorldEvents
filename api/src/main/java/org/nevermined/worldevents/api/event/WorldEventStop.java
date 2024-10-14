package org.nevermined.worldevents.api.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.nevermined.worldevents.api.core.WorldEventApi;
import org.nevermined.worldevents.api.core.WorldEventQueueApi;

public class WorldEventStop extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private WorldEventApi worldEvent;
    private WorldEventQueueApi worldEventQueue;

    public WorldEventStop(WorldEventApi worldEvent, WorldEventQueueApi queue)
    {
        this.worldEvent = worldEvent;
        this.worldEventQueue = queue;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public WorldEventApi getWorldEvent() {
        return worldEvent;
    }

    public WorldEventQueueApi getWorldEventQueue() {
        return worldEventQueue;
    }

}
