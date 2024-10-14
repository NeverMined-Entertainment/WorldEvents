package org.nevermined.worldevents.api.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.nevermined.worldevents.api.core.WorldEventApi;
import org.nevermined.worldevents.api.core.WorldEventQueueApi;

public class WorldEventStart extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private WorldEventApi worldEvent;
    private WorldEventQueueApi worldEventQueue;
    private boolean cancelled;

    public WorldEventStart(WorldEventApi worldEvent, WorldEventQueueApi queue)
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

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public WorldEventApi getWorldEvent() {
        return worldEvent;
    }

    public WorldEventQueueApi getWorldEventQueue() {
        return worldEventQueue;
    }
}
