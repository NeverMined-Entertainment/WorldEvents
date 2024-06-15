package org.nevermined.worldevents.api.core;

import java.util.List;
import java.util.Queue;

public interface WorldEventQueueApi {

    WorldEventApi peekEvent();
    WorldEventApi pollEvent();
    void startNext();
    void stopCurrent();
    Queue<WorldEventApi> getEventQueue();
    List<WorldEventApi> getEventQueueAsList();

}
