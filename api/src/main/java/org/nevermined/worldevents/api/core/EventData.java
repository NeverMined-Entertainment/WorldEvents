package org.nevermined.worldevents.api.core;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.nevermined.worldevents.api.expansions.ExpansionData;

import java.util.List;

public record EventData(String key, ExpansionData expansionData, Component name, List<Component> description, Material item, int chancePercent, long durationSeconds, long cooldownSeconds) {
}
