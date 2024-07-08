package org.nevermined.worldevents.config;

import com.google.inject.Singleton;
import me.wyne.wutils.config.ConfigEntry;
import org.nevermined.worldevents.api.config.QueueGuiConfigApi;

@Singleton
public class QueueGuiConfig implements QueueGuiConfigApi {

    @ConfigEntry(section = "GUI.Queue Gui", path = "queue-gui-info-slot")
    private int queueGuiInfoSlot = 4;

    public int getQueueGuiInfoSlot() {
        return queueGuiInfoSlot;
    }
}
