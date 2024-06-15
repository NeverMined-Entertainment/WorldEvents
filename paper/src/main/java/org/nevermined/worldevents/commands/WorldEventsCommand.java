package org.nevermined.worldevents.commands;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.CommandAPICommand;
import org.nevermined.worldevents.WorldEvents;
import org.nevermined.worldevents.gui.MainGui;

@Singleton
public class WorldEventsCommand {

    private final WorldEvents plugin;

    @Inject
    public WorldEventsCommand(WorldEvents plugin)
    {
        this.plugin = plugin;
        registerCommand();
    }

    private void registerCommand()
    {
        new CommandAPICommand("wevents")
                .executesPlayer((sender, args) -> {
                    new MainGui(plugin, sender).openGui(sender);
                })
                .register();
    }

}
