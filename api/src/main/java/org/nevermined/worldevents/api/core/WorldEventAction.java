package org.nevermined.worldevents.api.core;

public interface WorldEventAction {

    void startEvent(WorldEventApi worldEvent, WorldEventQueueApi queue);
    void stopEvent(WorldEventApi worldEvent, WorldEventQueueApi queue);

}
