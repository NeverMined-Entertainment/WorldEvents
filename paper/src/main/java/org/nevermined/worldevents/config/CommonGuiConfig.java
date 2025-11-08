package org.nevermined.worldevents.config;

import com.google.inject.Singleton;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurables.GuiConfigurable;
import me.wyne.wutils.config.configurables.MaterialConfigurable;
import me.wyne.wutils.config.configurables.gui.attribute.SlotAttribute;
import me.wyne.wutils.config.configurables.item.attribute.MaterialAttribute;
import me.wyne.wutils.config.configurables.item.attribute.NameAttribute;
import org.bukkit.Material;

import java.util.ArrayList;

@Singleton
public class CommonGuiConfig {

    @ConfigEntry(section = "GUI.Common Gui", path = "common-gui-border")
    private MaterialConfigurable border = new MaterialConfigurable(Material.CYAN_STAINED_GLASS_PANE);

    @ConfigEntry(section = "GUI.Common Gui", path = "common-gui-previous")
    private GuiConfigurable navigationPrevious = new GuiConfigurable(GuiConfigurable.builder()
            .with(new NameAttribute("common-gui-previous"))
            .with(new MaterialAttribute(Material.PAPER))
            .with(new SlotAttribute(48))
            .buildImmutable());

    @ConfigEntry(section = "GUI.Common Gui", path = "common-gui-next")
    private GuiConfigurable navigationNext = new GuiConfigurable(GuiConfigurable.builder()
            .with(new NameAttribute("common-gui-next"))
            .with(new MaterialAttribute(Material.PAPER))
            .with(new SlotAttribute(50))
            .buildImmutable());

    @ConfigEntry(section = "GUI.Common Gui", path = "common-gui-back")
    private GuiConfigurable navigationBack = new GuiConfigurable(GuiConfigurable.builder()
            .with(new NameAttribute("common-gui-back"))
            .with(new MaterialAttribute(Material.RED_STAINED_GLASS_PANE))
            .with(new SlotAttribute(53))
            .buildImmutable());

    public Material getBorder() {
        return border.getMaterial();
    }

    public GuiConfigurable getNavigationPreviousImpl()
    {
        return navigationPrevious;
    }

    public GuiConfigurable getNavigationNextImpl()
    {
        return navigationNext;
    }

    public GuiConfigurable getNavigationBackImpl()
    {
        return navigationBack;
    }

}
