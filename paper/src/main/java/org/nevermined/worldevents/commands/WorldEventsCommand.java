package org.nevermined.worldevents.commands;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.*;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.log.Log;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.nevermined.worldevents.WorldEvents;
import org.nevermined.worldevents.api.core.WorldEventQueueApi;
import org.nevermined.worldevents.api.core.WorldEventSelfFactoryApi;
import org.nevermined.worldevents.gui.MainGui;

import java.util.*;

@Singleton
public class WorldEventsCommand {

    private final WorldEvents plugin;

    @Inject
    public WorldEventsCommand(WorldEvents plugin)
    {
        this.plugin = plugin;
        registerMainCommand();
    }

    private void registerMainCommand()
    {
        new CommandTree("wevents")
                .executesPlayer((sender, args) -> {
                    new MainGui(plugin, sender).openGui(sender);
                })
                .then(new LiteralArgument("queue")
                        .withPermission("wevets.queuecontrol")
                        .then(new MultiLiteralArgument("queueKey", plugin.getWorldEventManager().getEventQueueMap().keySet().toArray(String[]::new))
                                .then(new StringArgument("queueAction")
                                        .replaceSuggestions(ArgumentSuggestions.stringCollection(this::getQueueActionSuggestions))
                                        .executes(this::executeQueueCommand)
                                        .then(new IntegerArgument("eventIndex", 1)
                                                .replaceSafeSuggestions(SafeSuggestions.suggestCollection(this::getEventIndexSuggestions))
                                                .then(new MultiLiteralArgument("eventAction", getEventActionSuggestions())
                                                                .then(new StringArgument("eventKey")
                                                                        .replaceSuggestions(ArgumentSuggestions.stringCollection(this::getEventKeySuggestions))
                                                                        .executes(this::executeEventTypeSwapCommand))
                                                        )))))

                .register();
    }

    private Collection<String> getQueueActionSuggestions(SuggestionInfo<CommandSender> info)
    {
        Set<String> suggestions = new HashSet<>();

        String queueKey = (String) info.previousArgs().getOrDefault("queueKey", "");

        if (!validateQueueKey(queueKey))
            return suggestions;

        WorldEventQueueApi queue = plugin.getWorldEventManager().getEventQueueMap().get(queueKey);
        if (queue.isActive())
            suggestions.add("stop");
        else
            suggestions.add("start");
        suggestions.add("skip");
        suggestions.add("event");

        return suggestions;
    }

    private String[] getEventActionSuggestions()
    {
        Set<String> suggestions = new HashSet<>();
        suggestions.add("swap");
        return suggestions.toArray(String[]::new);
    }

    private Collection<Integer> getEventIndexSuggestions(SuggestionInfo<CommandSender> info)
    {
        Set<Integer> suggestions = new HashSet<>();

        String queueKey = (String) info.previousArgs().getOrDefault("queueKey", "");

        if (!validateQueueKey(queueKey))
            return suggestions;
        if (!(info.previousArgs().getOrDefaultRaw("queueAction", "")).equalsIgnoreCase("event"))
            return suggestions;

        WorldEventQueueApi queue = plugin.getWorldEventManager().getEventQueueMap().get(queueKey);
        for (int i = 1; i < queue.getEventQueue().size(); i++)
            suggestions.add(i);

        return suggestions;
    }

    private Collection<String> getEventKeySuggestions(SuggestionInfo<CommandSender> info)
    {
        Set<String> suggestions = new HashSet<>();

        String queueKey = (String) info.previousArgs().getOrDefault("queueKey", "");

        if (!validateQueueKey(queueKey))
            return suggestions;
        if (!(info.previousArgs().getOrDefaultRaw("queueAction", "")).equalsIgnoreCase("event"))
            return suggestions;

        WorldEventQueueApi queue = plugin.getWorldEventManager().getEventQueueMap().get(queueKey);
        suggestions.addAll(queue.getEventSet().keySet());

        return suggestions;
    }

    private void executeQueueCommand(CommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException
    {
        Locale locale = null;
        if (sender instanceof Player player)
            locale = player.locale();

        String queueKey = (String) args.getOrDefault("queueKey", "");
        String action = args.getOrDefaultRaw("queueAction", "");

        if (!validateQueueKey(queueKey))
            throw CommandAPIBukkit.failWithAdventureComponent(I18n.global.getComponent(locale, "error-queue-not-found", Placeholder.unparsed("queue-key", queueKey)));

        WorldEventQueueApi queue = plugin.getWorldEventManager().getEventQueueMap().get(queueKey);

        switch (action) {
            case "start":
                if (!queue.isActive()) {
                    queue.startNext();
                    plugin.adventure().sender(sender).sendMessage(I18n.global.getComponent(locale, "success-queue-activated", Placeholder.component("queue-name", queue.getQueueData().name())));
                } else {
                    throw CommandAPIBukkit.failWithAdventureComponent(I18n.global.getComponent(locale, "error-queue-already-active", Placeholder.component("queue-name", queue.getQueueData().name())));
                }
                break;
            case "stop":
                if (queue.isActive()) {
                    queue.stopCurrent();
                    plugin.adventure().sender(sender).sendMessage(I18n.global.getComponent(locale, "success-queue-deactivated", Placeholder.component("queue-name", queue.getQueueData().name())));
                } else {
                    throw CommandAPIBukkit.failWithAdventureComponent(I18n.global.getComponent(locale, "error-queue-already-inactive", Placeholder.component("queue-name", queue.getQueueData().name())));
                }
                break;
            case "skip":
                if (queue.isActive()) {
                    Component eventName = queue.peekEvent().getEventData().name();
                    queue.stopCurrent();
                    queue.startNext();
                    plugin.adventure().sender(sender).sendMessage(I18n.global.getComponent(locale, "success-event-skipped", Placeholder.component("event-name", eventName)));
                } else {
                    Component eventName = queue.pollEvent().getEventData().name();
                    queue.startNext();
                    plugin.adventure().sender(sender).sendMessage(I18n.global.getComponent(locale, "success-event-skipped", Placeholder.component("event-name", eventName)));
                }
                break;
        }
    }

    private void executeEventTypeSwapCommand(CommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException
    {
        Locale locale = null;
        if (sender instanceof Player player)
            locale = player.locale();

        String queueKey = (String) args.getOrDefault("queueKey", "");
        int eventIndex = (int) args.getOrDefault("eventIndex", 1);
        String eventAction = (String) args.getOrDefault("eventAction", "");
        String eventKey = args.getOrDefaultRaw("eventKey", "");

        if (!validateQueueKey(queueKey))
            throw CommandAPIBukkit.failWithAdventureComponent(I18n.global.getComponent(locale, "error-queue-not-found", Placeholder.unparsed("queue-key", queueKey)));
        if (plugin.getWorldEventManager().getEventQueueMap().size() <= eventIndex)
            throw CommandAPIBukkit.failWithAdventureComponent(I18n.global.getComponent(locale, "error-event-not-found", Placeholder.unparsed("event-index", String.valueOf(eventIndex))));

        WorldEventQueueApi queue = plugin.getWorldEventManager().getEventQueueMap().get(queueKey);

        Optional<WorldEventSelfFactoryApi> eventFactory = Optional.ofNullable(queue.getEventSet().get(eventKey));

        if (eventFactory.isEmpty())
            throw CommandAPIBukkit.failWithAdventureComponent(I18n.global.getComponent("error-event-key-not-found", Placeholder.unparsed("event-key", eventKey)));

        if (eventAction.equals("swap")) {
            queue.replaceEvent(eventIndex, eventFactory.get().create());
            plugin.adventure().sender(sender).sendMessage(I18n.global.getComponent(locale, "success-event-type-swapped",
                    Placeholder.unparsed("event-index", String.valueOf(eventIndex)),
                    Placeholder.component("event-name", eventFactory.get().getEventData().name())));
        }
    }

    private boolean validateQueueKey(String queueKey)
    {
        return plugin.getWorldEventManager().getEventQueueMap().containsKey(queueKey);
    }
}
