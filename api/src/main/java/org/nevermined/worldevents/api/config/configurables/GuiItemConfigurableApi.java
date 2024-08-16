package org.nevermined.worldevents.api.config.configurables;

import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.Map;

public interface GuiItemConfigurableApi {

    GuiItemConfigurableApi applyPlaceholders(Player player);
    GuiItemConfigurableApi applyReplacements(Map<String, Component> replacements);
    GuiItem build();
    GuiItem build(GuiAction<InventoryClickEvent> action);
    int getSlot();
    Material getMaterial();
    Component getName();
    List<Component> getLore();
    Component getPrint();
    Sound getSound();


}
