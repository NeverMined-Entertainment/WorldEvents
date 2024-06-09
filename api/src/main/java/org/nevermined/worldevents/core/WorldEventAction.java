package org.nevermined.worldevents.core;

public interface WorldEventAction {

    void startEvent(EventData eventData);
    void finishEvent(EventData eventData);

}
