package org.nevermined.worldevents.api.expansion;

import org.jetbrains.annotations.NotNull;
import org.nevermined.worldevents.api.WorldEventsApi;
import org.nevermined.worldevents.api.core.WorldEventAction;

import java.io.File;
import java.time.Instant;
import java.util.function.Supplier;

public abstract class WorldEventExpansion {

    private final WorldEventsApi api;

    public WorldEventExpansion(WorldEventsApi api)
    {
       this.api = api;
    }

    @NotNull
    public abstract String getKey();

    @NotNull
    public abstract Supplier<WorldEventAction> getAction();

    @NotNull
    public ExpansionData getExpansionData() {
        return new ExpansionData(getKey(), getAction(),
                new File(getClass().getProtectionDomain().getCodeSource().getLocation().getFile()).getName(),
                getClass().getName(),
                Instant.now());
    }

    public WorldEventsApi getApi() {
        return api;
    }
}
