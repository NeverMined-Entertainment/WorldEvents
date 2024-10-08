package org.nevermined.worldevents.api.config;

import org.bukkit.Material;
import org.nevermined.worldevents.api.config.configurable.GuiItemConfigurableApi;

public interface CommonGuiConfigApi {

    Material getBorder();
    GuiItemConfigurableApi getNavigationPrevious();
    GuiItemConfigurableApi getNavigationNext();
    GuiItemConfigurableApi getNavigationBack();

}
