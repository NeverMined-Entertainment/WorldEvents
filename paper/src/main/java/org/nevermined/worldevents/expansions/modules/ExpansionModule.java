package org.nevermined.worldevents.expansions.modules;

import com.google.inject.AbstractModule;
import org.nevermined.worldevents.expansions.ExpansionLoader;
import org.nevermined.worldevents.expansions.ExpansionRegistry;

import java.util.Map;

public class ExpansionModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ExpansionRegistry.class);
        bind(ExpansionLoader.class);
    }

}
