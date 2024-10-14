package org.nevermined.worldevents.config;

import com.google.inject.Singleton;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurables.GuiItemConfigurable;
import org.bukkit.Material;
import org.nevermined.worldevents.api.config.QueueGuiConfigApi;
import org.nevermined.worldevents.api.config.configurable.GuiItemConfigurableApi;
import org.nevermined.worldevents.config.configurable.GuiItemConfigurableWrapper;

import java.util.ArrayList;

@Singleton
public class QueueGuiConfig implements QueueGuiConfigApi {

    @ConfigEntry(section = "GUI.Queue Gui", path = "queue-gui-info-slot")
    private int queueGuiInfoSlot = 4;

    @ConfigEntry(section = "GUI.Queue Gui", path = "queue-gui-activate")
    private GuiItemConfigurable queueActivate = new GuiItemConfigurable(
            "queue-gui-activate",
            Material.GREEN_STAINED_GLASS_PANE,
            49,
            -1,
            new ArrayList<>(),
            null, null
    );

    @ConfigEntry(section = "GUI.Queue Gui", path = "queue-gui-deactivate")
    private GuiItemConfigurable queueDeactivate = new GuiItemConfigurable(
            "queue-gui-deactivate",
            Material.RED_STAINED_GLASS_PANE,
            49,
            -1,
            new ArrayList<>(),
            null, null
    );


    public int getQueueGuiInfoSlot() {
        return queueGuiInfoSlot;
    }

    @Override
    public GuiItemConfigurableApi getQueueActivate() {
        return new GuiItemConfigurableWrapper(queueActivate);
    }

    public GuiItemConfigurable getQueueActivateImpl() {
        return queueActivate;
    }

    @Override
    public GuiItemConfigurableApi getQueueDeactivate() {
        return new GuiItemConfigurableWrapper(queueDeactivate);
    }

    public GuiItemConfigurable getQueueDeactivateImpl() {
        return queueDeactivate;
    }

}
