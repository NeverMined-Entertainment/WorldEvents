package org.nevermined.worldevents.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.clip.placeholderapi.PlaceholderAPI;
import me.wyne.wutils.i18n.I18n;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.javatuples.Pair;
import org.nevermined.worldevents.api.config.QueuesGuiConfigApi;
import org.nevermined.worldevents.api.core.WorldEventManagerApi;
import org.nevermined.worldevents.api.core.WorldEventQueueApi;

import java.util.ArrayList;
import java.util.List;

public class QueuesGui {

    private PaginatedGui gui;

    public QueuesGui(MainGui mainGui, QueuesGuiConfigApi config, WorldEventManagerApi eventManager, Player player)
    {
        gui = Gui.paginated()
                .title(I18n.global.getPlaceholderComponent(player.locale(), player, "queues-gui-header"))
                .rows(6)
                .disableAllInteractions()
                .create();
        gui.getFiller().fillBorder(ItemBuilder.from(config.getBorder()).asGuiItem());
        buildNavigation(mainGui, config, player);
        populatePage(eventManager, player);
    }

    public void openGui(Player player)
    {
        gui.open(player);
    }

    private void buildNavigation(MainGui mainGui, QueuesGuiConfigApi config, Player player)
    {
        Pair<Integer, GuiItem> navigationBack = config.getNavigationBack(event -> mainGui.openGui(player));
        Pair<Integer, GuiItem> navigationPrevious = config.getNavigationPrevious(event -> gui.previous());
        Pair<Integer, GuiItem> navigationNext = config.getNavigationNext(event -> gui.next());
        gui.setItem(navigationBack.getValue0(), navigationBack.getValue1());
        gui.setItem(navigationPrevious.getValue0(), navigationPrevious.getValue1());
        gui.setItem(navigationNext.getValue0(), navigationNext.getValue1());
    }

    private void populatePage(WorldEventManagerApi eventManager, Player player)
    {
        for (String queueKey : eventManager.getEventQueueMap().keySet())
        {
            WorldEventQueueApi queue = eventManager.getEventQueueMap().get(queueKey);

            List<Component> lore = new ArrayList<>(queue.getQueueData().description());
            lore.add(MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, I18n.global.getString(player.locale(), "queue-capacity-format").replace("<queue-key>", queueKey))));

            gui.addItem(ItemBuilder.from(queue.getQueueData().item())
                    .name(queue.getQueueData().name())
                    .lore(lore)
                    .asGuiItem(event -> {

                    }));
        }
    }

}
