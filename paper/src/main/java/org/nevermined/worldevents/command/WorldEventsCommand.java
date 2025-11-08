package org.nevermined.worldevents.command;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.*;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import me.clip.placeholderapi.PlaceholderAPI;
import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.language.replacement.Placeholder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.nevermined.worldevents.WorldEvents;
import org.nevermined.worldevents.api.core.*;
import org.nevermined.worldevents.api.core.exception.AlreadyActiveException;
import org.nevermined.worldevents.api.core.exception.AlreadyInactiveException;
import org.nevermined.worldevents.api.expansion.ExpansionRegistryApi;
import org.nevermined.worldevents.config.GlobalConfig;
import org.nevermined.worldevents.core.TempWorldEventQueue;
import org.nevermined.worldevents.expansion.ExpansionLoader;
import org.nevermined.worldevents.gui.MainGui;

import java.util.*;

@Singleton
public class WorldEventsCommand {

    private final WorldEvents plugin;
    private final GlobalConfig globalConfig;
    private final WorldEventManagerApi worldEventManager;
    private final ExpansionRegistryApi expansionRegistry;
    private final ExpansionLoader expansionLoader;

    @Inject
    public WorldEventsCommand(WorldEvents plugin, GlobalConfig globalConfig, WorldEventManagerApi worldEventManager, ExpansionRegistryApi expansionRegistry, ExpansionLoader expansionLoader)
    {
        this.plugin = plugin;
        this.globalConfig = globalConfig;
        this.worldEventManager = worldEventManager;
        this.expansionRegistry = expansionRegistry;
        this.expansionLoader = expansionLoader;
        registerMainCommand();
    }

    private void registerMainCommand()
    {
        new CommandTree("wevents")
                .executesPlayer((sender, args) -> {
                    if (!sender.hasPermission("wevents.queuegui"))
                        return;
                    new MainGui(globalConfig, worldEventManager, sender).openGui(sender); // TODO Might do assisted inject
                })
                .then(new LiteralArgument("queue")
                        .withPermission("wevents.queuecontrol")
                        .then(new LiteralArgument("reload")
                                .withPermission(CommandPermission.OP)
                                .executes(((sender, args) -> {
                                    worldEventManager.reloadEventQueues();
                                    I18n.global.accessor(sender, "success-queues-reloaded").getPlaceholderComponent(sender).sendMessage(sender);
                                })))
                        .then(new StringArgument("queueKey")
                                .replaceSuggestions(ArgumentSuggestions.stringCollection(info -> worldEventManager.getEventQueueMap().keySet()))
                                .then(new StringArgument("queueAction")
                                        .replaceSuggestions(ArgumentSuggestions.stringCollection(this::getQueueActionSuggestions))
                                        .executes(this::executeQueueCommand)
                                        .then(new IntegerArgument("eventIndex", 0)
                                                .includeSafeSuggestions(SafeSuggestions.suggestCollection(this::getEventIndexSuggestions))
                                                .then(new MultiLiteralArgument("eventAction", getEventActionSuggestions())
                                                        .executes(this::executeEventCommand)
                                                                .then(new StringArgument("eventKey")
                                                                        .replaceSuggestions(ArgumentSuggestions.stringCollection(this::getEventKeySuggestions))
                                                                        .executes(this::executeEventCommand)))))))
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
                                                                .withList(this::getEventKeyList)
                                                                .withStringMapper().buildGreedy()
                                                                .executes(this::executeQueueCreateCommand)))))))
                .then(new LiteralArgument("expansion")
                        .withPermission("wevents.expansioncontrol")
                        .then(new LiteralArgument("reload")
                                .withPermission(CommandPermission.OP)
                                .executes((sender, args) -> {
                                    expansionLoader.reloadExpansions();
                                    I18n.global.accessor(sender, "success-expansions-reloaded").getPlaceholderComponent(sender).sendMessage(sender);
                                }))
                        .then(new StringArgument("expansionKey")
                                .replaceSuggestions(ArgumentSuggestions.stringCollection(this::getExpansionKeySuggestions))
                                .then(new MultiLiteralArgument("expansionAction", getExpansionActionSuggestions())
                                        .executes(this::executeExpansionCommand))))
                .then(new LiteralArgument("reload")
                        .withPermission(CommandPermission.OP)
                        .executes(((sender, args) -> {
                            plugin.reload();
                            I18n.global.accessor(sender, "success-plugin-reloaded").getPlaceholderComponent(sender).sendMessage(sender);
                        })))
                .register();
    }

    private Collection<String> getQueueActionSuggestions(SuggestionInfo<CommandSender> info)
    {
        Set<String> suggestions = new HashSet<>();

        String queueKey = info.previousArgs().getOrDefaultRaw("queueKey", "");

        if (!validateQueueKey(queueKey))
            return suggestions;

        WorldEventQueueApi queue = worldEventManager.getEventQueueMap().get(queueKey);
        if (queue.isActive())
            suggestions.add("stop");
        else
            suggestions.add("start");
        suggestions.add("skip");
        suggestions.add("event");
        suggestions.add("info");
        suggestions.add("remove");

        return suggestions;
    }

    private String[] getEventActionSuggestions()
    {
        Set<String> suggestions = new HashSet<>();
        suggestions.add("swap");
        suggestions.add("add");
        suggestions.add("remove");
        suggestions.add("info");
        return suggestions.toArray(String[]::new);
    }

    private String[] getExpansionActionSuggestions()
    {
        Set<String> suggestions = new HashSet<>();
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

        WorldEventQueueApi queue = worldEventManager.getEventQueueMap().get(queueKey);
        for (int i = 0; i < queue.getEventQueue().size(); i++)
            suggestions.add(i);

        return suggestions;
    }

    private Collection<String> getEventKeySuggestions(SuggestionInfo<CommandSender> info)
    {
        Set<String> suggestions = new HashSet<>();

        String queueKey = info.previousArgs().getOrDefaultRaw("queueKey", "");
        String eventAction = (String)info.previousArgs().getOrDefault("eventAction", "");

        if (!validateQueueKey(queueKey))
            return suggestions;
        if (!(info.previousArgs().getOrDefaultRaw("queueAction", "")).equalsIgnoreCase("event"))
            return suggestions;
        if (!eventAction.equalsIgnoreCase("swap") &&
                !eventAction.equalsIgnoreCase("add"))
            return suggestions;

        WorldEventQueueApi queue = worldEventManager.getEventQueueMap().get(queueKey);
        suggestions.addAll(queue.getEventSet().keySet());

        return suggestions;
    }
    
    private Collection<String> getExpansionKeySuggestions(SuggestionInfo<CommandSender> info)
    {
        return expansionRegistry.getRegisteredExpansions().keySet();
    }

    private Collection<String> getEventKeyList()
    {
        Set<String> eventKeys = new HashSet<>();
        worldEventManager.getEventQueueMap().values().forEach(queue -> eventKeys.addAll(queue.getEventSet().keySet()));
        return eventKeys;
    }

    private void executeQueueCommand(CommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException
    {
        String queueKey = args.getOrDefaultRaw("queueKey", "");
        String action = args.getOrDefaultRaw("queueAction", "");

        if (!validateQueueKey(queueKey))
            throw CommandAPIBukkit.failWithAdventureComponent(I18n.global.accessor(sender, "error-queue-not-found").getPlaceholderComponent(sender, Placeholder.replace("queue-key", queueKey)));

        WorldEventQueueApi queue = worldEventManager.getEventQueueMap().get(queueKey);

        switch (action) {
            case "start" -> {
                try {
                    queue.startNext();
                    I18n.global.accessor(sender, "success-queue-activated").getPlaceholderComponent(sender, Placeholder.legacy("queue-name", queue.getQueueData().name())).sendMessage(sender);
                } catch (AlreadyActiveException e)
                {
                    throw CommandAPIBukkit.failWithAdventureComponent(I18n.global.accessor(sender, "error-queue-already-active").getPlaceholderComponent(sender, Placeholder.legacy("queue-name", queue.getQueueData().name())));
                }
            }
            case "stop" -> {
                try {
                    queue.stopCurrent();
                    I18n.global.accessor(sender, "success-queue-deactivated").getPlaceholderComponent(sender, Placeholder.legacy("queue-name", queue.getQueueData().name())).sendMessage(sender);
                } catch (AlreadyInactiveException e)
                {
                    throw CommandAPIBukkit.failWithAdventureComponent(I18n.global.accessor(sender, "error-queue-already-inactive").getPlaceholderComponent(sender, Placeholder.legacy("queue-name", queue.getQueueData().name())));
                }
            }
            case "skip" -> {
                if (queue.isActive()) {
                    Component eventName = queue.peekEvent().getEventData().name();
                    queue.stopCurrent();
                    queue.startNext();
                    I18n.global.accessor(sender, "success-event-skipped").getPlaceholderComponent(sender, Placeholder.legacy("event-name", eventName)).sendMessage(sender);
                } else {
                    Component eventName = queue.pollEvent().getEventData().name();
                    I18n.global.accessor(sender, "success-event-skipped").getPlaceholderComponent(sender, Placeholder.legacy("event-name", eventName)).sendMessage(sender);
                }
            }
            case "remove" -> {
                if (queue.isActive())
                    queue.stopCurrent();
                worldEventManager.getEventQueueMap().remove(queueKey);
                I18n.global.accessor(sender, "success-event-removed").getPlaceholderComponent(sender, Placeholder.legacy("queue-name", queue.getQueueData().name())).sendMessage(sender);
            }
            case "info" ->
                    I18n.global.accessor(sender, "info-queue").getPlaceholderComponent(sender, Placeholder.replace("queue-key", queueKey)).sendMessage(sender);
        }
    }

    private void executeEventCommand(CommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException
    {
        String queueKey = args.getOrDefaultRaw("queueKey", "");
        int eventIndex = (int) args.getOrDefault("eventIndex", 1);
        String eventAction = (String) args.getOrDefault("eventAction", "");
        String eventKey = args.getOrDefaultRaw("eventKey", "");

        if (!validateQueueKey(queueKey))
            throw CommandAPIBukkit.failWithAdventureComponent(I18n.global.accessor(sender, "error-queue-not-found").getPlaceholderComponent(sender, Placeholder.replace("queue-key", queueKey)));

        WorldEventQueueApi queue = worldEventManager.getEventQueueMap().get(queueKey);

        if (queue.getEventQueue().size() <= eventIndex)
            throw CommandAPIBukkit.failWithAdventureComponent(I18n.global.accessor(sender, "error-event-not-found").getPlaceholderComponent(sender, Placeholder.replace("event-index", eventIndex)));

        Optional<WorldEventSelfFactoryApi> eventFactory = Optional.ofNullable(queue.getEventSet().get(eventKey));

        if (eventAction.equalsIgnoreCase("swap") && queue.getEventQueueAsList().get(eventIndex).isActive())
            throw CommandAPIBukkit.failWithAdventureComponent(I18n.global.accessor(sender, "error-event-active-swap").getPlaceholderComponent(sender));
        if ((eventAction.equalsIgnoreCase("swap") || eventAction.equalsIgnoreCase("add")) && eventFactory.isEmpty())
            throw CommandAPIBukkit.failWithAdventureComponent(I18n.global.accessor(sender, "error-event-key-not-found").getPlaceholderComponent(sender, Placeholder.replace("event-key", eventKey)));

        switch (eventAction) {
            case "swap" -> {
                queue.replaceEvent(eventIndex, eventFactory.get().create());
                I18n.global.accessor(sender, "success-event-type-swapped")
                        .getPlaceholderComponent(sender,
                                Placeholder.replace("event-index", eventIndex),
                                Placeholder.legacy("event-name", eventFactory.get().getEventData().name()))
                        .sendMessage(sender);
            }
            case "add" -> {
                queue.queueEvent(eventFactory.get().create());
                I18n.global.accessor(sender, "success-event-queued")
                        .getPlaceholderComponent(sender,
                                Placeholder.legacy("event-name",eventFactory.get().getEventData().name()),
                                Placeholder.legacy("queue-name", queue.getQueueData().name()))
                        .sendMessage(sender);
            }
            case "remove" -> {
                WorldEventApi removedEvent = queue.removeEvent(eventIndex);
                I18n.global.accessor(sender, "success-event-removed")
                        .getPlaceholderComponent(sender,
                                Placeholder.legacy("event-name", removedEvent.getEventData().name()),
                                Placeholder.legacy("queue-name", queue.getQueueData().name()))
                        .sendMessage(sender);
            }
            case "info" -> {
                I18n.global.accessor(sender, "info-event")
                        .getPlaceholderComponent(sender,
                                Placeholder.replace("event-key", queue.getEventQueueAsList().get(eventIndex).getEventData().key()),
                                Placeholder.replace("queue-key", queueKey),
                                Placeholder.replace("event-index", eventIndex))
                        .sendMessage(sender);
            }
        }
    }

    private void executeQueueCreateCommand(CommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException
    {
        String queueKey = args.getOrDefaultRaw("queueKey", "new-queue");
        Component queueName = LegacyComponentSerializer.legacyAmpersand().deserialize(PlaceholderAPI.setPlaceholders(I18n.toPlayer(sender), args.getOrDefaultRaw("queueName", "New queue").replaceAll("\"", ""))).decoration(TextDecoration.ITALIC, false);
        List<Component> queueDescription = args.getOrDefaultRaw("queueDescription", "").equalsIgnoreCase("\"\"") ?
                new ArrayList<>() :
                Arrays.stream(args.getOrDefaultRaw("queueDescription", "")
                                .replaceAll("\"", "")
                                .split("(?i)<br\\s*/?>"))
                .map(s -> LegacyComponentSerializer.legacyAmpersand().deserialize(PlaceholderAPI.setPlaceholders(I18n.toPlayer(sender), s)).decoration(TextDecoration.ITALIC, false))
                .map(Component::asComponent)
                .toList();
        Material queueItem = ((ItemStack) args.getOrDefault("queueItem", new ItemStack(Material.NETHER_STAR))).getType();
        List<String> eventKeys = (List<String>) args.getOrDefault("eventKeys", new ArrayList<String>());

        if (worldEventManager.getEventQueueMap().containsKey(queueKey))
            throw CommandAPIBukkit.failWithAdventureComponent(I18n.global.accessor(sender, "error-queue-already-exists").getPlaceholderComponent(sender, Placeholder.replace("queue-key", queueKey)));
        if (eventKeys.isEmpty())
            throw CommandAPIBukkit.failWithAdventureComponent(I18n.global.accessor(sender, "error-queue-not-enough-events").getPlaceholderComponent(sender));

        Map<String, WorldEventSelfFactoryApi> eventSet = new HashMap<>();

        worldEventManager.getEventQueueMap().values()
                .forEach(queue -> eventKeys
                        .forEach(eventKey -> {
                            if (queue.getEventSet().containsKey(eventKey))
                                eventSet.put(eventKey, queue.getEventSet().get(eventKey));
        }));

        worldEventManager.getEventQueueMap().put(queueKey, new TempWorldEventQueue(worldEventManager,
            new QueueData(
                    queueKey,
                    queueName,
                    queueDescription,
                    queueItem,
                    eventKeys.size()
            ), eventSet));
        I18n.global.accessor(sender, "success-queue-created").getPlaceholderComponent(sender, Placeholder.replace("queue-name", queueName)).sendMessage(sender);
    }

    private void executeExpansionCommand(CommandSender sender, CommandArguments args) throws WrapperCommandSyntaxException
    {
        String action = (String) args.getOrDefault("expansionAction", "info");
        String expansionKey = args.getOrDefaultRaw("expansionKey", "");

        if ((action.equalsIgnoreCase("unregister") || action.equalsIgnoreCase("info")) &&
                !expansionRegistry.getRegisteredExpansions().containsKey(expansionKey))
            throw CommandAPIBukkit.failWithAdventureComponent(I18n.global.accessor(sender, "error-expansion-not-found").getPlaceholderComponent(sender, Placeholder.replace("expansion-key", expansionKey)));

        switch (action) {
            case "info" -> {
                I18n.global.accessor(sender, "info-expansion").getPlaceholderComponent(sender, Placeholder.replace("expansion-key", expansionKey)).sendMessage(sender);
            }
        }
    }

    private boolean validateQueueKey(String queueKey)
    {
        return worldEventManager.getEventQueueMap().containsKey(queueKey);
    }
}
