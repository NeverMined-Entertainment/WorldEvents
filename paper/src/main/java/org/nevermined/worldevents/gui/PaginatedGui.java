package org.nevermined.worldevents.gui;

import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.entity.Player;
import org.nevermined.worldevents.api.config.CommonGuiConfigApi;
import org.nevermined.worldevents.config.CommonGuiConfig;

public abstract class PaginatedGui extends BaseGui<dev.triumphteam.gui.guis.PaginatedGui> {

    protected void buildNavigation(BaseGui previousGui, CommonGuiConfigApi config, Player player)
    {
        CommonGuiConfig commonGuiConfig = (CommonGuiConfig)config;
        GuiItem navigationBack = commonGuiConfig.getNavigationBackImpl().buildLegacyGuiItem(event -> previousGui.openGui(player), player);
        GuiItem navigationPrevious = commonGuiConfig.getNavigationPreviousImpl().buildLegacyGuiItem(event -> gui.previous(), player);
        GuiItem navigationNext = commonGuiConfig.getNavigationNextImpl().buildLegacyGuiItem(event -> gui.next(), player);
        gui.setItem(config.getNavigationBack().getSlot(), navigationBack);
        gui.setItem(config.getNavigationPrevious().getSlot(), navigationPrevious);
        gui.setItem(config.getNavigationNext().getSlot(), navigationNext);
    }

}
