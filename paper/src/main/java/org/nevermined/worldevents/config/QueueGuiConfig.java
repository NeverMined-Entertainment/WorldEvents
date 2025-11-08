package org.nevermined.worldevents.config;

import com.google.inject.Singleton;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurables.GuiConfigurable;
import me.wyne.wutils.config.configurables.gui.attribute.SlotAttribute;
import me.wyne.wutils.config.configurables.item.attribute.MaterialAttribute;
import me.wyne.wutils.config.configurables.item.attribute.NameAttribute;
import org.bukkit.Material;

@Singleton
public class QueueGuiConfig {

    @ConfigEntry(section = "GUI.Queue Gui", path = "queue-gui-info-slot")
    private int queueGuiInfoSlot = 4;

    @ConfigEntry(section = "GUI.Queue Gui", path = "queue-gui-activate")
    private GuiConfigurable queueActivate = new GuiConfigurable(GuiConfigurable.builder()
            .with(new NameAttribute("queue-gui-activate"))
            .with(new MaterialAttribute(Material.GREEN_STAINED_GLASS_PANE))
            .with(new SlotAttribute(49))
            .buildImmutable());

    @ConfigEntry(section = "GUI.Queue Gui", path = "queue-gui-deactivate")
    private GuiConfigurable queueDeactivate = new GuiConfigurable(GuiConfigurable.builder()
            .with(new NameAttribute("queue-gui-deactivate"))
            .with(new MaterialAttribute(Material.RED_STAINED_GLASS_PANE))
            .with(new SlotAttribute(49))
            .buildImmutable());

    public int getQueueGuiInfoSlot() {
        return queueGuiInfoSlot;
    }

    public GuiConfigurable getQueueActivateImpl() {
        return queueActivate;
    }

    public GuiConfigurable getQueueDeactivateImpl() {
        return queueDeactivate;
    }

}
