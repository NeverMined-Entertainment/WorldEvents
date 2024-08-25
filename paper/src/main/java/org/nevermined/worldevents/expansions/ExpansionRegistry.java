package org.nevermined.worldevents.expansions;

import com.google.inject.Singleton;
import me.wyne.wutils.log.Log;
import org.nevermined.worldevents.api.core.exceptions.ExpansionRegistryException;
import org.nevermined.worldevents.api.expansions.ExpansionData;
import org.nevermined.worldevents.api.core.WorldEventAction;
import org.nevermined.worldevents.api.expansions.ExpansionRegistryApi;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Singleton
public class ExpansionRegistry implements ExpansionRegistryApi {

    private final Map<String, ExpansionData> registeredExpansions = new HashMap<>()
    {
        { put("Demo", new ExpansionData("Demo", DemoExpansion::new, "WorldEvents", "org.nevermined.worldevents.expansions.ExpansionRegistry", Instant.now())); }
    };

    @Override
    public void registerExpansion(String key, ExpansionData expansion) throws ExpansionRegistryException
    {
        if (registeredExpansions.containsKey(key))
            throw new ExpansionRegistryException("Can not register expansion '" + key + "' because expansion with this key already exists!");

        registeredExpansions.put(key, expansion);
        Log.global.info("Registered expansion '" + key + "'");
    }

    @Override
    public void registerExpansions(Map<String, ExpansionData> expansions) throws ExpansionRegistryException
    {
        expansions.forEach((key, action) -> {
            if (registeredExpansions.containsKey(key))
                throw new ExpansionRegistryException("Can not register expansion '" + key + "' because expansion with this key already exists!");

            registeredExpansions.put(key, action);
            Log.global.info("Registered expansion '" + key + "'");
        });
    }

    @Override
    public void unregisterExpansion(String key) throws ExpansionRegistryException
    {
        if (!registeredExpansions.containsKey(key))
            throw new ExpansionRegistryException("Can not unregister expansion '" + key + "' because it wasn't registered!");

        registeredExpansions.remove(key);
        Log.global.info("Unregistered expansion '" + key + "'");
    }

    @Override
    public Map<String, ExpansionData> getRegisteredExpansions() {
        return registeredExpansions;
    }

    @Override
    public Map<String, Supplier<WorldEventAction>> getActionTypeMap() {
        Map<String, Supplier<WorldEventAction>> actionTypeMap = new HashMap<>();
        registeredExpansions.forEach((key, expansion) -> actionTypeMap.put(key, expansion.actionSupplier()));
        return actionTypeMap;
    }

}
