package org.nevermined.worldevents.core;

import me.lucko.helper.promise.Promise;
import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.log.Log;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.nevermined.worldevents.api.core.*;
import org.nevermined.worldevents.api.core.exceptions.AlreadyActiveException;
import org.nevermined.worldevents.api.core.exceptions.AlreadyInactiveException;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class WorldEventQueue implements WorldEventQueueApi {

    private final List<String> RESERVED_CONFIG_NAMES = new ArrayList<>() {
        { add("name"); }
        { add("description"); }
        { add("item"); }
        { add("capacity"); }
    };
    
    protected final QueueData queueData;

    protected final Map<String, WorldEventSelfFactoryApi> eventSet = new HashMap<>();
    protected final Queue<WorldEventApi> eventQueue = new LinkedList<>();
    
    private int totalWeight = 0;

    protected boolean isActive = false;
    protected Promise<Void> eventCyclePromise;

    public WorldEventQueue(QueueData queueData, ConfigurationSection queueSection, Map<String, Supplier<WorldEventAction>> actionTypeMap)
    {
        this.queueData = queueData;
        loadEventSet(queueSection, actionTypeMap);
        generateInitialQueue();
    }

    protected WorldEventQueue(QueueData queueData)
    {
        this.queueData = queueData;
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
    public void startNext() throws AlreadyActiveException
    {
        if (isActive)
            throw new AlreadyActiveException("Queue " + queueData.key() + " is already active");
        startNextSilently();
    }

    protected void startNextSilently()
    {
        WorldEventApi event = peekEvent();
        setExpireTime();
        event.startEvent(this);
        isActive = true;
        eventCyclePromise = event.getStopPromise().thenRunDelayedSync(() -> {
            pollEvent();
            startNextSilently();
        }, event.getEventData().cooldownSeconds(), TimeUnit.SECONDS);
    }

    protected void setExpireTime()
    {
        WorldEventApi previousEvent = null;

        for (WorldEventApi event : eventQueue)
        {
            if (previousEvent == null)
                event.setExpireTime(Instant.now().plusSeconds(event.getEventData().durationSeconds()));
            else
                event.setExpireTime(previousEvent.getExpireTime().get()
                        .plusSeconds(previousEvent.getEventData().cooldownSeconds())
                        .plusSeconds(event.getEventData().durationSeconds()));
            previousEvent = event;
        }
    }

    @Override
    public void stopCurrent() throws AlreadyInactiveException
    {
        if (!isActive)
            throw new AlreadyInactiveException("Queue " + queueData.key() + " is already inactive");

        WorldEventApi event = pollEvent();
        removeExpireTime();
        event.stopEvent(this);
        isActive = false;
        if (eventCyclePromise != null && !eventCyclePromise.isClosed())
            eventCyclePromise.closeSilently();
        if (!event.getStopPromise().isClosed())
            event.getStopPromise().closeSilently();
    }

    protected void removeExpireTime()
    {
        eventQueue.forEach(event -> event.setExpireTime(null));
    }

    @Override
    public void replaceEvent(int index, WorldEventApi event) {
        getEventQueueAsList().set(index, event);
        setExpireTime();
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public Map<String, WorldEventSelfFactoryApi> getEventSet() {
        return eventSet;
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
        for (WorldEventSelfFactoryApi eventFactory : eventSet.values())
        {
            cumulativeWeight += eventFactory.getEventData().chancePercent();
            if (cumulativeWeight >= randomValue)
                return eventFactory.create();
        }
        return null;
    }

    private void loadEventSet(ConfigurationSection queueSection, Map<String, Supplier<WorldEventAction>> actionTypeMap)
    {
        for (String eventKey : queueSection.getKeys(false))
        {
            if (RESERVED_CONFIG_NAMES.stream().anyMatch(reserved -> reserved.equalsIgnoreCase(eventKey)))
                continue;
            if (!actionTypeMap.containsKey(queueSection.getConfigurationSection(eventKey).getString("type"))) {
                Log.global.warn("Loading event '" + eventKey + "' with type '" + queueSection.getConfigurationSection(eventKey).getString("type") + "' that wasn't registered yet.");
                Log.global.warn("It may still be registered by other plugins using WorldEvents api.");
            }
            
            ConfigurationSection eventSection = queueSection.getConfigurationSection(eventKey);
            EventData eventData = new EventData(
                    eventKey,
                    I18n.global.getLegacyPlaceholderComponent(null, null, eventSection.getString("name")),
                    eventSection.contains("description")
                            ? eventSection.getStringList("description").stream()
                            .map(s -> I18n.global.getLegacyPlaceholderComponent(null, null, s))
                            .toList()
                            : Collections.unmodifiableList(new ArrayList<>()),
                    eventSection.contains("item")
                            ? Material.getMaterial(eventSection.getString("item"))
                            : Material.BEACON,
                    eventSection.getInt("chance"),
                    eventSection.getLong("duration"),
                    eventSection.getLong("cooldown")
            );
            eventSet.put(eventKey, new WorldEventFactory(eventData, actionTypeMap.get(eventSection.getString("type"))));
            totalWeight += eventData.chancePercent();
        }
    }
}
