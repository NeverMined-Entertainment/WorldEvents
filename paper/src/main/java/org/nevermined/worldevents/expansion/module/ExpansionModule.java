package org.nevermined.worldevents.expansion.module;

import com.google.inject.AbstractModule;
import org.nevermined.worldevents.api.expansion.ExpansionRegistryApi;
import org.nevermined.worldevents.expansion.ExpansionLoader;
import org.nevermined.worldevents.expansion.ExpansionRegistry;

public class ExpansionModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ExpansionRegistryApi.class).to(ExpansionRegistry.class);
        bind(ExpansionLoader.class);
    }

}
