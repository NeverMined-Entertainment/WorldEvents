package org.nevermined.worldevents.api.core;

public interface WorldEventQueueApi {

    WorldEventApi peekEvent();
    WorldEventApi pollEvent();
    void startNext();
    void stopCurrent();

}
