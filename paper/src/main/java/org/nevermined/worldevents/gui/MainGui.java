package org.nevermined.worldevents.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import me.wyne.wutils.config.configurables.GuiItemConfigurable;
import me.wyne.wutils.i18n.I18n;
import org.bukkit.entity.Player;
import org.nevermined.worldevents.WorldEvents;
import org.nevermined.worldevents.config.MainGuiConfig;

public class MainGui extends BaseGui<Gui> {

    public MainGui(WorldEvents plugin, Player player)
    {
        gui = Gui.gui()
                .title(I18n.global.getLegacyPlaceholderComponent(player.locale(), player, "main-gui-header"))
                .rows(1)
                .disableAllInteractions()
                .create();
        gui.getFiller().fill(ItemBuilder.from(plugin.getGlobalConfig().mainGuiConfig().getFiller()).asGuiItem());
        buildQueuesItem(plugin, player);
    }

    private void buildQueuesItem(WorldEvents plugin, Player player)
    {
        MainGuiConfig mainGuiConfig = (MainGuiConfig)plugin.getGlobalConfig().mainGuiConfig();
        GuiItemConfigurable queuesConfig = mainGuiConfig.getQueuesItemImpl();
        GuiItem queuesItem = queuesConfig.buildLegacyGuiItem(event ->
                        new QueuesGui(this, plugin.getGlobalConfig(), plugin.getWorldEventManager(), player).openGui(player),
                        player);
        gui.setItem(queuesConfig.getSlot(), queuesItem);
    }

}
