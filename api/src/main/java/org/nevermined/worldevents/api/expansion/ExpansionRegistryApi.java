package org.nevermined.worldevents.api.expansion;

import org.nevermined.worldevents.api.core.WorldEventAction;
import org.nevermined.worldevents.api.core.exception.ExpansionRegistryException;

import java.util.Map;
import java.util.function.Supplier;

public interface ExpansionRegistryApi {

    void registerExpansion(String key, WorldEventExpansion expansion) throws ExpansionRegistryException;
    void registerExpansions(Map<String, WorldEventExpansion> expansions) throws ExpansionRegistryException;
    void reloadExpansion(String key);
    void reloadExpansions();
    Map<String, WorldEventExpansion> getRegisteredExpansions();
    Map<String, Supplier<WorldEventAction>> getActionTypeMap();

}
