package org.nevermined.worldevents.api.core;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.nevermined.worldevents.api.expansion.ExpansionData;
import org.nevermined.worldevents.api.expansion.WorldEventExpansion;

import java.util.List;

public record EventData(String key, WorldEventExpansion expansion, Component name, List<Component> description, Material item, int chancePercent, long durationSeconds, long cooldownSeconds) {
}
