package org.nevermined.worldevents.api.core;

import org.nevermined.worldevents.api.core.exceptions.AlreadyActiveException;
import org.nevermined.worldevents.api.core.exceptions.AlreadyInactiveException;

import java.util.List;
import java.util.Map;
import java.util.Queue;

public interface WorldEventQueueApi {

    WorldEventApi peekEvent();
    WorldEventApi pollEvent();
    void startNext() throws AlreadyActiveException;
    void stopCurrent() throws AlreadyInactiveException;

    void queueEvent(WorldEventApi event);
    WorldEventApi removeEvent(int index);
    void replaceEvent(int index, WorldEventApi event);

    boolean isActive();
    Map<String, WorldEventSelfFactoryApi> getEventSet();
    Queue<WorldEventApi> getEventQueue();
    List<WorldEventApi> getEventQueueAsList();
    QueueData getQueueData();

}
