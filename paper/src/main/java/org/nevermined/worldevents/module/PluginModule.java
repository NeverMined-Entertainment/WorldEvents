package org.nevermined.worldevents.module;

import com.google.inject.AbstractModule;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.nevermined.worldevents.WorldEvents;
import org.nevermined.worldevents.api.WorldEventsApi;

public class PluginModule extends AbstractModule {

    private final WorldEvents plugin;

    public PluginModule(WorldEvents plugin)
    {
        this.plugin = plugin;
    }

    @Override
    protected void configure() {
        bind(JavaPlugin.class)
                .toInstance(plugin);
        bind(WorldEvents.class)
                .toInstance(plugin);
        bind(WorldEventsApi.class)
                .to(org.nevermined.worldevents.WorldEventsApi.class);
        bind(FileConfiguration.class)
                .toInstance(plugin.getConfig());
    }

}
