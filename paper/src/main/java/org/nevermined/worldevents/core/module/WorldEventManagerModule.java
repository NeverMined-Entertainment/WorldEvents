package org.nevermined.worldevents.core.module;

import com.google.inject.AbstractModule;
import org.nevermined.worldevents.api.core.WorldEventManagerApi;
import org.nevermined.worldevents.core.WorldEventManager;

public class WorldEventManagerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(WorldEventManagerApi.class).to(WorldEventManager.class);
    }

}
