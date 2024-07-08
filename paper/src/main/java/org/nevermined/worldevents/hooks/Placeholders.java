package org.nevermined.worldevents.hooks;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.wyne.wutils.i18n.I18n;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nevermined.worldevents.WorldEvents;
import org.nevermined.worldevents.api.core.QueueData;
import org.nevermined.worldevents.api.core.WorldEventApi;
import org.nevermined.worldevents.api.core.WorldEventManagerApi;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;

@Singleton
public class Placeholders extends PlaceholderExpansion {

    private final WorldEvents plugin;
    private final WorldEventManagerApi worldEventManager;

    // Key - data type
    // Value - BiFunction<Queue key, Event queue index, data>
    private final Map<String, BiFunction<String, Integer, String>> eventDataParserMap = new HashMap<>();
    // Key - data type
    // Value - Function<Queue key, data>
    private final Map<String, Function<String, String>> queueDataParserMap = new HashMap<>();

    @Inject
    public Placeholders(WorldEvents plugin, WorldEventManagerApi worldEventManager)
    {
        this.plugin = plugin;
        this.worldEventManager = worldEventManager;
        createEventNameParser();
        createQueueNameParser();
        createEventDescriptionParser();
        createQueueDescriptionParser();
        createEventItemParser();
        createQueueItemParser();
        createEventChanceParser();
        createEventDurationParser();
        createEventExpireParser();
        createEventCooldownParser();
        createEventActiveParser();
        createQueueActiveParser();
        createQueueCapacityParser();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "worldevents";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Wyne";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.startsWith("event"))
        {
            // Key word/Queue key/index/data type
            // event_  demo-queue-1_0  _name

            String[] args = params.split("_");
            if (args[2].matches("^\\d+$"))
                return eventDataParserMap.get(args[3]).apply(args[1], Integer.valueOf(args[2]));
            else
            {
                if (args[2].equalsIgnoreCase("current"))
                    return eventDataParserMap.get(args[3]).apply(args[1], 0);
                if (args[2].equalsIgnoreCase("next"))
                    return eventDataParserMap.get(args[3]).apply(args[1], 1);
                if (args[2].equalsIgnoreCase("last"))
                    return eventDataParserMap.get(args[3]).apply(args[1], worldEventManager.getEventQueueMap().get(args[1]).getEventQueue().size() - 1);
            }
        }

        if (params.startsWith("queue"))
        {
            // Key word/Queue key/data type
            // queue_  demo-queue-1  _name

            String[] args = params.split("_");
            return queueDataParserMap.get(args[2]).apply(args[1]);
        }

        return null;
    }

    private void createEventNameParser()
    {
        eventDataParserMap.put("name", (queueKey, eventIndex) -> {
            List<WorldEventApi> queue = worldEventManager.getEventQueueMap().get(queueKey).getEventQueueAsList();
            return LegacyComponentSerializer.legacyAmpersand().serialize(queue.get(eventIndex >= queue.size() ? queue.size() - 1 : eventIndex).getEventData().name());
        });
    }

    private void createQueueNameParser()
    {
        queueDataParserMap.put("name", queueKey -> {
            QueueData queueData = worldEventManager.getEventQueueMap().get(queueKey).getQueueData();
            return LegacyComponentSerializer.legacyAmpersand().serialize(queueData.name());
        });
    }

    private void createEventDescriptionParser()
    {
        eventDataParserMap.put("description", (queueKey, eventIndex) -> {
            List<WorldEventApi> queue = worldEventManager.getEventQueueMap().get(queueKey).getEventQueueAsList();
            return queue.get(eventIndex >= queue.size() ? queue.size() - 1 : eventIndex).getEventData().description()
                    .stream()
                    .map(component -> LegacyComponentSerializer.legacyAmpersand().serialize(component))
                    .reduce("", (s, s2) -> s + "\n" + s2);
        });
    }

    private void createQueueDescriptionParser()
    {
        queueDataParserMap.put("description", queueKey -> {
            QueueData queueData = worldEventManager.getEventQueueMap().get(queueKey).getQueueData();
            return queueData.description()
                    .stream()
                    .map(component -> LegacyComponentSerializer.legacyAmpersand().serialize(component))
                    .reduce("", (s, s2) -> s + "\n" + s2);
        });
    }

    private void createEventItemParser()
    {
        eventDataParserMap.put("item", (queueKey, eventIndex) -> {
            List<WorldEventApi> queue = worldEventManager.getEventQueueMap().get(queueKey).getEventQueueAsList();
            return queue.get(eventIndex >= queue.size() ? queue.size() - 1 : eventIndex).getEventData().item().toString();
        });
    }

    private void createQueueItemParser()
    {
        queueDataParserMap.put("item", queueKey -> {
            QueueData queueData = worldEventManager.getEventQueueMap().get(queueKey).getQueueData();
            return queueData.item().toString();
        });
    }

    private void createEventChanceParser()
    {
        eventDataParserMap.put("chance", (queueKey, eventIndex) -> {
            List<WorldEventApi> queue = worldEventManager.getEventQueueMap().get(queueKey).getEventQueueAsList();
            return String.valueOf(queue.get(eventIndex >= queue.size() ? queue.size() - 1 : eventIndex).getEventData().chancePercent());
        });
    }

    private void createEventDurationParser()
    {
        eventDataParserMap.put("duration", (queueKey, eventIndex) -> {
            List<WorldEventApi> queue = worldEventManager.getEventQueueMap().get(queueKey).getEventQueueAsList();
            return String.valueOf(queue.get(eventIndex >= queue.size() ? queue.size() - 1 : eventIndex).getEventData().durationSeconds());
        });
    }

    private void createEventExpireParser()
    {
        eventDataParserMap.put("expire", (queueKey, eventIndex) -> {
            List<WorldEventApi> queue = worldEventManager.getEventQueueMap().get(queueKey).getEventQueueAsList();
            AtomicReference<String> expireFormatted = new AtomicReference<>("??:??:??");
            queue.get(eventIndex >= queue.size() ? queue.size() - 1 : eventIndex).getExpireTime().ifPresent(instant ->
                    expireFormatted.set(DateTimeFormatter.ofPattern(I18n.global.getString("event-expire-time-format")).withZone(ZoneId.systemDefault()).format(instant)));
            return expireFormatted.get();
        });
    }

    private void createEventCooldownParser()
    {
        eventDataParserMap.put("cooldown", (queueKey, eventIndex) -> {
            List<WorldEventApi> queue = worldEventManager.getEventQueueMap().get(queueKey).getEventQueueAsList();
            return String.valueOf(queue.get(eventIndex >= queue.size() ? queue.size() - 1 : eventIndex).getEventData().cooldownSeconds());
        });
    }

    private void createEventActiveParser()
    {
        eventDataParserMap.put("active", (queueKey, eventIndex) -> {
            List<WorldEventApi> queue = worldEventManager.getEventQueueMap().get(queueKey).getEventQueueAsList();
            return String.valueOf(queue.get(eventIndex >= queue.size() ? queue.size() - 1 : eventIndex).isActive());
        });
    }

    private void createQueueActiveParser()
    {
        queueDataParserMap.put("active", queueKey -> {
            return String.valueOf(worldEventManager.getEventQueueMap().get(queueKey).isActive());
        });
    }

    private void createQueueCapacityParser()
    {
        queueDataParserMap.put("capacity", queueKey -> {
            QueueData queueData = worldEventManager.getEventQueueMap().get(queueKey).getQueueData();
            return String.valueOf(queueData.capacity());
        });
    }

}
