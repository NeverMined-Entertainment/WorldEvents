package org.nevermined.worldevents;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.nevermined.worldevents.api.config.GlobalConfigApi;
import org.nevermined.worldevents.api.expansions.ExpansionData;
import org.nevermined.worldevents.api.core.WorldEventAction;
import org.nevermined.worldevents.api.core.WorldEventManagerApi;
import org.nevermined.worldevents.api.expansions.ExpansionRegistryApi;

import java.io.File;
import java.time.Instant;
import java.util.function.Supplier;

@Singleton
public class WorldEventsApi implements org.nevermined.worldevents.api.WorldEventsApi {

    private final WorldEvents plugin;
    private final ExpansionRegistryApi expansionRegistry;

    @Inject
    public WorldEventsApi(WorldEvents plugin, ExpansionRegistryApi expansionRegistry)
    {
        this.plugin = plugin;
        this.expansionRegistry = expansionRegistry;
        plugin.getServer().getServicesManager().register(org.nevermined.worldevents.api.WorldEventsApi.class, this, plugin, ServicePriority.Normal);
    }

    @Override
    public void registerWorldEventAction(String key, Supplier<WorldEventAction> actionSupplier) {
        Class<? extends WorldEventAction> actionClass = actionSupplier.get().getClass();
        expansionRegistry.registerExpansion(key, new ExpansionData(key, actionSupplier,
                new File(actionClass.getProtectionDomain().getCodeSource().getLocation().getFile()).getName(),
                actionClass.getName(),
                Instant.now()));
    }

    @Override
    public void registerWorldEventExpansion(ExpansionData expansionData) {
        expansionRegistry.registerExpansion(expansionData.key(), expansionData);
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
    public ExpansionRegistryApi getExpansionRegistry() {
        return expansionRegistry;
    }

    @Override
    public GlobalConfigApi getGlobalConfig() {
        return plugin.getGlobalConfig();
    }
}
