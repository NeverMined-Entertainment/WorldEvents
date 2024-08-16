package org.nevermined.worldevents.gui;

import org.bukkit.entity.Player;

public abstract class BaseGui<GuiType extends dev.triumphteam.gui.guis.BaseGui> {

    protected GuiType gui;

    public void openGui(Player player)
    {
        gui.open(player);
    }

}
