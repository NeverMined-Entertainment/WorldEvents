package org.nevermined.worldevents.api.config;

import org.bukkit.Material;
import org.nevermined.worldevents.api.config.configurables.GuiItemConfigurableApi;

public interface MainGuiConfigApi {

    Material getFiller();
    GuiItemConfigurableApi getQueuesItem();

}
