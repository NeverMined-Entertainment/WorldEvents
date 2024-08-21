package org.nevermined.worldevents.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.wyne.wutils.i18n.I18n;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.nevermined.worldevents.api.core.*;
import org.nevermined.worldevents.api.core.exceptions.AlreadyActiveException;
import org.nevermined.worldevents.api.core.exceptions.AlreadyInactiveException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class WorldEventManager implements WorldEventManagerApi {

    private final Map<String, WorldEventQueueApi> eventQueueMap = new HashMap<>();

    @Override
    public void startEventQueues()
    {
        eventQueueMap.keySet()
                .stream()
                .filter(queueKey -> !eventQueueMap.get(queueKey).isActive())
                .forEach(this::startEventQueue);
    }

    @Override
    public void startEventQueue(String queueKey) throws AlreadyActiveException
    {
        eventQueueMap.get(queueKey)
                .startNext();
    }

    @Override
    public void stopEventQueues() {
        eventQueueMap.keySet()
                .stream()
                .filter(queueKey -> eventQueueMap.get(queueKey).isActive())
                .forEach(this::stopEventQueue);
    }

    @Override
    public void stopEventQueue(String queueKey) throws AlreadyInactiveException
    {
        eventQueueMap.get(queueKey)
                .stopCurrent();
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
            ConfigurationSection queueSection = config.getConfigurationSection("events." + queueKey);
            eventQueueMap.put(queueKey, new WorldEventQueue(new QueueData(
                    queueKey,
                    queueSection.contains("name")
                            ? I18n.global.getLegacyPlaceholderComponent(null, null, queueSection.getString("name"))
                            : Component.text(queueKey),
                    queueSection.contains("description")
                            ? queueSection.getStringList("description").stream()
                            .map(s -> I18n.global.getLegacyPlaceholderComponent(null, null, s))
                            .toList()
                            : Collections.unmodifiableList(new ArrayList<>()),
                    queueSection.contains("item")
                            ? Material.getMaterial(queueSection.getString("item"))
                            : Material.NETHER_STAR,
                    queueSection.getInt("capacity")
            ),
                    queueSection,
                    actionTypeMap));
        }
    }

    @Inject
    private void reloadEventQueues(FileConfiguration config, Map<String, WorldEventAction> actionTypeMap) {
        stopEventQueues();
        loadEventQueues(config, actionTypeMap);
    }
}
