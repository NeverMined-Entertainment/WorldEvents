package org.nevermined.worldevents.api.core;

public interface WorldEventAction {

    void startEvent(EventData eventData);
    void stopEvent(EventData eventData);

}
