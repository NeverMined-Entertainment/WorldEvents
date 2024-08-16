package org.nevermined.worldevents.api.core;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;

import java.util.List;

public record EventData(String key, Component name, List<Component> description, Material item, int chancePercent, long durationSeconds, long cooldownSeconds) {
}
