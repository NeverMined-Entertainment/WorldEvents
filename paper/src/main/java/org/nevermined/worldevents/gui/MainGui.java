package org.nevermined.worldevents.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import me.wyne.wutils.config.configurables.GuiConfigurable;
import me.wyne.wutils.config.configurables.gui.attribute.GuiActionAttribute;
import me.wyne.wutils.i18n.I18n;
import org.bukkit.entity.Player;
import org.nevermined.worldevents.api.core.WorldEventManagerApi;
import org.nevermined.worldevents.config.GlobalConfig;
import org.nevermined.worldevents.config.MainGuiConfig;

public class MainGui extends BaseGui<Gui> {

    public MainGui(GlobalConfig globalConfig, WorldEventManagerApi worldEventManager, Player player)
    {
        gui = Gui.gui()
                .title(I18n.global.accessor(player.locale(), "main-gui-header").getPlaceholderComponent(player).get())
                .rows(1)
                .disableAllInteractions()
                .create();
        gui.getFiller().fill(ItemBuilder.from(globalConfig.mainGuiConfig().getFiller()).asGuiItem());
        buildQueuesItem(globalConfig, worldEventManager, player);
    }

    private void buildQueuesItem(GlobalConfig globalConfig, WorldEventManagerApi worldEventManager, Player player)
    {
        MainGuiConfig mainGuiConfig = globalConfig.mainGuiConfig();
        GuiConfigurable queuesConfig = mainGuiConfig.getQueuesItemImpl();
        GuiItem queuesItem = queuesConfig
                .<GuiConfigurable>getImmutableAccessor()
                .with(new GuiActionAttribute(event ->
                        new QueuesGui(this, globalConfig, worldEventManager, player).openGui(player)))
                .buildGuiItem(player);
        gui.setItem(queuesConfig.getSlot(), queuesItem);
    }

}
