package org.nevermined.worldevents.api.core;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public interface WorldEventQueueApi {

    WorldEventApi peekEvent();
    WorldEventApi pollEvent();
    void startNext();
    void stopCurrent();

    void replaceEvent(int index, WorldEventApi event);

    boolean isActive();
    Map<String, WorldEventSelfFactoryApi> getEventSet();
    Queue<WorldEventApi> getEventQueue();
    List<WorldEventApi> getEventQueueAsList();
    QueueData getQueueData();

}
