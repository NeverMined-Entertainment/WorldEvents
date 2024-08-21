package org.nevermined.worldevents.expansions.modules;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import org.nevermined.worldevents.api.core.WorldEventAction;
import org.nevermined.worldevents.expansions.ExpansionRegistry;

import java.util.Map;

public class ExpansionModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ExpansionRegistry.class);
        bind(new TypeLiteral<Map<String, WorldEventAction>>(){}).toProvider(ExpansionProvider.class);
    }

}
