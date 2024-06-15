package org.nevermined.worldevents.config;

import com.google.inject.Singleton;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import me.wyne.wutils.config.ConfigEntry;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.javatuples.Pair;
import org.nevermined.worldevents.api.config.QueuesGuiConfigApi;
import org.nevermined.worldevents.config.configurables.GuiItemConfigurable;
import org.nevermined.worldevents.config.configurables.MaterialConfigurable;

import java.util.ArrayList;

@Singleton
public class QueuesGuiConfig implements QueuesGuiConfigApi {

    @ConfigEntry(section = "GUI.Queues Gui", path = "queues-gui-border")
    private MaterialConfigurable border = new MaterialConfigurable(Material.CYAN_STAINED_GLASS_PANE);

    @ConfigEntry(section = "GUI.Queues Gui", path = "queues-gui-previous")
    private GuiItemConfigurable navigationPrevious = new GuiItemConfigurable(
            48,
            Material.PAPER,
            "<!i>< Назад",
            new ArrayList<>(),
            null, null
    );

    @ConfigEntry(section = "GUI.Queues Gui", path = "queues-gui-next")
    private GuiItemConfigurable navigationNext = new GuiItemConfigurable(
            50,
            Material.PAPER,
            "<!i>Вперед >",
            new ArrayList<>(),
            null, null
    );

    @ConfigEntry(section = "GUI.Queues Gui", path = "queues-gui-back")
    private GuiItemConfigurable navigationBack = new GuiItemConfigurable(
            53,
            Material.RED_STAINED_GLASS_PANE,
            "<!i><b><red>Назад",
            new ArrayList<>(),
            null, null
    );

    @Override
    public Material getBorder() {
        return border.getMaterial();
    }

    @Override
    public Pair<Integer, GuiItem> getNavigationPrevious(GuiAction<InventoryClickEvent> action)
    {
        return new Pair<>(navigationPrevious.getSlot(), navigationPrevious.build(action));
    }

    @Override
    public Pair<Integer, GuiItem> getNavigationNext(GuiAction<InventoryClickEvent> action)
    {
        return new Pair<>(navigationNext.getSlot(), navigationNext.build(action));
    }

    @Override
    public Pair<Integer, GuiItem> getNavigationBack(GuiAction<InventoryClickEvent> action)
    {
        return new Pair<>(navigationBack.getSlot(), navigationBack.build(action));
    }
}
