package org.nevermined.worldevents.expansion;

import com.google.inject.Singleton;
import me.wyne.wutils.log.Log;
import org.nevermined.worldevents.WorldEvents;
import org.nevermined.worldevents.api.core.exception.ExpansionRegistryException;
import org.nevermined.worldevents.api.core.WorldEventAction;
import org.nevermined.worldevents.api.expansion.ExpansionRegistryApi;
import org.nevermined.worldevents.api.expansion.WorldEventExpansion;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Singleton
public class ExpansionRegistry implements ExpansionRegistryApi {

    private final Map<String, WorldEventExpansion> registeredExpansions = new HashMap<>()
    {
        { put("demo", new DemoExpansion()); }
    };

    @Override
    public void registerExpansion(String key, WorldEventExpansion expansion) throws ExpansionRegistryException
    {
        if (registeredExpansions.containsKey(key))
            throw new ExpansionRegistryException("Can not register expansion '" + key + "' because expansion with this key already exists!");

        registeredExpansions.put(key, expansion);
        WorldEvents.getInstance().getLog().info("Registered expansion '" + key + "'");
    }

    @Override
    public void registerExpansions(Map<String, WorldEventExpansion> expansions) throws ExpansionRegistryException
    {
        expansions.forEach((key, action) -> {
            if (registeredExpansions.containsKey(key))
                throw new ExpansionRegistryException("Can not register expansion '" + key + "' because expansion with this key already exists!");

            registeredExpansions.put(key, action);
            WorldEvents.getInstance().getLog().info("Registered expansion '" + key + "'");
        });
    }

    @Override
    public void reloadExpansion(String key) {
        registeredExpansions.get(key).reload();
        WorldEvents.getInstance().getLog().info("Reloaded expansion '" + key + "'");
    }

    @Override
    public void reloadExpansions() {
        registeredExpansions.keySet().forEach(this::reloadExpansion);
    }

    @Override
    public Map<String, WorldEventExpansion> getRegisteredExpansions() {
        return registeredExpansions;
    }

    @Override
    public Map<String, Supplier<WorldEventAction>> getActionTypeMap() {
        Map<String, Supplier<WorldEventAction>> actionTypeMap = new HashMap<>();
        registeredExpansions.forEach((key, expansion) -> actionTypeMap.put(key, expansion.getAction()));
        return actionTypeMap;
    }

}
