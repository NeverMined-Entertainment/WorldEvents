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
        registeredExpansions.put(key, action);
        plugin.reloadEventQueues();
        Log.global.info("Registered WorldEvent expansion " + key);
    }

    public Map<String, WorldEventAction> getRegisteredExpansions() {
        return registeredExpansions;
    }

}
