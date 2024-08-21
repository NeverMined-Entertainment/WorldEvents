package org.nevermined.worldevents.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.language.replacement.Placeholder;
import net.kyori.adventure.text.Component;
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
                .title(I18n.global.getLegacyPlaceholderComponent(player.locale(), player, "queues-gui-header"))
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
            lore.add(I18n.global.getLegacyPlaceholderComponent(player.locale(), player, "queue-capacity-format", Placeholder.replace("queue-key", queueKey)));
            lore.add(I18n.global.getLegacyPlaceholderComponent(player.locale(), player, "queue-active-format", Placeholder.replace("queue-key", queueKey)));

            gui.addItem(ItemBuilder.from(queue.getQueueData().item())
                    .name(queue.getQueueData().name())
                    .lore(lore)
                    .asGuiItem(event -> {
                        new QueueGui(this, queueKey, config, eventManager, player).openGui(player);
                    }));
        }
    }

}
