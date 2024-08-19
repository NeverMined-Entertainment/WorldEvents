package org.nevermined.worldevents.commands;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.*;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import me.clip.placeholderapi.PlaceholderAPI;
import me.wyne.wutils.i18n.I18n;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.nevermined.worldevents.WorldEvents;
import org.nevermined.worldevents.api.core.QueueData;
import org.nevermined.worldevents.api.core.WorldEventQueueApi;
import org.nevermined.worldevents.api.core.WorldEventSelfFactoryApi;
import org.nevermined.worldevents.api.core.exceptions.AlreadyActiveException;
import org.nevermined.worldevents.api.core.exceptions.AlreadyInactiveException;
import org.nevermined.worldevents.core.TempWorldEventQueue;
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
                        .withPermission("wevents.queuecontrol")
                        .then(new LiteralArgument("reload")
                                .withPermission(CommandPermission.OP)
                                .executes(((sender, args) -> {
                                    plugin.reloadEventQueues();
                                    WorldEvents.adventure().sender(sender).sendMessage(I18n.global.getPlaceholderComponent(I18n.toLocale(sender), sender, "success-queues-reloaded"));
                                })))
                        .then(new StringArgument("queueKey")
                                .replaceSuggestions(ArgumentSuggestions.stringCollection(info -> plugin.getWorldEventManager().getEventQueueMap().keySet()))
                                .then(new StringArgument("queueAction")
                                        .replaceSuggestions(ArgumentSuggestions.stringCollection(this::getQueueActionSuggestions))
                                        .executes(this::executeQueueCommand)
                                        .then(new IntegerArgument("eventIndex", 0)
                                                .replaceSafeSuggestions(SafeSuggestions.suggestCollection(this::getEventIndexSuggestions))
                                                .then(new MultiLiteralArgument("eventAction", getEventActionSuggestions())
                                                        .executes(this::executeEventCommand)
                                                                .then(new StringArgument("eventKey")
                                                                        .replaceSuggestions(ArgumentSuggestions.stringCollection(this::getEventKeySuggestions))
                                                                        .executes(this::executeEventCommand))
                                                        )))))
                .then(new LiteralArgument("create")
                        .withPermission("wevents.queuecreate")
                        .then(new StringArgument("queueKey")
                                .replaceSuggestions(ArgumentSuggestions.strings("<queue key>"))
                                .then(new TextArgument("queueName")
                                        .replaceSuggestions(ArgumentSuggestions.strings("<queue name>"))
                                        .then(new TextArgument("queueDescription")
                                                .replaceSuggestions(ArgumentSuggestions.strings("<queue description>"))
                                                .then(new ItemStackArgument("queueItem")
                                                        .then(new ListArgumentBuilder<String>("eventKeys")
                                                                .allowDuplicates(true)
                                                                .withList(this::getEventKeysList)
                                                                .withStringMapper().buildGreedy()
                                                                .executes(this::executeQueueCreateCommand)))))))
                .then(new LiteralArgument("reload")
                        .withPermission(CommandPermission.OP)
                        .executes(((sender, args) -> {
                            plugin.reload();
                            WorldEvents.adventure().sender(sender).sendMessage(I18n.global.getPlaceholderComponent(I18n.toLocale(sender), sender, "success-plugin-reloaded"));
                        })))
                .register();
    }

    private Collection<String> getQueueActionSuggestions(SuggestionInfo<CommandSender> info)
    {
        Set<String> suggestions = new HashSet<>();

        String queueKey = info.previousArgs().getOrDefaultRaw("queueKey", "");

        if (!validateQueueKey(queueKey))
            return suggestions;

        WorldEventQueueApi queue = plugin.getWorldEventManager().getEventQueueMap().get(queueKey);
        if (queue.isActive())
            suggestions.add("stop");
        else
            suggestions.add("start");
        suggestions.add("skip");
        suggestions.add("event");
        suggestions.add("info");

        return suggestions;
    }

    private String[] getEventActionSuggestions()
    {
        Set<String> suggestions = new HashSet<>();
        suggestions.add("swap");
        suggestions.add("info");
        return suggestions.toArray(String[]::new);
    }

    private Collection<Integer> getEventIndexSuggestions(SuggestionInfo<CommandSender> info)
    {
        Set<Integer> suggestions = new HashSet<>();

        String queueKey = info.previousArgs().getOrDefaultRaw("queueKey", "");

        if (!validateQueueKey(queueKey))
            return suggestions;
        if (!(info.previousArgs().getOrDefaultRaw("queueAction", "")).equalsIgnoreCase("event"))
            return suggestions;

        WorldEventQueueApi queue = plugin.getWorldEventManager().getEventQueueMap().get(queueKey);
        for (int i = 0; i < queue.getEventQueue().size(); i++)
            suggestions.add(i);

        return suggestions;
    }

    private Collection<String> getEventKeySuggestions(SuggestionInfo<CommandSender> info)
    {
        Set<String> suggestions = new HashSet<>();

        String queueKey = info.previousArgs().getOrDefaultRaw("queueKey", "");

        if (!validateQueueKey(queueKey))
            return suggestions;
        if (!(info.previousArgs().getOrDefaultRaw("queueAction", "")).equalsIgnoreCase("event"))
            return suggestions;
        if (!((String)(info.previousArgs().getOrDefault("eventAction", ""))).equalsIgnoreCase("swap"))
            return suggestions;

        WorldEventQueueApi queue = plugin.getWorldEventManager().getEventQueueMap().get(queueKey);
        suggestions.addAll(queue.getEventSet().keySet());

        return suggestions;
    }

    private Collection<String> getEventKeysList()
    {
        Set<String> eventKeys = new HashSet<>();
        plugin.getWorldEventManager().getEventQueueMap().values().forEach(queue -> eventKeys.addAll(queue.getEventSet().keySet()));
        return eventKeys;
    }

    private void executeQueueCommand(CommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException
    {
        String queueKey = args.getOrDefaultRaw("queueKey", "");
        String action = args.getOrDefaultRaw("queueAction", "");

        if (!validateQueueKey(queueKey))
            throw CommandAPIBukkit.failWithAdventureComponent(I18n.global.getPlaceholderComponent(I18n.toLocale(sender), sender, "error-queue-not-found", Placeholder.unparsed("queue-key", queueKey)));

        WorldEventQueueApi queue = plugin.getWorldEventManager().getEventQueueMap().get(queueKey);

        switch (action) {
            case "start" -> {
                try {
                    queue.startNext();
                    WorldEvents.adventure().sender(sender).sendMessage(I18n.global.getPlaceholderComponent(I18n.toLocale(sender), sender, "success-queue-activated", Placeholder.component("queue-name", queue.getQueueData().name())));
                } catch (AlreadyActiveException e)
                {
                    throw CommandAPIBukkit.failWithAdventureComponent(I18n.global.getPlaceholderComponent(I18n.toLocale(sender), sender, "error-queue-already-active", Placeholder.component("queue-name", queue.getQueueData().name())));
                }
            }
            case "stop" -> {
                try {
                    queue.stopCurrent();
                    WorldEvents.adventure().sender(sender).sendMessage(I18n.global.getPlaceholderComponent(I18n.toLocale(sender), sender, "success-queue-deactivated", Placeholder.component("queue-name", queue.getQueueData().name())));
                } catch (AlreadyInactiveException e)
                {
                    throw CommandAPIBukkit.failWithAdventureComponent(I18n.global.getPlaceholderComponent(I18n.toLocale(sender), sender, "error-queue-already-inactive", Placeholder.component("queue-name", queue.getQueueData().name())));
                }
            }
            case "skip" -> {
                if (queue.isActive()) {
                    Component eventName = queue.peekEvent().getEventData().name();
                    queue.stopCurrent();
                    queue.startNext();
                    WorldEvents.adventure().sender(sender).sendMessage(I18n.global.getPlaceholderComponent(I18n.toLocale(sender), sender, "success-event-skipped", Placeholder.component("event-name", eventName)));
                } else {
                    Component eventName = queue.pollEvent().getEventData().name();
                    queue.startNext();
                    WorldEvents.adventure().sender(sender).sendMessage(I18n.global.getPlaceholderComponent(I18n.toLocale(sender), sender, "success-event-skipped", Placeholder.component("event-name", eventName)));
                }
            }
            case "info" ->
                    WorldEvents.adventure().sender(sender).sendMessage(MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(I18n.toPlayer(sender), I18n.reduceString(I18n.global.getStringList(I18n.toLocale(sender), "info-queue")).replace("<queue-key>", queueKey))));
        }
    }

    private void executeEventCommand(CommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException
    {
        String queueKey = args.getOrDefaultRaw("queueKey", "");
        int eventIndex = (int) args.getOrDefault("eventIndex", 1);
        String eventAction = (String) args.getOrDefault("eventAction", "");
        String eventKey = args.getOrDefaultRaw("eventKey", "");

        if (!validateQueueKey(queueKey))
            throw CommandAPIBukkit.failWithAdventureComponent(I18n.global.getPlaceholderComponent(I18n.toLocale(sender), sender,"error-queue-not-found", Placeholder.unparsed("queue-key", queueKey)));
        if (plugin.getWorldEventManager().getEventQueueMap().size() <= eventIndex)
            throw CommandAPIBukkit.failWithAdventureComponent(I18n.global.getPlaceholderComponent(I18n.toLocale(sender), sender,"error-event-not-found", Placeholder.unparsed("event-index", String.valueOf(eventIndex))));

        WorldEventQueueApi queue = plugin.getWorldEventManager().getEventQueueMap().get(queueKey);

        Optional<WorldEventSelfFactoryApi> eventFactory = Optional.ofNullable(queue.getEventSet().get(eventKey));

        if (eventAction.equalsIgnoreCase("swap") && queue.getEventQueueAsList().get(eventIndex).isActive())
            throw CommandAPIBukkit.failWithAdventureComponent(I18n.global.getPlaceholderComponent(I18n.toLocale(sender), sender, "error-event-active-swap"));
        if (eventAction.equalsIgnoreCase("swap") && eventFactory.isEmpty())
            throw CommandAPIBukkit.failWithAdventureComponent(I18n.global.getPlaceholderComponent(I18n.toLocale(sender), sender, "error-event-key-not-found", Placeholder.unparsed("event-key", eventKey)));

        switch (eventAction) {
            case "swap" -> {
                queue.replaceEvent(eventIndex, eventFactory.get().create());
                WorldEvents.adventure().sender(sender).sendMessage(I18n.global.getPlaceholderComponent(I18n.toLocale(sender), sender,"success-event-type-swapped",
                        Placeholder.unparsed("event-index", String.valueOf(eventIndex)),
                        Placeholder.component("event-name", eventFactory.get().getEventData().name())));
            }
            case "info" -> {
                WorldEvents.adventure().sender(sender).sendMessage(MiniMessage.miniMessage().deserialize(
                        PlaceholderAPI.setPlaceholders(I18n.toPlayer(sender), I18n.reduceString(I18n.global.getStringList(I18n.toLocale(sender), "info-event"))
                                .replace("<event-key>", queue.getEventQueueAsList().get(eventIndex).getEventData().key())
                                .replace("<queue-key>", queueKey)
                                .replace("<event-index>", String.valueOf(eventIndex)))
                ));
            }
        }
    }

    private void executeQueueCreateCommand(CommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException
    {
        String queueKey = args.getOrDefaultRaw("queueKey", "new-queue");
        Component queueName = MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(I18n.toPlayer(sender), args.getOrDefaultRaw("queueName", "<!i>New queue").replaceAll("\"", ""))).decoration(TextDecoration.ITALIC, false);
        List<Component> queueDescription = args.getOrDefaultRaw("queueDescription", "").equalsIgnoreCase("") ?
                new ArrayList<>() :
                Arrays.stream(args.getOrDefaultRaw("queueDescription", "")
                                .replaceAll("\"", "")
                                .split("(?i)<br\\s*/?>"))
                .map(s -> MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(I18n.toPlayer(sender), s)).decoration(TextDecoration.ITALIC, false))
                .toList();
        Material queueItem = ((ItemStack) args.getOrDefault("queueItem", new ItemStack(Material.NETHER_STAR))).getType();
        List<String> eventKeys = (List<String>) args.getOrDefault("eventKeys", new ArrayList<String>());

        if (plugin.getWorldEventManager().getEventQueueMap().containsKey(queueKey))
            throw CommandAPIBukkit.failWithAdventureComponent(I18n.global.getPlaceholderComponent(I18n.toLocale(sender), sender, "error-queue-already-exists",
                    Placeholder.unparsed("queue-key", queueKey)));
        if (eventKeys.isEmpty())
            throw CommandAPIBukkit.failWithAdventureComponent(I18n.global.getPlaceholderComponent(I18n.toLocale(sender), sender, "error-queue-not-enough-events"));

        Map<String, WorldEventSelfFactoryApi> eventSet = new HashMap<>();

        plugin.getWorldEventManager().getEventQueueMap().values()
                .forEach(queue -> eventKeys
                        .forEach(eventKey -> {
                            if (queue.getEventSet().containsKey(eventKey))
                                eventSet.put(eventKey, queue.getEventSet().get(eventKey));
        }));

        plugin.getWorldEventManager().getEventQueueMap().put(queueKey, new TempWorldEventQueue(plugin.getWorldEventManager(),
            new QueueData(
                    queueKey,
                    queueName,
                    queueDescription,
                    queueItem,
                    eventKeys.size()
            ), eventSet));
        WorldEvents.adventure().sender(sender).sendMessage(I18n.global.getPlaceholderComponent(I18n.toLocale(sender), sender, "success-queue-created",
                Placeholder.component("queue-name", queueName)));
    }

    private boolean validateQueueKey(String queueKey)
    {
        return plugin.getWorldEventManager().getEventQueueMap().containsKey(queueKey);
    }
}
