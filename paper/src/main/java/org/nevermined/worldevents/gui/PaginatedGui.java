package org.nevermined.worldevents.gui;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.javatuples.Pair;
import org.nevermined.worldevents.api.config.CommonGuiConfigApi;

public abstract class PaginatedGui extends BaseGui<dev.triumphteam.gui.guis.PaginatedGui> {

    protected void buildNavigation(BaseGui previousGui, CommonGuiConfigApi config, Player player)
    {
        Pair<Integer, GuiItem> navigationBack = config.getNavigationBack(event -> previousGui.openGui(player));
        Pair<Integer, GuiItem> navigationPrevious = config.getNavigationPrevious(event -> gui.previous());
        Pair<Integer, GuiItem> navigationNext = config.getNavigationNext(event -> gui.next());
        gui.setItem(navigationBack.getValue0(), navigationBack.getValue1());
        gui.setItem(navigationPrevious.getValue0(), navigationPrevious.getValue1());
        gui.setItem(navigationNext.getValue0(), navigationNext.getValue1());
    }

}
