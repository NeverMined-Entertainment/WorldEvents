package org.nevermined.worldevents.api.core;

public interface WorldEventQueueApi {

    WorldEventApi peekEvent();
    WorldEventApi pollEvent();

}
