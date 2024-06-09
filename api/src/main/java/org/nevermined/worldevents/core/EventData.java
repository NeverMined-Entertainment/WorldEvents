package org.nevermined.worldevents.core;

import net.kyori.adventure.text.Component;

import java.util.List;

public record EventData(Component name, List<Component> description, int chancePercent, long durationSeconds, long cooldownSeconds) {
}
