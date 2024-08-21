package org.nevermined.worldevents.expansions.modules;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import org.nevermined.worldevents.api.core.WorldEventAction;

import java.util.Map;

public class ExpansionModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(new TypeLiteral<Map<String, WorldEventAction>>(){}).toProvider(ExpansionProvider.class);
    }

}
