package org.nevermined.worldevents.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.nevermined.worldevents.api.core.*;

import java.util.ArrayList;
import java.util.Collections;
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
        MiniMessage miniMessage = MiniMessage.miniMessage();

        for (String queueKey : config.getConfigurationSection("events").getKeys(false))
        {
            ConfigurationSection queueSection = config.getConfigurationSection("events." + queueKey);
            eventQueueMap.put(queueKey, new WorldEventQueue(new QueueData(
                    queueKey,
                    queueSection.contains("name")
                            ? miniMessage.deserialize(PlaceholderAPI.setPlaceholders(null, queueSection.getString("name")))
                            : Component.text(queueKey),
                    queueSection.contains("description")
                            ? queueSection.getStringList("description").stream()
                            .map(s -> PlaceholderAPI.setPlaceholders(null, s))
                            .map(miniMessage::deserialize)
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

}
