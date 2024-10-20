package org.nevermined.worldevents.api;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.nevermined.worldevents.api.config.ConfigProviderApi;
import org.nevermined.worldevents.api.config.GlobalConfigApi;
import org.nevermined.worldevents.api.core.WorldEventAction;
import org.nevermined.worldevents.api.core.WorldEventManagerApi;
import org.nevermined.worldevents.api.expansion.ExpansionData;
import org.nevermined.worldevents.api.expansion.ExpansionRegistryApi;
import org.nevermined.worldevents.api.expansion.WorldEventExpansion;

import java.io.File;
import java.util.function.Supplier;

public interface WorldEventsApi {
    void registerWorldEventAction(String key, Supplier<WorldEventAction> actionSupplier);
    void registerWorldEventExpansion(ExpansionData expansionData);
    void registerWorldEventExpansion(WorldEventExpansion worldEventExpansion);
    void unregisterWorldEventAction(String key);

    JavaPlugin getPlugin();
    WorldEventManagerApi getWorldEventManager();
    ExpansionRegistryApi getExpansionRegistry();
    GlobalConfigApi getGlobalConfig();
    ConfigProviderApi getConfigProvider();
}
