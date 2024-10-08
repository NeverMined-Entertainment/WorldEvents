package org.nevermined.worldevents.config;

import com.google.inject.Singleton;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.configurables.GuiItemConfigurable;
import me.wyne.wutils.config.configurables.MaterialConfigurable;
import org.bukkit.Material;
import org.nevermined.worldevents.api.config.MainGuiConfigApi;
import org.nevermined.worldevents.api.config.configurable.GuiItemConfigurableApi;
import org.nevermined.worldevents.config.configurable.GuiItemConfigurableWrapper;

import java.util.ArrayList;

@Singleton
public class MainGuiConfig implements MainGuiConfigApi {

    @ConfigEntry(section = "GUI.Main Gui", path = "main-gui-filler")
    private MaterialConfigurable filler = new MaterialConfigurable(Material.CYAN_STAINED_GLASS_PANE);

    @ConfigEntry(section = "GUI.Main Gui", path = "main-gui-queues")
    private GuiItemConfigurable queuesItem = new GuiItemConfigurable(
            "main-gui-queues",
            Material.CHEST,
            4,
            -1,
            new ArrayList<>(),
            null, null
    );

    @Override
    public Material getFiller() {
        return filler.getMaterial();
    }

    @Override
    public GuiItemConfigurableApi getQueuesItem() {
        return new GuiItemConfigurableWrapper(queuesItem);
    }

    public GuiItemConfigurable getQueuesItemImpl() {
        return queuesItem;
    }
}
