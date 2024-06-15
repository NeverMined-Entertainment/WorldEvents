package org.nevermined.worldevents.api.core;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;

import java.util.List;

public record QueueData(Component name, List<Component> description, Material item, int capacity) {
}
