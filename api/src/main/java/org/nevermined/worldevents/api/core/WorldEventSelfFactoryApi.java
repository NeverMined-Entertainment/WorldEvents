package org.nevermined.worldevents.api.core;

import java.time.Instant;

public interface WorldEventSelfFactoryApi extends WorldEventFactoryApi {

    WorldEventApi create();
    WorldEventApi create(Instant expireTime);
    EventData getEventData();
    WorldEventAction getAction();

}
