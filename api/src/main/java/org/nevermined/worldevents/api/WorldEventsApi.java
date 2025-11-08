package org.nevermined.worldevents.api;

import org.bukkit.plugin.java.JavaPlugin;
import org.nevermined.worldevents.api.config.ConfigProviderApi;
import org.nevermined.worldevents.api.config.GlobalConfigApi;
import org.nevermined.worldevents.api.core.WorldEventManagerApi;
import org.nevermined.worldevents.api.expansion.ExpansionRegistryApi;
import org.nevermined.worldevents.api.expansion.WorldEventExpansion;

public interface WorldEventsApi {
    void registerWorldEventExpansion(WorldEventExpansion worldEventExpansion);

    JavaPlugin getPlugin();
    WorldEventManagerApi getWorldEventManager();
    ExpansionRegistryApi getExpansionRegistry();
    GlobalConfigApi getGlobalConfig();
    ConfigProviderApi getConfigProvider();
}
