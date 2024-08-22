package org.nevermined.worldevents;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.nevermined.worldevents.api.config.GlobalConfigApi;
import org.nevermined.worldevents.api.core.WorldEventAction;
import org.nevermined.worldevents.api.core.WorldEventManagerApi;
import org.nevermined.worldevents.expansions.ExpansionRegistry;

import java.util.function.Supplier;

@Singleton
public class WorldEventsApi implements org.nevermined.worldevents.api.WorldEventsApi {

    private final WorldEvents plugin;
    private final ExpansionRegistry expansionRegistry;

    @Inject
    public WorldEventsApi(WorldEvents plugin, ExpansionRegistry expansionRegistry)
    {
        this.plugin = plugin;
        this.expansionRegistry = expansionRegistry;
        plugin.getServer().getServicesManager().register(org.nevermined.worldevents.api.WorldEventsApi.class, this, plugin, ServicePriority.Normal);
    }

    @Override
    public void registerWorldEventAction(String key, Supplier<WorldEventAction> action) {
        expansionRegistry.registerExpansion(key, action);
    }

    @Override
    public void unregisterWorldEventAction(String key) {
        expansionRegistry.unregisterExpansion(key);
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
