package org.nevermined.worldevents.config;

import com.google.inject.Singleton;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurables.GuiConfigurable;
import me.wyne.wutils.config.configurables.MaterialConfigurable;
import me.wyne.wutils.config.configurables.gui.attribute.SlotAttribute;
import me.wyne.wutils.config.configurables.item.attribute.MaterialAttribute;
import me.wyne.wutils.config.configurables.item.attribute.NameAttribute;
import org.bukkit.Material;

@Singleton
public class MainGuiConfig {

    @ConfigEntry(section = "GUI.Main Gui", path = "main-gui-filler")
    private MaterialConfigurable filler = new MaterialConfigurable(Material.CYAN_STAINED_GLASS_PANE);

    @ConfigEntry(section = "GUI.Main Gui", path = "main-gui-queues")
    private GuiConfigurable queuesItem = new GuiConfigurable(GuiConfigurable.builder()
            .with(new NameAttribute("main-gui-queues"))
            .with(new MaterialAttribute(Material.CHEST))
            .with(new SlotAttribute(4))
            .buildImmutable());

    public Material getFiller() {
        return filler.getMaterial();
    }

    public GuiConfigurable getQueuesItemImpl() {
        return queuesItem;
    }
}
