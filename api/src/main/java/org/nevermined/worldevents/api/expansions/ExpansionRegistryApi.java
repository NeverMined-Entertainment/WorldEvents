package org.nevermined.worldevents.api.expansions;

import org.nevermined.worldevents.api.core.WorldEventAction;
import org.nevermined.worldevents.api.core.exceptions.ExpansionRegistryException;

import java.util.Map;
import java.util.function.Supplier;

public interface ExpansionRegistryApi {

    void registerExpansion(String key, ExpansionData expansion) throws ExpansionRegistryException;
    void registerExpansions(Map<String, ExpansionData> expansions) throws ExpansionRegistryException;
    void unregisterExpansion(String key) throws ExpansionRegistryException;
    Map<String, ExpansionData> getRegisteredExpansions();
    Map<String, Supplier<WorldEventAction>> getActionTypeMap();

}
