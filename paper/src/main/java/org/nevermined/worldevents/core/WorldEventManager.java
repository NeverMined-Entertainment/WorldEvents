package org.nevermined.worldevents.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.configuration.file.FileConfiguration;
import org.nevermined.worldevents.api.core.WorldEventAction;
import org.nevermined.worldevents.api.core.WorldEventManagerApi;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class WorldEventManager implements WorldEventManagerApi {

    private final Map<String, WorldEventQueue> eventQueueMap = new HashMap<>();

    @Inject
    public WorldEventManager(FileConfiguration config, Map<String, WorldEventAction> actionTypeMap)
    {
        loadEventQueues(config, actionTypeMap);
    }

    public void startEventQueues()
    {
        eventQueueMap.keySet()
                .forEach(this::startEventQueue);
    }

    public void startEventQueue(String queueKey)
    {
        eventQueueMap.get(queueKey)
                .peekEvent()
                .startEvent(eventQueueMap.get(queueKey));
    }

    private void loadEventQueues(FileConfiguration config, Map<String, WorldEventAction> actionTypeMap)
    {
        for (String queueKey : config.getConfigurationSection("events").getKeys(false))
        {
            eventQueueMap.put(queueKey, new WorldEventQueue(config.getConfigurationSection("events." + queueKey), actionTypeMap));
        }
    }

}
