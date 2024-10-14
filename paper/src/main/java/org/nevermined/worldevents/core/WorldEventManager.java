package org.nevermined.worldevents.core;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.wyne.wutils.i18n.I18n;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.nevermined.worldevents.WorldEvents;
import org.nevermined.worldevents.api.core.*;
import org.nevermined.worldevents.api.core.exception.AlreadyActiveException;
import org.nevermined.worldevents.api.core.exception.AlreadyInactiveException;
import org.nevermined.worldevents.api.expansion.ExpansionData;
import org.nevermined.worldevents.api.expansion.ExpansionRegistryApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class WorldEventManager implements WorldEventManagerApi {

    private final Map<String, WorldEventQueueApi> eventQueueMap = new HashMap<>();

    private final WorldEvents plugin;
    private final ExpansionRegistryApi expansionRegistry;

    @Inject
    public WorldEventManager(WorldEvents plugin, ExpansionRegistryApi expansionRegistry) {
        this.plugin = plugin;
        this.expansionRegistry = expansionRegistry;
    }

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

    private void loadEventQueues(FileConfiguration config, Map<String, ExpansionData> registeredExpansions)
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
                    registeredExpansions));
        }
        clearEventQueues();
    }

    private void clearEventQueues()
    {
        eventQueueMap.keySet().stream()
                .filter(queueKey -> eventQueueMap.get(queueKey).getEventSet().isEmpty())
                .toList()
                .forEach(eventQueueMap::remove);
    }

    @Override
    public void reloadEventQueues() {
        stopEventQueues();
        loadEventQueues(plugin.getConfig(), expansionRegistry.getRegisteredExpansions());
    }
}
