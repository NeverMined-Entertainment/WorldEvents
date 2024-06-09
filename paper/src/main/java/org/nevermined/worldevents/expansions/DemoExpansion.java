package org.nevermined.worldevents.expansions;

import me.wyne.wutils.log.Log;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.nevermined.worldevents.api.core.EventData;
import org.nevermined.worldevents.api.core.WorldEventAction;

import java.time.Instant;

public class DemoExpansion implements WorldEventAction {

    @Override
    public void startEvent(EventData eventData) {
        Log.global.info("Event started!");
        Log.global.info(LegacyComponentSerializer.legacyAmpersand().serialize(eventData.name()));
        eventData.description().stream().map(c -> LegacyComponentSerializer.legacyAmpersand().serialize(c)).forEach(Log.global::info);
        Log.global.info("Chance: " + eventData.chancePercent());
        Log.global.info("Duration (sec): " + eventData.durationSeconds());
        Log.global.info("Cooldown (sec): " + eventData.cooldownSeconds());
        Log.global.info("Start: " + Instant.now().toString());
        Log.global.info("Finish: " + Instant.now().plusSeconds(eventData.durationSeconds()).toString());
    }

    @Override
    public void finishEvent(EventData eventData) {
        Log.global.info("Event finished!");
    }

}
