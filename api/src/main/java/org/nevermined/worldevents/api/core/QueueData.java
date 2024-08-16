package org.nevermined.worldevents.api.core;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;

import java.util.List;

public record QueueData(String key, Component name, List<Component> description, Material item, int capacity) {
}
