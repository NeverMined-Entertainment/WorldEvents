package org.nevermined.worldevents.api.core;

import org.jetbrains.annotations.NotNull;
import org.nevermined.worldevents.api.WorldEventsApi;

public abstract class WorldEventExpansion {

    private final WorldEventsApi api;

    public WorldEventExpansion(WorldEventsApi api)
    {
       this.api = api;
    }

    @NotNull
    public abstract String getKey();

    @NotNull
    public abstract WorldEventAction getAction();

    public WorldEventsApi getApi() {
        return api;
    }
}
