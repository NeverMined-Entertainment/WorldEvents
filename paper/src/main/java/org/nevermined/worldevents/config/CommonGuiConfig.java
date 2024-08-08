package org.nevermined.worldevents.config;

import com.google.inject.Singleton;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import me.wyne.wutils.config.ConfigEntry;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.javatuples.Pair;
import org.nevermined.worldevents.api.config.CommonGuiConfigApi;
import org.nevermined.worldevents.api.config.configurables.GuiItemConfigurableApi;
import org.nevermined.worldevents.config.configurables.GuiItemConfigurable;
import org.nevermined.worldevents.config.configurables.MaterialConfigurable;

import java.util.ArrayList;

@Singleton
public class CommonGuiConfig implements CommonGuiConfigApi {

    @ConfigEntry(section = "GUI.Common Gui", path = "common-gui-border")
    private MaterialConfigurable border = new MaterialConfigurable(Material.CYAN_STAINED_GLASS_PANE);

    @ConfigEntry(section = "GUI.Common Gui", path = "common-gui-previous")
    private GuiItemConfigurable navigationPrevious = new GuiItemConfigurable(
            48,
            Material.PAPER,
            "common-gui-previous",
            new ArrayList<>(),
            null, null
    );

    @ConfigEntry(section = "GUI.Common Gui", path = "common-gui-next")
    private GuiItemConfigurable navigationNext = new GuiItemConfigurable(
            50,
            Material.PAPER,
            "common-gui-next",
            new ArrayList<>(),
            null, null
    );

    @ConfigEntry(section = "GUI.Common Gui", path = "common-gui-back")
    private GuiItemConfigurable navigationBack = new GuiItemConfigurable(
            53,
            Material.RED_STAINED_GLASS_PANE,
            "common-gui-back",
            new ArrayList<>(),
            null, null
    );

    public Material getBorder() {
        return border.getMaterial();
    }

    @Override
    public GuiItemConfigurableApi getNavigationPrevious()
    {
        return navigationPrevious;
    }

    @Override
    public GuiItemConfigurableApi getNavigationNext()
    {
        return navigationNext;
    }

    @Override
    public GuiItemConfigurableApi getNavigationBack()
    {
        return navigationBack;
    }

}
