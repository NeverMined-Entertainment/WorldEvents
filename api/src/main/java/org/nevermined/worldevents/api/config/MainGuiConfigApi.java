package org.nevermined.worldevents.api.config;

import org.bukkit.Material;
import org.nevermined.worldevents.api.config.configurable.GuiItemConfigurableApi;

public interface MainGuiConfigApi {

    Material getFiller();
    GuiItemConfigurableApi getQueuesItem();

}
