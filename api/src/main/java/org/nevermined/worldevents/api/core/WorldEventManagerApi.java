package org.nevermined.worldevents.api.core;

import org.nevermined.worldevents.api.core.exceptions.AlreadyActiveException;
import org.nevermined.worldevents.api.core.exceptions.AlreadyInactiveException;

import java.util.Map;

public interface WorldEventManagerApi {

    void startEventQueues();
    void startEventQueue(String queueKey) throws AlreadyActiveException;
    void stopEventQueues();
    void stopEventQueue(String queueKey) throws AlreadyInactiveException;
    Map<String, WorldEventApi> getCurrentEvents();
    Map<String, WorldEventQueueApi> getEventQueueMap();

}
