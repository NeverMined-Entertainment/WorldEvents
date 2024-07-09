package org.nevermined.worldevents.core;

import me.clip.placeholderapi.PlaceholderAPI;
import me.lucko.helper.promise.Promise;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.nevermined.worldevents.api.core.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class WorldEventQueue implements WorldEventQueueApi {

    private final List<String> RESERVED_CONFIG_NAMES = new ArrayList<>() {
        { add("name"); }
        { add("description"); }
        { add("item"); }
        { add("capacity"); }
    };
    
    private final QueueData queueData;

    private final Set<WorldEventSelfFactoryApi> eventList = new HashSet<>();
    private final Queue<WorldEventApi> eventQueue = new LinkedList<>();
    
    private int totalWeight = 0;

    private boolean isActive = false;
    private Promise<Void> eventCyclePromise;

    public WorldEventQueue(QueueData queueData, ConfigurationSection queueSection, Map<String, WorldEventAction> actionTypeMap)
    {
        this.queueData = queueData;
        loadEventList(queueSection, actionTypeMap);
        generateInitialQueue();
    }

    @Override
    public WorldEventApi peekEvent()
    {
        return eventQueue.peek();
    }

    @Override
    public WorldEventApi pollEvent()
    {
        WorldEventApi event = eventQueue.poll();
        eventQueue.add(selectRandomEvent());
        return event;
    }

    @Override
    public void startNext()
    {
        WorldEventApi event = peekEvent();
        event.startEvent(this);
        isActive = true;
        eventCyclePromise = event.getStopPromise().thenRunDelayedSync(() -> {
            pollEvent();
            startNext();
        }, event.getEventData().cooldownSeconds(), TimeUnit.SECONDS);
    }

    @Override
    public void stopCurrent()
    {
        WorldEventApi event = pollEvent();
        event.stopEvent(this);
        isActive = false;
        if (eventCyclePromise != null && !eventCyclePromise.isClosed())
            eventCyclePromise.closeSilently();
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public Queue<WorldEventApi> getEventQueue()
    {
        return eventQueue;
    }

    @Override
    public List<WorldEventApi> getEventQueueAsList() {
        return (LinkedList<WorldEventApi>)eventQueue;
    }

    @Override
    public QueueData getQueueData() {
        return queueData;
    }

    private void generateInitialQueue()
    {
        for (int i = 0; i < queueData.capacity(); i++)
        {
            eventQueue.add(selectRandomEvent());
        }
    }

    private WorldEventApi selectRandomEvent()
    {
        Random random = new Random();
        int randomValue = random.nextInt(totalWeight);
        int cumulativeWeight = 0;
        for (WorldEventSelfFactoryApi eventFactory : eventList)
        {
            cumulativeWeight += eventFactory.getEventData().chancePercent();
            if (cumulativeWeight >= randomValue)
                return eventFactory.create();
        }
        return null;
    }

    private void loadEventList(ConfigurationSection queueSection, Map<String, WorldEventAction> actionTypeMap)
    {
        MiniMessage miniMessage = MiniMessage.miniMessage();

        for (String eventKey : queueSection.getKeys(false))
        {
            if (RESERVED_CONFIG_NAMES.stream().anyMatch(reserved -> reserved.equalsIgnoreCase(eventKey)))
                continue;
            
            ConfigurationSection eventSection = queueSection.getConfigurationSection(eventKey);
            EventData eventData = new EventData(
                    miniMessage.deserialize(PlaceholderAPI.setPlaceholders(null, eventSection.getString("name"))),
                    eventSection.contains("description")
                            ? eventSection.getStringList("description").stream()
                            .map(s -> PlaceholderAPI.setPlaceholders(null, s))
                            .map(miniMessage::deserialize)
                            .toList()
                            : Collections.unmodifiableList(new ArrayList<>()),
                    eventSection.contains("item")
                            ? Material.getMaterial(eventSection.getString("item"))
                            : Material.BEACON,
                    eventSection.getInt("chance"),
                    eventSection.getLong("duration"),
                    eventSection.getLong("cooldown")
            );
            eventList.add(new WorldEventFactory(eventData, actionTypeMap.get(eventSection.getString("type"))));
            totalWeight += eventData.chancePercent();
        }
    }
}
