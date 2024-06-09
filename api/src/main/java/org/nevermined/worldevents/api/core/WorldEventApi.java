package org.nevermined.worldevents.api.core;

import java.time.Instant;
import java.util.Optional;

public interface WorldEventApi {

    void startEvent(WorldEventQueueApi queue);
    void finishEvent(WorldEventQueueApi queue);

    EventData getEventData();
    WorldEventAction getAction();
    boolean isActive();
    Optional<Instant> getExpireTime();
    void setExpireTime(Instant expireTime);

}
