package org.nevermined.worldevents.gui;

import dev.triumphteam.gui.guis.GuiItem;
import me.wyne.wutils.config.configurables.GuiConfigurable;
import me.wyne.wutils.config.configurables.gui.attribute.GuiActionAttribute;
import org.bukkit.entity.Player;
import org.nevermined.worldevents.config.CommonGuiConfig;

public abstract class PaginatedGui extends BaseGui<dev.triumphteam.gui.guis.PaginatedGui> {

    protected void buildNavigation(BaseGui previousGui, CommonGuiConfig config, Player player)
    {
        GuiItem navigationBack = config.getNavigationBackImpl()
                .<GuiConfigurable>getImmutableAccessor()
                .with(new GuiActionAttribute(event -> previousGui.openGui(player)))
                .buildGuiItem(player);
        GuiItem navigationPrevious = config.getNavigationPreviousImpl()
                .<GuiConfigurable>getImmutableAccessor()
                .with(new GuiActionAttribute(event -> gui.previous()))
                .buildGuiItem(player);
        GuiItem navigationNext = config.getNavigationNextImpl()
                .<GuiConfigurable>getImmutableAccessor()
                .with(new GuiActionAttribute(event -> gui.next()))
                .buildGuiItem(player);
        gui.setItem(config.getNavigationBackImpl().getSlot(), navigationBack);
        gui.setItem(config.getNavigationPreviousImpl().getSlot(), navigationPrevious);
        gui.setItem(config.getNavigationNextImpl().getSlot(), navigationNext);
    }

}
