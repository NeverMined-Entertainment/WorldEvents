package org.nevermined.worldevents.api.core;

import java.util.Map;

public interface WorldEventManagerApi {

    void startEventQueues();
    void startEventQueue(String queueKey);
    Map<String, WorldEventApi> getCurrentEvents();
    Map<String, WorldEventQueueApi> getEventQueueMap();

}
