package org.nevermined.worldevents.api.expansion;

import org.nevermined.worldevents.api.core.WorldEventAction;
import org.nevermined.worldevents.api.core.exception.ExpansionRegistryException;

import java.util.Map;
import java.util.function.Supplier;

public interface ExpansionRegistryApi {

    void registerExpansion(String key, ExpansionData expansion) throws ExpansionRegistryException;
    void registerExpansions(Map<String, ExpansionData> expansions) throws ExpansionRegistryException;
    void unregisterExpansion(String key) throws ExpansionRegistryException;
    void clearExpansions();
    Map<String, ExpansionData> getRegisteredExpansions();
    Map<String, Supplier<WorldEventAction>> getActionTypeMap();

}
