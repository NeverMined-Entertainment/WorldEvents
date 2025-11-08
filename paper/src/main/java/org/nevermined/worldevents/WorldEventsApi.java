package org.nevermined.worldevents;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.nevermined.worldevents.api.WEApi;
import org.nevermined.worldevents.api.config.ConfigProviderApi;
import org.nevermined.worldevents.api.config.GlobalConfigApi;
import org.nevermined.worldevents.api.core.WorldEventManagerApi;
import org.nevermined.worldevents.api.expansion.ExpansionRegistryApi;
import org.nevermined.worldevents.api.expansion.WorldEventExpansion;

@Singleton
public class WorldEventsApi implements org.nevermined.worldevents.api.WorldEventsApi {

    private final WorldEvents plugin;
    private final GlobalConfigApi globalConfig;
    private final ConfigProviderApi configProvider;
    private final WorldEventManagerApi worldEventManager;
    private final ExpansionRegistryApi expansionRegistry;

    @Inject
    public WorldEventsApi(WorldEvents plugin, GlobalConfigApi globalConfig, ConfigProviderApi configProvider, WorldEventManagerApi worldEventManager, ExpansionRegistryApi expansionRegistry)
    {
        this.plugin = plugin;
        this.globalConfig = globalConfig;
        this.configProvider = configProvider;
        this.worldEventManager = worldEventManager;
        this.expansionRegistry = expansionRegistry;
        plugin.getServer().getServicesManager().register(org.nevermined.worldevents.api.WorldEventsApi.class, this, plugin, ServicePriority.Normal);
        WEApi.setInstance(this);
    }

    @Override
    public void registerWorldEventExpansion(WorldEventExpansion worldEventExpansion) {
        expansionRegistry.registerExpansion(worldEventExpansion.getKey(), worldEventExpansion);
    }

    @Override
    public JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    public WorldEventManagerApi getWorldEventManager() {
        return worldEventManager;
    }

    @Override
    public ExpansionRegistryApi getExpansionRegistry() {
        return expansionRegistry;
    }

    @Override
    public GlobalConfigApi getGlobalConfig() {
        return globalConfig;
    }

    @Override
    public ConfigProviderApi getConfigProvider() {
        return configProvider;
    }
}
