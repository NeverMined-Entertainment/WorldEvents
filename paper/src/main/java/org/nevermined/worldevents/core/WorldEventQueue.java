package org.nevermined.worldevents.core;

import me.clip.placeholderapi.PlaceholderAPI;
import me.lucko.helper.promise.Promise;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.ConfigurationSection;
import org.nevermined.worldevents.api.core.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class WorldEventQueue implements WorldEventQueueApi {

    private final Set<WorldEventSelfFactoryApi> eventList = new HashSet<>();
    private final Queue<WorldEventApi> eventQueue = new LinkedList<>();

    private final int queueCapacity;
    private int totalWeight = 0;

    private Promise<Void> eventCyclePromise;

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
        return event;
    }

    @Override
    public void startNext()
    {
        WorldEventApi event = peekEvent();
        event.startEvent(this);
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
        if (eventCyclePromise != null && !eventCyclePromise.isClosed())
            eventCyclePromise.closeSilently();
    }

    @Override
    public Queue<WorldEventApi> getEventQueue()
    {
        return eventQueue;
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
        for (WorldEventSelfFactoryApi eventFactory : eventList)
        {
            cumulativeWeight += eventFactory.getEventData().chancePercent();
            if (cumulativeWeight >= randomValue)
                return eventFactory.create();
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
                    configSection.contains(eventKey + ".description") ? configSection.getStringList(eventKey + ".description").stream()
                            .map(s -> PlaceholderAPI.setPlaceholders(null, s))
                            .map(miniMessage::deserialize)
                            .toList() : new ArrayList<>(),
                    configSection.getInt(eventKey + ".chance"),
                    configSection.getLong(eventKey + ".duration"),
                    configSection.getLong(eventKey + ".cooldown")
            );
            eventList.add(new WorldEventFactory(eventData, actionTypeMap.get(configSection.getString(eventKey + ".type"))));
            totalWeight += eventData.chancePercent();
        }
    }

}
