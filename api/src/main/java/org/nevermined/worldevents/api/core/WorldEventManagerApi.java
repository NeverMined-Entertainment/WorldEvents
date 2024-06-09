package org.nevermined.worldevents.api.core;

public interface WorldEventManagerApi {

    void startEventQueues();
    void startEventQueue(String queueKey);

}
