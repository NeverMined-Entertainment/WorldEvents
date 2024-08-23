package org.nevermined.worldevents.expansions.modules;

import com.google.inject.AbstractModule;
import org.nevermined.worldevents.api.expansions.ExpansionRegistryApi;
import org.nevermined.worldevents.expansions.ExpansionLoader;
import org.nevermined.worldevents.expansions.ExpansionRegistry;

public class ExpansionModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ExpansionRegistryApi.class).to(ExpansionRegistry.class);
        bind(ExpansionLoader.class);
    }

}
