package org.nevermined.worldevents.api.core;

import org.bukkit.configuration.file.FileConfiguration;
import org.nevermined.worldevents.api.core.exceptions.AlreadyActiveException;
import org.nevermined.worldevents.api.core.exceptions.AlreadyInactiveException;

import java.util.Map;

public interface WorldEventManagerApi {

    void startEventQueues();
    void startEventQueue(String queueKey) throws AlreadyActiveException;
    void stopEventQueues();
    void stopEventQueue(String queueKey) throws AlreadyInactiveException;
    void reloadEventQueues(FileConfiguration config);
    Map<String, WorldEventApi> getCurrentEvents();
    Map<String, WorldEventQueueApi> getEventQueueMap();

}
