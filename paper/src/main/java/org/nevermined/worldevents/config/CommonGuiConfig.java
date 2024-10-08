package org.nevermined.worldevents.config;

import com.google.inject.Singleton;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurables.GuiItemConfigurable;
import me.wyne.wutils.config.configurables.MaterialConfigurable;
import org.bukkit.Material;
import org.nevermined.worldevents.api.config.CommonGuiConfigApi;
import org.nevermined.worldevents.api.config.configurable.GuiItemConfigurableApi;
import org.nevermined.worldevents.config.configurable.GuiItemConfigurableWrapper;

import java.util.ArrayList;

@Singleton
public class CommonGuiConfig implements CommonGuiConfigApi {

    @ConfigEntry(section = "GUI.Common Gui", path = "common-gui-border")
    private MaterialConfigurable border = new MaterialConfigurable(Material.CYAN_STAINED_GLASS_PANE);

    @ConfigEntry(section = "GUI.Common Gui", path = "common-gui-previous")
    private GuiItemConfigurable navigationPrevious = new GuiItemConfigurable(
            "common-gui-previous",
            Material.PAPER,
            48,
            -1,
            new ArrayList<>(),
            null, null
    );

    @ConfigEntry(section = "GUI.Common Gui", path = "common-gui-next")
    private GuiItemConfigurable navigationNext = new GuiItemConfigurable(
            "common-gui-next",
            Material.PAPER,
            50,
            -1,
            new ArrayList<>(),
            null, null
    );

    @ConfigEntry(section = "GUI.Common Gui", path = "common-gui-back")
    private GuiItemConfigurable navigationBack = new GuiItemConfigurable(
            "common-gui-back",
            Material.RED_STAINED_GLASS_PANE,
            53,
            -1,
            new ArrayList<>(),
            null, null
    );

    public Material getBorder() {
        return border.getMaterial();
    }

    @Override
    public GuiItemConfigurableApi getNavigationPrevious()
    {
        return new GuiItemConfigurableWrapper(navigationPrevious);
    }

    public GuiItemConfigurable getNavigationPreviousImpl()
    {
        return navigationPrevious;
    }

    @Override
    public GuiItemConfigurableApi getNavigationNext()
    {
        return new GuiItemConfigurableWrapper(navigationNext);
    }

    public GuiItemConfigurable getNavigationNextImpl()
    {
        return navigationNext;
    }

    @Override
    public GuiItemConfigurableApi getNavigationBack()
    {
        return new GuiItemConfigurableWrapper(navigationBack);
    }

    public GuiItemConfigurable getNavigationBackImpl()
    {
        return navigationBack;
    }

}
