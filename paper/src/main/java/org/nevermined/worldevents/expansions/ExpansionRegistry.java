package org.nevermined.worldevents.expansions;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.wyne.wutils.log.Log;
import org.nevermined.worldevents.WorldEvents;
import org.nevermined.worldevents.api.core.WorldEventAction;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class ExpansionRegistry {

    private final WorldEvents plugin;

    private final Map<String, WorldEventAction> registeredExpansions = new HashMap<>()
    {
        { put("Demo", new DemoExpansion()); }
    };

    @Inject
    public ExpansionRegistry(WorldEvents plugin) {
        this.plugin = plugin;
    }

    public void registerExpansion(String key, WorldEventAction action)
    {
        if (registeredExpansions.containsKey(key))
        {
            Log.global.error("Can not register expansion '" + key + "' because expansion with this key already exists!");
            return;
        }

        registeredExpansions.put(key, action);
        plugin.getWorldEventManager().reloadEventQueues();
        Log.global.info("Registered expansion '" + key + "'");
    }

    public void registerExpansions(Map<String, WorldEventAction> expansions)
    {
        expansions.forEach((key, action) -> {
            if (registeredExpansions.containsKey(key))
            {
                Log.global.error("Can not register expansion '" + key + "' because expansion with this key already exists!");
                return;
            }

            registeredExpansions.put(key, action);
            Log.global.info("Registered expansion '" + key + "'");
        });
        plugin.getWorldEventManager().reloadEventQueues();
    }

    public void unregisterExpansion(String key)
    {
        if (!registeredExpansions.containsKey(key))
        {
            Log.global.warn("Can not unregister expansion '" + key + "' because it wasn't registered!");
            return;
        }

        registeredExpansions.remove(key);
        plugin.getWorldEventManager().reloadEventQueues();
        Log.global.info("Unregistered expansion '" + key + "'");
    }

    public Map<String, WorldEventAction> getRegisteredExpansions() {
        return registeredExpansions;
    }

}
