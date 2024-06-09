package org.nevermined.worldevents.hooks;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nevermined.worldevents.WorldEvents;
import org.nevermined.worldevents.api.core.WorldEventApi;
import org.nevermined.worldevents.api.core.WorldEventManagerApi;
import org.nevermined.worldevents.core.WorldEventManager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;

@Singleton
public class Placeholders extends PlaceholderExpansion {

    private final WorldEvents plugin;
    private final WorldEventManagerApi worldEventManager;

    // Key - data type
    // Value - BiFunction<Queue key, Event queue index, data>
    private final Map<String, BiFunction<String, Integer, String>> eventDataParserMap = new HashMap<>();

    @Inject
    public Placeholders(WorldEvents plugin, WorldEventManagerApi worldEventManager)
    {
        this.plugin = plugin;
        this.worldEventManager = worldEventManager;
        createNameParser();
        createDescriptionParser();
        createChanceParser();
        createDurationParser();
        createCooldownParser();
        createActiveParser();
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

        return null;
    }

    private void createNameParser()
    {
        eventDataParserMap.put("name", (queueKey, eventIndex) -> {
            LinkedList<WorldEventApi> queue = ((LinkedList<WorldEventApi>)worldEventManager.getEventQueueMap().get(queueKey).getEventQueue());
            return LegacyComponentSerializer.legacyAmpersand().serialize(queue.get(eventIndex >= queue.size() ? queue.size() - 1 : eventIndex).getEventData().name());
        });
    }

    private void createDescriptionParser()
    {
        eventDataParserMap.put("description", (queueKey, eventIndex) -> {
            LinkedList<WorldEventApi> queue = ((LinkedList<WorldEventApi>)worldEventManager.getEventQueueMap().get(queueKey).getEventQueue());
            return queue.get(eventIndex >= queue.size() ? queue.size() - 1 : eventIndex).getEventData().description()
                    .stream()
                    .map(component -> LegacyComponentSerializer.legacyAmpersand().serialize(component))
                    .reduce("", (s, s2) -> s + "\n" + s2);
        });
    }

    private void createChanceParser()
    {
        eventDataParserMap.put("chance", (queueKey, eventIndex) -> {
            LinkedList<WorldEventApi> queue = ((LinkedList<WorldEventApi>)worldEventManager.getEventQueueMap().get(queueKey).getEventQueue());
            return String.valueOf(queue.get(eventIndex >= queue.size() ? queue.size() - 1 : eventIndex).getEventData().chancePercent());
        });
    }

    private void createDurationParser()
    {
        eventDataParserMap.put("duration", (queueKey, eventIndex) -> {
            LinkedList<WorldEventApi> queue = ((LinkedList<WorldEventApi>)worldEventManager.getEventQueueMap().get(queueKey).getEventQueue());
            return String.valueOf(queue.get(eventIndex >= queue.size() ? queue.size() - 1 : eventIndex).getEventData().durationSeconds());
        });
    }

    private void createCooldownParser()
    {
        eventDataParserMap.put("cooldown", (queueKey, eventIndex) -> {
            LinkedList<WorldEventApi> queue = ((LinkedList<WorldEventApi>)worldEventManager.getEventQueueMap().get(queueKey).getEventQueue());
            return String.valueOf(queue.get(eventIndex >= queue.size() ? queue.size() - 1 : eventIndex).getEventData().cooldownSeconds());
        });
    }

    private void createActiveParser()
    {
        eventDataParserMap.put("active", (queueKey, eventIndex) -> {
            LinkedList<WorldEventApi> queue = ((LinkedList<WorldEventApi>)worldEventManager.getEventQueueMap().get(queueKey).getEventQueue());
            return String.valueOf(queue.get(eventIndex >= queue.size() ? queue.size() - 1 : eventIndex).isActive());
        });
    }

}
