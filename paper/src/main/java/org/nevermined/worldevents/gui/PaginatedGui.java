package org.nevermined.worldevents.gui;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.nevermined.worldevents.api.config.CommonGuiConfigApi;

public abstract class PaginatedGui extends BaseGui<dev.triumphteam.gui.guis.PaginatedGui> {

    protected void buildNavigation(BaseGui previousGui, CommonGuiConfigApi config, Player player)
    {
        GuiItem navigationBack = config.getNavigationBack().applyPlaceholders(player).build(event -> previousGui.openGui(player));
        GuiItem navigationPrevious = config.getNavigationPrevious().applyPlaceholders(player).build(event -> gui.previous());
        GuiItem navigationNext = config.getNavigationNext().applyPlaceholders(player).build(event -> gui.next());
        gui.setItem(config.getNavigationBack().getSlot(), navigationBack);
        gui.setItem(config.getNavigationPrevious().getSlot(), navigationPrevious);
        gui.setItem(config.getNavigationNext().getSlot(), navigationNext);
    }

}
