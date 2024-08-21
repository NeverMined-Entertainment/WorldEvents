package org.nevermined.worldevents.expansions.modules;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.nevermined.worldevents.api.core.WorldEventAction;
import org.nevermined.worldevents.expansions.ExpansionRegistry;

import java.util.Map;

public class ExpansionProvider implements Provider<Map<String, WorldEventAction>> {

    private final ExpansionRegistry registry;

    @Inject
    public ExpansionProvider(ExpansionRegistry registry)
    {
        this.registry = registry;
    }

    @Override
    public Map<String, WorldEventAction> get() {
        return registry.getRegisteredExpansions();
    }

}
