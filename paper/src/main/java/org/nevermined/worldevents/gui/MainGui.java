package org.nevermined.worldevents.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import me.wyne.wutils.config.configurables.GuiItemConfigurable;
import me.wyne.wutils.i18n.I18n;
import org.bukkit.entity.Player;
import org.nevermined.worldevents.api.config.GlobalConfigApi;
import org.nevermined.worldevents.api.core.WorldEventManagerApi;
import org.nevermined.worldevents.config.MainGuiConfig;

public class MainGui extends BaseGui<Gui> {

    public MainGui(GlobalConfigApi globalConfig, WorldEventManagerApi worldEventManager, Player player)
    {
        gui = Gui.gui()
                .title(I18n.global.getLegacyPlaceholderComponent(player.locale(), player, "main-gui-header"))
                .rows(1)
                .disableAllInteractions()
                .create();
        gui.getFiller().fill(ItemBuilder.from(globalConfig.mainGuiConfig().getFiller()).asGuiItem());
        buildQueuesItem(globalConfig, worldEventManager, player);
    }

    private void buildQueuesItem(GlobalConfigApi globalConfig, WorldEventManagerApi worldEventManager, Player player)
    {
        MainGuiConfig mainGuiConfig = (MainGuiConfig)globalConfig.mainGuiConfig();
        GuiItemConfigurable queuesConfig = mainGuiConfig.getQueuesItemImpl();
        GuiItem queuesItem = queuesConfig.buildLegacyGuiItem(event ->
                        new QueuesGui(this, globalConfig, worldEventManager, player).openGui(player),
                        player);
        gui.setItem(queuesConfig.getSlot(), queuesItem);
    }

}
