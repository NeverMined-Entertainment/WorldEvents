package org.nevermined.worldevents.expansions.modules;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import org.nevermined.worldevents.api.core.WorldEventAction;
import org.nevermined.worldevents.expansions.DemoExpansion;

public class ExpansionModule extends AbstractModule {

    @Override
    protected void configure() {
        MapBinder<String, WorldEventAction> actionTypeMap = MapBinder.newMapBinder(binder(), String.class, WorldEventAction.class);
        actionTypeMap.addBinding("Demo").to(DemoExpansion.class);
    }

}
