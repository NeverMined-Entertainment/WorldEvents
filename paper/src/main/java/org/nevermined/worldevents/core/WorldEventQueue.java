package org.nevermined.worldevents.core;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.ConfigurationSection;
import org.nevermined.worldevents.api.core.EventData;
import org.nevermined.worldevents.api.core.WorldEventApi;
import org.nevermined.worldevents.api.core.WorldEventAction;
import org.nevermined.worldevents.api.core.WorldEventQueueApi;

import java.util.*;

public class WorldEventQueue implements WorldEventQueueApi {

    private final Set<WorldEventApi> eventList = new HashSet<>();
    private final Queue<WorldEventApi> eventQueue = new LinkedList<>();

    private final int queueCapacity;
    private int totalWeight = 0;

    public WorldEventQueue(ConfigurationSection configSection, Map<String, WorldEventAction> actionTypeMap)
    {
        queueCapacity = configSection.getInt("queue-capacity");
        loadEventList(configSection, actionTypeMap);
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
        peekEvent().startEvent(this);
        return event;
    }

    private void generateInitialQueue()
    {
        for (int i = 0; i < queueCapacity; i++)
        {
            eventQueue.add(selectRandomEvent());
        }
    }

    private WorldEventApi selectRandomEvent()
    {
        Random random = new Random();
        int randomValue = random.nextInt(totalWeight);
        int cumulativeWeight = 0;
        for (WorldEventApi event : eventList)
        {
            cumulativeWeight += event.getEventData().chancePercent();
            if (randomValue >= cumulativeWeight)
                return event;
        }
        return null;
    }

    private void loadEventList(ConfigurationSection configSection, Map<String, WorldEventAction> actionTypeMap)
    {
        MiniMessage miniMessage = MiniMessage.miniMessage();

        for (String eventKey : configSection.getKeys(false))
        {
            if (eventKey.equalsIgnoreCase("queue-capacity"))
                continue;

            EventData eventData = new EventData(
                    miniMessage.deserialize(PlaceholderAPI.setPlaceholders(null, configSection.getString(eventKey + ".name"))),
                    configSection.getStringList(eventKey + ".description").stream()
                            .map(s -> PlaceholderAPI.setPlaceholders(null, s))
                            .map(miniMessage::deserialize)
                            .toList(),
                    configSection.getInt(eventKey + ".chance"),
                    configSection.getLong(eventKey + ".duration"),
                    configSection.getLong(eventKey + ".cooldown")
            );
            eventList.add(new WorldEvent(eventData, actionTypeMap.get(configSection.getString(eventKey + ".type"))));
            totalWeight += eventData.chancePercent();
        }
    }

}
