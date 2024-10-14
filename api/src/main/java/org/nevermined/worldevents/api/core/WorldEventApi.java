package org.nevermined.worldevents.api.core;

import org.jetbrains.annotations.Nullable;
import org.nevermined.worldevents.api.core.exception.AlreadyActiveException;
import org.nevermined.worldevents.api.core.exception.AlreadyInactiveException;
import org.nevermined.worldevents.api.wrapper.PromiseWrapper;

import java.time.Instant;
import java.util.Optional;

public interface WorldEventApi {

    void startEvent(WorldEventQueueApi queue) throws AlreadyActiveException;
    void stopEvent(WorldEventQueueApi queue) throws AlreadyInactiveException;

    EventData getEventData();
    WorldEventAction getAction();
    @Nullable PromiseWrapper<Void> getStopPromise();
    boolean isActive();
    Optional<Instant> getExpireTime();
    void setExpireTime(@Nullable Instant expireTime);

}
