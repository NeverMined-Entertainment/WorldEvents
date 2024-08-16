package org.nevermined.worldevents.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import me.clip.placeholderapi.PlaceholderAPI;
import me.wyne.wutils.i18n.I18n;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.nevermined.worldevents.api.config.GlobalConfigApi;
import org.nevermined.worldevents.api.core.WorldEventManagerApi;
import org.nevermined.worldevents.api.core.WorldEventQueueApi;

import java.util.ArrayList;
import java.util.List;

public class QueuesGui extends org.nevermined.worldevents.gui.PaginatedGui {

    public QueuesGui(MainGui mainGui, GlobalConfigApi config, WorldEventManagerApi eventManager, Player player)
    {
        gui = Gui.paginated()
                .title(I18n.global.getPlaceholderComponent(player.locale(), player, "queues-gui-header"))
                .rows(6)
                .disableAllInteractions()
                .create();
        gui.getFiller().fillBorder(ItemBuilder.from(config.commonGuiConfig().getBorder()).asGuiItem());
        buildNavigation(mainGui, config.commonGuiConfig(), player);
        populatePage(config, eventManager, player);
    }

    private void populatePage(GlobalConfigApi config, WorldEventManagerApi eventManager, Player player)
    {
        for (String queueKey : eventManager.getEventQueueMap().keySet())
        {
            WorldEventQueueApi queue = eventManager.getEventQueueMap().get(queueKey);

            List<Component> lore = new ArrayList<>(queue.getQueueData().description());
            lore.add(MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, I18n.global.getString(player.locale(), "queue-capacity-format").replace("<queue-key>", queueKey))));
            lore.add(MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, I18n.global.getString(player.locale(), "queue-active-format").replace("<queue-key>", queueKey))));

            gui.addItem(ItemBuilder.from(queue.getQueueData().item())
                    .name(queue.getQueueData().name())
                    .lore(lore)
                    .asGuiItem(event -> {
                        new QueueGui(this, queueKey, config, eventManager, player).openGui(player);
                    }));
        }
    }

}
