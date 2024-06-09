package org.nevermined.worldevents.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.configuration.file.FileConfiguration;
import org.nevermined.worldevents.api.core.WorldEventAction;
import org.nevermined.worldevents.api.core.WorldEventApi;
import org.nevermined.worldevents.api.core.WorldEventManagerApi;
import org.nevermined.worldevents.api.core.WorldEventQueueApi;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class WorldEventManager implements WorldEventManagerApi {

    private final Map<String, WorldEventQueueApi> eventQueueMap = new HashMap<>();

    @Inject
    public WorldEventManager(FileConfiguration config, Map<String, WorldEventAction> actionTypeMap)
    {
        loadEventQueues(config, actionTypeMap);
    }

    @Override
    public void startEventQueues()
    {
        eventQueueMap.keySet()
                .forEach(this::startEventQueue);
    }

    @Override
    public void startEventQueue(String queueKey)
    {
        eventQueueMap.get(queueKey)
                .startNext();
    }

    @Override
    public Map<String, WorldEventApi> getCurrentEvents()
    {
        Map<String, WorldEventApi> currentEvents = new HashMap<>();
        eventQueueMap
                .forEach((queueKey, queue) -> currentEvents.put(queueKey, queue.peekEvent()));
        return currentEvents;
    }

    @Override
    public Map<String, WorldEventQueueApi> getEventQueueMap()
    {
        return eventQueueMap;
    }

    private void loadEventQueues(FileConfiguration config, Map<String, WorldEventAction> actionTypeMap)
    {
        for (String queueKey : config.getConfigurationSection("events").getKeys(false))
        {
            eventQueueMap.put(queueKey, new WorldEventQueue(config.getConfigurationSection("events." + queueKey), actionTypeMap));
        }
    }

}
