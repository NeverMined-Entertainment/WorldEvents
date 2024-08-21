package org.nevermined.worldevents.config;

import com.google.inject.Singleton;
import me.wyne.wutils.config.ConfigEntry;
import org.bukkit.Material;
import org.nevermined.worldevents.api.config.QueueGuiConfigApi;
import org.nevermined.worldevents.api.config.configurables.GuiItemConfigurableApi;
import org.nevermined.worldevents.config.configurables.GuiItemConfigurable;

import java.util.ArrayList;

@Singleton
public class QueueGuiConfig implements QueueGuiConfigApi {

    @ConfigEntry(section = "GUI.Queue Gui", path = "queue-gui-info-slot")
    private int queueGuiInfoSlot = 4;

    @ConfigEntry(section = "GUI.Queue Gui", path = "queue-gui-activate")
    private GuiItemConfigurableApi queueActivate = new GuiItemConfigurable(
            49,
            Material.GREEN_STAINED_GLASS_PANE,
            "queue-gui-activate",
            new ArrayList<>(),
            null, null
    );

    @ConfigEntry(section = "GUI.Queue Gui", path = "queue-gui-deactivate")
    private GuiItemConfigurableApi queueDeactivate = new GuiItemConfigurable(
            49,
            Material.RED_STAINED_GLASS_PANE,
            "queue-gui-deactivate",
            new ArrayList<>(),
            null, null
    );


    public int getQueueGuiInfoSlot() {
        return queueGuiInfoSlot;
    }

    @Override
    public GuiItemConfigurableApi getQueueActivate() {
        return queueActivate;
    }

    @Override
    public GuiItemConfigurableApi getQueueDeactivate() {
        return queueDeactivate;
    }
}
