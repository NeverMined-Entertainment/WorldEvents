package org.nevermined.worldevents.api.core;

import java.time.Instant;

public interface WorldEventFactoryApi {

    WorldEventApi create(EventData eventData, WorldEventAction worldEventAction);
    WorldEventApi create(EventData eventData, WorldEventAction worldEventAction, Instant expireTime);

}
