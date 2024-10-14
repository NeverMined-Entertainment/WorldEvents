package org.nevermined.worldevents.api.core;

import org.nevermined.worldevents.api.core.exception.AlreadyActiveException;
import org.nevermined.worldevents.api.core.exception.AlreadyInactiveException;

import java.util.Map;

public interface WorldEventManagerApi {

    void startEventQueues();
    void startEventQueue(String queueKey) throws AlreadyActiveException;
    void stopEventQueues();
    void stopEventQueue(String queueKey) throws AlreadyInactiveException;
    void reloadEventQueues();

    /**
     * @return Key - Queue Key, Value - World event
     */
    Map<String, WorldEventApi> getCurrentEvents();
    /**
     * @return Key - Queue Key, Value - World event queue
     */
    Map<String, WorldEventQueueApi> getEventQueueMap();

}
