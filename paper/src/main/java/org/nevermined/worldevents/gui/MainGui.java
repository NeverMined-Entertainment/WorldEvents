package org.nevermined.worldevents.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import me.wyne.wutils.i18n.I18n;
import org.bukkit.entity.Player;
import org.nevermined.worldevents.WorldEvents;
import org.nevermined.worldevents.api.config.configurables.GuiItemConfigurableApi;

public class MainGui extends BaseGui<Gui> {

    public MainGui(WorldEvents plugin, Player player)
    {
        gui = Gui.gui()
                .title(I18n.global.getPlaceholderComponent(player.locale(), player, "main-gui-header"))
                .rows(1)
                .disableAllInteractions()
                .create();
        gui.getFiller().fill(ItemBuilder.from(plugin.getGlobalConfig().mainGuiConfig().getFiller()).asGuiItem());
        buildQueuesItem(plugin, player);
    }

    private void buildQueuesItem(WorldEvents plugin, Player player)
    {
        GuiItemConfigurableApi queuesConfig = plugin.getGlobalConfig().mainGuiConfig().getQueuesItem();
        GuiItem queuesItem = queuesConfig
                .applyPlaceholders(player)
                .build(event ->
                        new QueuesGui(this, plugin.getGlobalConfig(), plugin.getWorldEventManager(), player).openGui(player));
        gui.setItem(queuesConfig.getSlot(), queuesItem);
    }

}
