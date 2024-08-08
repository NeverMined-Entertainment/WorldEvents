package org.nevermined.worldevents.api.core;

import me.lucko.helper.promise.Promise;
import org.nevermined.worldevents.api.core.exceptions.AlreadyActiveException;
import org.nevermined.worldevents.api.core.exceptions.AlreadyInactiveException;

import java.time.Instant;
import java.util.Optional;

public interface WorldEventApi {

    void startEvent(WorldEventQueueApi queue) throws AlreadyActiveException;
    void stopEvent(WorldEventQueueApi queue) throws AlreadyInactiveException;

    EventData getEventData();
    WorldEventAction getAction();
    Promise<Void> getStopPromise();
    boolean isActive();
    Optional<Instant> getExpireTime();
    void setExpireTime(Instant expireTime);

}
