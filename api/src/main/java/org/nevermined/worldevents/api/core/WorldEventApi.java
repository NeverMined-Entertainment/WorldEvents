package org.nevermined.worldevents.api.core;

import me.lucko.helper.promise.Promise;

import java.time.Instant;
import java.util.Optional;

public interface WorldEventApi {

    void startEvent(WorldEventQueueApi queue);
    void stopEvent(WorldEventQueueApi queue);

    EventData getEventData();
    WorldEventAction getAction();
    Promise<Void> getStopPromise();
    boolean isActive();
    Optional<Instant> getExpireTime();
    void setExpireTime(Instant expireTime);

}
