package org.nevermined.worldevents.commands;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.*;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import me.wyne.wutils.i18n.I18n;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.nevermined.worldevents.WorldEvents;
import org.nevermined.worldevents.api.core.WorldEventQueueApi;
import org.nevermined.worldevents.gui.MainGui;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

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
        new CommandAPICommand("wevents")
                .withSubcommand(createQueueCommand())
                .executesPlayer((sender, args) -> {
                    new MainGui(plugin, sender).openGui(sender);
                }).register();
    }

    private CommandAPICommand createQueueCommand()
    {
        return new CommandAPICommand("queue")
                .withPermission("wevents.queuecontrol")
                .withArguments(new StringArgument("queueKey")
                        .replaceSuggestions(ArgumentSuggestions.stringCollection(info ->
                                plugin.getWorldEventManager().getEventQueueMap().keySet())))
                .withArguments(new StringArgument("action")
                        .replaceSuggestions(ArgumentSuggestions.stringCollection(info -> {
                            Set<String> suggestions = new HashSet<>();
                            if (!plugin.getWorldEventManager().getEventQueueMap().containsKey((String) info.previousArgs().get("queueKey")))
                                return suggestions;

                            WorldEventQueueApi queue = plugin.getWorldEventManager().getEventQueueMap().get((String) info.previousArgs().get("queueKey"));
                            if (queue.isActive())
                                suggestions.add("stop");
                            else
                                suggestions.add("start");

                            return suggestions;
                        })))
                .executes((sender, args) -> {
                    Locale locale = null;
                    if (sender instanceof Player player)
                        locale = player.locale();

                    String queueKey = args.getOrDefaultRaw("queueKey", "");
                    String action = (String) args.get("action");

                    if (!plugin.getWorldEventManager().getEventQueueMap().containsKey(queueKey))
                        throw CommandAPIBukkit.failWithAdventureComponent(I18n.global.getComponent(locale, "error-queue-not-found", Placeholder.unparsed("queue-key", queueKey)));

                    WorldEventQueueApi queue = plugin.getWorldEventManager().getEventQueueMap().get(queueKey);

                    if (action.equalsIgnoreCase("start") && !queue.isActive()) {
                        queue.startNext();
                        plugin.adventure().sender(sender).sendMessage(I18n.global.getComponent(locale, "success-queue-activated", Placeholder.unparsed("queue-key", queueKey)));
                    }
                    else if (action.equalsIgnoreCase("start") && queue.isActive())
                        throw CommandAPIBukkit.failWithAdventureComponent(I18n.global.getComponent(locale, "error-queue-already-active", Placeholder.unparsed("queue-key", queueKey)));
                    else if (action.equalsIgnoreCase("stop") && queue.isActive()) {
                        queue.stopCurrent();
                        plugin.adventure().sender(sender).sendMessage(I18n.global.getComponent(locale, "success-queue-deactivated", Placeholder.unparsed("queue-key", queueKey)));
                    }
                    else if (action.equalsIgnoreCase("stop") && !queue.isActive())
                        throw CommandAPIBukkit.failWithAdventureComponent(I18n.global.getComponent(locale, "error-queue-already-inactive", Placeholder.unparsed("queue-key", queueKey)));
                });

    }
}
