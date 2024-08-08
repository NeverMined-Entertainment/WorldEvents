package org.nevermined.worldevents.api.config;

import org.nevermined.worldevents.api.config.configurables.GuiItemConfigurableApi;

public interface QueueGuiConfigApi {

    int getQueueGuiInfoSlot();
    GuiItemConfigurableApi getQueueActivate();
    GuiItemConfigurableApi getQueueDeactivate();

}
