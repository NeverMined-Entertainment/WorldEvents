package org.nevermined.worldevents.api.config;

import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.javatuples.Pair;

public interface MainGuiConfigApi {

    Material getFiller();
    Pair<Integer, GuiItem> getQueuesItem(GuiAction<InventoryClickEvent> action);

}
