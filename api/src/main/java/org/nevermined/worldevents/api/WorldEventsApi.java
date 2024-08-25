package org.nevermined.worldevents.api;

import org.bukkit.plugin.java.JavaPlugin;
import org.nevermined.worldevents.api.config.GlobalConfigApi;
import org.nevermined.worldevents.api.core.WorldEventAction;
import org.nevermined.worldevents.api.core.WorldEventManagerApi;
import org.nevermined.worldevents.api.expansions.ExpansionData;
import org.nevermined.worldevents.api.expansions.ExpansionRegistryApi;

import java.util.function.Supplier;

public interface WorldEventsApi {
    void registerWorldEventAction(String key, Supplier<WorldEventAction> actionSupplier);
    void registerWorldEventExpansion(ExpansionData expansionData);
    void unregisterWorldEventAction(String key);

    JavaPlugin getPlugin();
    WorldEventManagerApi getWorldEventManager();
    ExpansionRegistryApi getExpansionRegistry();
    GlobalConfigApi getGlobalConfig();
}
