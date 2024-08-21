package org.nevermined.worldevents.api;

import org.bukkit.plugin.java.JavaPlugin;
import org.nevermined.worldevents.api.config.GlobalConfigApi;
import org.nevermined.worldevents.api.core.WorldEventAction;
import org.nevermined.worldevents.api.core.WorldEventManagerApi;

public interface WorldEventsApi {
    void registerWorldEventAction(String key, WorldEventAction action);

    JavaPlugin getPlugin();
    WorldEventManagerApi getWorldEventManager();
    GlobalConfigApi getGlobalConfig();
}
