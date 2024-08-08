package org.nevermined.worldevents.api.config;

import org.bukkit.Material;
import org.nevermined.worldevents.api.config.configurables.GuiItemConfigurableApi;

public interface CommonGuiConfigApi {

    Material getBorder();
    GuiItemConfigurableApi getNavigationPrevious();
    GuiItemConfigurableApi getNavigationNext();
    GuiItemConfigurableApi getNavigationBack();

}
