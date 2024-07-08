package org.nevermined.worldevents.api.config;

import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.javatuples.Pair;

public interface CommonGuiConfigApi {

    Material getBorder();
    Pair<Integer, GuiItem> getNavigationPrevious(GuiAction<InventoryClickEvent> action);
    Pair<Integer, GuiItem> getNavigationNext(GuiAction<InventoryClickEvent> action);
    Pair<Integer, GuiItem> getNavigationBack(GuiAction<InventoryClickEvent> action);

}
