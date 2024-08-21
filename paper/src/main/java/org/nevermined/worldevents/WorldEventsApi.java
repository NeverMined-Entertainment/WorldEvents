package org.nevermined.worldevents;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.plugin.java.JavaPlugin;
import org.nevermined.worldevents.api.config.GlobalConfigApi;
import org.nevermined.worldevents.api.core.WorldEventAction;
import org.nevermined.worldevents.api.core.WorldEventManagerApi;

@Singleton
public class WorldEventsApi implements org.nevermined.worldevents.api.WorldEventsApi {

    private final WorldEvents plugin;

    @Inject
    public WorldEventsApi(WorldEvents plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void registerWorldEventAction(String key, WorldEventAction action) {
        plugin.getExpansionRegistry().registerExpansion(key, action);
    }

    @Override
    public JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    public WorldEventManagerApi getWorldEventManager() {
        return plugin.getWorldEventManager();
    }

    @Override
    public GlobalConfigApi getGlobalConfig() {
        return plugin.getGlobalConfig();
    }
}
