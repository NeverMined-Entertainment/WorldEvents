package org.nevermined.worldevents.config;

import com.google.inject.Singleton;
import me.wyne.wutils.config.ConfigEntry;
import org.bukkit.Material;
import org.nevermined.worldevents.api.config.MainGuiConfigApi;
import org.nevermined.worldevents.api.config.configurables.GuiItemConfigurableApi;
import org.nevermined.worldevents.api.config.configurables.MaterialConfigurableApi;
import org.nevermined.worldevents.config.configurables.GuiItemConfigurable;
import org.nevermined.worldevents.config.configurables.MaterialConfigurable;

import java.util.ArrayList;

@Singleton
public class MainGuiConfig implements MainGuiConfigApi {

    @ConfigEntry(section = "GUI.Main Gui", path = "main-gui-filler")
    private MaterialConfigurableApi filler = new MaterialConfigurable(Material.CYAN_STAINED_GLASS_PANE);

    @ConfigEntry(section = "GUI.Main Gui", path = "main-gui-queues")
    private GuiItemConfigurableApi queuesItem = new GuiItemConfigurable(
            4,
            Material.CHEST,
            "main-gui-queues",
            new ArrayList<>(),
            null, null
    );

    @Override
    public Material getFiller() {
        return filler.getMaterial();
    }

    @Override
    public GuiItemConfigurableApi getQueuesItem() {
        return queuesItem;
    }
}
