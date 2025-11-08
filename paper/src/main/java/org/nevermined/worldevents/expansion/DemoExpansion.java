package org.nevermined.worldevents.expansion;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.nevermined.worldevents.WorldEvents;
import org.nevermined.worldevents.api.core.EventData;
import org.nevermined.worldevents.api.core.WorldEventAction;
import org.nevermined.worldevents.api.core.WorldEventApi;
import org.nevermined.worldevents.api.core.WorldEventQueueApi;
import org.nevermined.worldevents.api.expansion.WorldEventExpansion;

import java.time.Instant;
import java.util.function.Supplier;

public class DemoExpansion extends WorldEventExpansion implements WorldEventAction {

    @Override
    public @NotNull String getKey() {
        return "demo";
    }

    @Override
    public @NotNull Supplier<WorldEventAction> getAction() {
        return () -> this;
    }

    @Override
    public void startEvent(WorldEventApi worldEvent, WorldEventQueueApi queue) {
        EventData eventData = worldEvent.getEventData();
        WorldEvents.getInstance().getLog().warn("Event started!");
        WorldEvents.getInstance().getLog().warn(LegacyComponentSerializer.legacyAmpersand().serialize(eventData.name()));
        eventData.description().stream().map(c -> LegacyComponentSerializer.legacyAmpersand().serialize(c)).forEach(WorldEvents.getInstance().getLog()::warn);
        WorldEvents.getInstance().getLog().warn("Chance: " + eventData.chancePercent());
        WorldEvents.getInstance().getLog().warn("Duration (sec): " + eventData.durationSeconds());
        WorldEvents.getInstance().getLog().warn("Cooldown (sec): " + eventData.cooldownSeconds());
        WorldEvents.getInstance().getLog().warn("Start: " + Instant.now().toString());
        WorldEvents.getInstance().getLog().warn("Finish: " + Instant.now().plusSeconds(eventData.durationSeconds()).toString());
    }

    @Override
    public void stopEvent(WorldEventApi worldEvent, WorldEventQueueApi queue) {
        EventData eventData = worldEvent.getEventData();
        WorldEvents.getInstance().getLog().warn("Event " + LegacyComponentSerializer.legacyAmpersand().serialize(eventData.name()) + " stopped!");
    }

}
