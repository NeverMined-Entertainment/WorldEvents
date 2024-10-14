package org.nevermined.worldevents.expansion;

import me.wyne.wutils.log.Log;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.nevermined.worldevents.api.core.EventData;
import org.nevermined.worldevents.api.core.WorldEventAction;
import org.nevermined.worldevents.api.core.WorldEventApi;
import org.nevermined.worldevents.api.core.WorldEventQueueApi;

import java.time.Instant;

public class DemoExpansion implements WorldEventAction {

    @Override
    public void startEvent(WorldEventApi worldEvent, WorldEventQueueApi queue) {
        EventData eventData = worldEvent.getEventData();
        Log.global.warn("Event started!");
        Log.global.warn(LegacyComponentSerializer.legacyAmpersand().serialize(eventData.name()));
        eventData.description().stream().map(c -> LegacyComponentSerializer.legacyAmpersand().serialize(c)).forEach(Log.global::warn);
        Log.global.warn("Chance: " + eventData.chancePercent());
        Log.global.warn("Duration (sec): " + eventData.durationSeconds());
        Log.global.warn("Cooldown (sec): " + eventData.cooldownSeconds());
        Log.global.warn("Start: " + Instant.now().toString());
        Log.global.warn("Finish: " + Instant.now().plusSeconds(eventData.durationSeconds()).toString());
    }

    @Override
    public void stopEvent(WorldEventApi worldEvent, WorldEventQueueApi queue) {
        EventData eventData = worldEvent.getEventData();
        Log.global.warn("Event " + LegacyComponentSerializer.legacyAmpersand().serialize(eventData.name()) + " stopped!");
    }

}
