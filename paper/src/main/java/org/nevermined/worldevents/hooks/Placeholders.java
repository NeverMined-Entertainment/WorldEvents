package org.nevermined.worldevents.hooks;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.wyne.wutils.i18n.I18n;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nevermined.worldevents.WorldEvents;
import org.nevermined.worldevents.api.core.QueueData;
import org.nevermined.worldevents.api.core.WorldEventApi;
import org.nevermined.worldevents.api.core.WorldEventManagerApi;
import org.nevermined.worldevents.api.expansions.ExpansionRegistryApi;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

@Singleton
public class Placeholders extends PlaceholderExpansion {

    private final WorldEvents plugin;
    private final WorldEventManagerApi worldEventManager;
    private final ExpansionRegistryApi expansionRegistry;

    // Key - data type
    // Value - BiFunction<Queue key, Event queue index, data>
    private final Map<String, BiFunction<String, Integer, String>> eventDataParserMap = new HashMap<>();
    // Key - data type
    // Value - Function<Queue key, data>
    private final Map<String, Function<String, String>> queueDataParserMap = new HashMap<>();
    private final Map<String, Function<String, String>> expansionDataParserMap = new HashMap<>();

    @Inject
    public Placeholders(WorldEvents plugin, WorldEventManagerApi worldEventManager, ExpansionRegistryApi expansionRegistry)
    {
        this.plugin = plugin;
        this.worldEventManager = worldEventManager;
        this.expansionRegistry = expansionRegistry;
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
        createExpansionJarParser();
        createExpansionClassParser();
        createExpansionRegistryTimeParser();
        createExpansionUsageParser();
        register();
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
            // event_  demo-queue-1 _0_  name

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

        if (params.startsWith("expansion"))
        {
            // Key word/Expansion key/data type
            // expansion_     Demo    _jar

            String[] args = params.split("_");
            return expansionDataParserMap.get(args[2]).apply(args[1]);
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
                    .reduce((c1, c2) -> c1.append(Component.newline()).append(c2))
                    .map(component -> LegacyComponentSerializer.legacyAmpersand().serialize(component))
                    .orElse("");
        });
    }

    private void createQueueDescriptionParser()
    {
        queueDataParserMap.put("description", queueKey -> {
            QueueData queueData = worldEventManager.getEventQueueMap().get(queueKey).getQueueData();
            return queueData.description()
                    .stream()
                    .reduce((c1, c2) -> c1.append(Component.newline()).append(c2))
                    .map(component -> LegacyComponentSerializer.legacyAmpersand().serialize(component))
                    .orElse("");
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
        queueDataParserMap.put("item", queueKey ->
                worldEventManager.getEventQueueMap().get(queueKey).getQueueData().item().toString());
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
        queueDataParserMap.put("active", queueKey ->
                String.valueOf(worldEventManager.getEventQueueMap().get(queueKey).isActive()));
    }

    private void createQueueCapacityParser()
    {
        queueDataParserMap.put("capacity", queueKey ->
                String.valueOf(worldEventManager.getEventQueueMap().get(queueKey).getQueueData().capacity()));
    }

    private void createExpansionJarParser()
    {
        expansionDataParserMap.put("jar", expansionKey ->
                expansionRegistry.getRegisteredExpansions().get(expansionKey).jarName());
    }

    private void createExpansionClassParser()
    {
        expansionDataParserMap.put("class", expansionKey ->
                expansionRegistry.getRegisteredExpansions().get(expansionKey).className());
    }

    private void createExpansionRegistryTimeParser()
    {
        expansionDataParserMap.put("time", expansionKey ->
                DateTimeFormatter.ofPattern(I18n.global.getString("expansion-registry-time-format"))
                        .withZone(ZoneId.systemDefault())
                        .format(expansionRegistry.getRegisteredExpansions().get(expansionKey).registryTime()));
    }

    private void createExpansionUsageParser()
    {
        expansionDataParserMap.put("usage", expansionKey -> {
            Stream<String> eventKeysStream = worldEventManager.getEventQueueMap().keySet().stream()
                    .flatMap(queueKey -> worldEventManager.getEventQueueMap().get(queueKey).getEventSet().values().stream())
                    .filter(eventFactory -> eventFactory.getEventData().expansionData().key().equals(expansionKey))
                    .map(eventFactory -> eventFactory.getEventData().key());
            return eventKeysStream
                    .reduce((s1, s2) -> s1 + ", " + s2).orElse("");
        });
    }

}
