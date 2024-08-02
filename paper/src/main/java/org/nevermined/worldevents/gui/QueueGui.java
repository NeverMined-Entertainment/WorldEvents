package org.nevermined.worldevents.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import me.clip.placeholderapi.PlaceholderAPI;
import me.wyne.wutils.i18n.I18n;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.nevermined.worldevents.api.config.GlobalConfigApi;
import org.nevermined.worldevents.api.core.WorldEventApi;
import org.nevermined.worldevents.api.core.WorldEventManagerApi;
import org.nevermined.worldevents.api.core.WorldEventQueueApi;

import java.util.ArrayList;
import java.util.List;

public class QueueGui extends PaginatedGui {

    public QueueGui(QueuesGui queuesGui, String queueKey, GlobalConfigApi config, WorldEventManagerApi eventManager, Player player)
    {
        gui = Gui.paginated()
                .title(MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, I18n.global.getString(player.locale(), "queue-gui-header").replace("<queue-key>", queueKey))))
                .rows(6)
                .disableAllInteractions()
                .create();
        gui.getFiller().fillBorder(ItemBuilder.from(config.commonGuiConfig().getBorder()).asGuiItem());
        gui.setItem(config.queueGuiConfig().getQueueGuiInfoSlot(), buildQueueInfo(queueKey, eventManager, player));
        buildNavigation(queuesGui, config.commonGuiConfig(), player);
        populatePage(queueKey, eventManager, player);
    }

    private GuiItem buildQueueInfo(String queueKey, WorldEventManagerApi eventManager, Player player)
    {
        WorldEventQueueApi queue = eventManager.getEventQueueMap().get(queueKey);

        List<Component> lore = new ArrayList<>(queue.getQueueData().description());
        lore.add(MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, I18n.global.getString(player.locale(), "queue-capacity-format").replace("<queue-key>", queueKey))));
        lore.add(MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, I18n.global.getString(player.locale(), "queue-active-format").replace("<queue-key>", queueKey))));

        return ItemBuilder.from(queue.getQueueData().item())
                .name(queue.getQueueData().name())
                .lore(lore)
                .asGuiItem();
    }

    private void populatePage(String queueKey, WorldEventManagerApi eventManager, Player player)
    {
        for (int i = 0; i < eventManager.getEventQueueMap().get(queueKey).getEventQueue().size(); i++)
        {
            WorldEventApi event = eventManager.getEventQueueMap().get(queueKey).getEventQueueAsList().get(i);

            List<Component> lore = new ArrayList<>(event.getEventData().description());
            lore.add(MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, I18n.global.getString(player.locale(), "event-chance-format").replace("<queue-key>", queueKey).replace("<event-index>", String.valueOf(i)))));
            lore.add(MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, I18n.global.getString(player.locale(), "event-duration-format").replace("<queue-key>", queueKey).replace("<event-index>", String.valueOf(i)))));
            lore.add(MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, I18n.global.getString(player.locale(), "event-cooldown-format").replace("<queue-key>", queueKey).replace("<event-index>", String.valueOf(i)))));
            lore.add(MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, I18n.global.getString(player.locale(), "event-expire-format").replace("<queue-key>", queueKey).replace("<event-index>", String.valueOf(i)))));
            lore.add(MiniMessage.miniMessage().deserialize(PlaceholderAPI.setPlaceholders(player, I18n.global.getString(player.locale(), "event-active-format").replace("<queue-key>", queueKey).replace("<event-index>", String.valueOf(i)))));

            gui.addItem(ItemBuilder.from(event.getEventData().item())
                    .name(event.getEventData().name())
                    .lore(lore)
                    .asGuiItem(clickEvent -> {

                    }));
        }
    }

}
