package org.nevermined.worldevents.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.language.replacement.Placeholder;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.nevermined.worldevents.api.config.GlobalConfigApi;
import org.nevermined.worldevents.api.config.QueueGuiConfigApi;
import org.nevermined.worldevents.api.core.WorldEventApi;
import org.nevermined.worldevents.api.core.WorldEventManagerApi;
import org.nevermined.worldevents.api.core.WorldEventQueueApi;
import org.nevermined.worldevents.api.core.exceptions.AlreadyActiveException;
import org.nevermined.worldevents.api.core.exceptions.AlreadyInactiveException;

import java.util.ArrayList;
import java.util.List;

public class QueueGui extends PaginatedGui {

    public QueueGui(QueuesGui queuesGui, String queueKey, GlobalConfigApi config, WorldEventManagerApi eventManager, Player player)
    {
        gui = Gui.paginated()
                .title(I18n.global.getLegacyPlaceholderComponent(player.locale(), player, "queue-gui-header", Placeholder.replace("queue-key", queueKey)))
                .rows(6)
                .disableAllInteractions()
                .create();
        gui.getFiller().fillBorder(ItemBuilder.from(config.commonGuiConfig().getBorder()).asGuiItem());
        buildNavigation(queuesGui, config.commonGuiConfig(), player);
        updatePage(queueKey, config.queueGuiConfig(), eventManager, player);
    }

    private void updatePage(String queueKey, QueueGuiConfigApi config, WorldEventManagerApi eventManager, Player player)
    {
        gui.updateItem(config.getQueueGuiInfoSlot(), buildQueueInfo(queueKey, eventManager, player));
        buildActivator(config, queueKey, eventManager, player);
        gui.clearPageItems();
        populatePage(queueKey, eventManager, player);
        gui.update();
    }

    private GuiItem buildQueueInfo(String queueKey, WorldEventManagerApi eventManager, Player player)
    {
        WorldEventQueueApi queue = eventManager.getEventQueueMap().get(queueKey);

        List<Component> lore = new ArrayList<>(queue.getQueueData().description());
        lore.add(I18n.global.getLegacyPlaceholderComponent(player.locale(), player, "queue-capacity-format", Placeholder.replace("queue-key", queueKey)));
        lore.add(I18n.global.getLegacyPlaceholderComponent(player.locale(), player, "queue-active-format", Placeholder.replace("queue-key", queueKey)));

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
            lore.addAll(getEventLore(queueKey, String.valueOf(i), player, I18n.global.getStringList(player.locale(), "event-lore-order").toArray(String[]::new)));

            gui.addItem(ItemBuilder.from(event.getEventData().item())
                    .name(event.getEventData().name())
                    .lore(lore)
                    .asGuiItem(clickEvent -> {

                    }));
        }
    }

    private List<Component> getEventLore(String queueKey, String eventIndex, Player player, String ...lines)
    {
        List<Component> lore = new ArrayList<>();

        for (String line : lines)
        {
            lore.add(I18n.global.getLegacyPlaceholderComponent(player.locale(), player, line, Placeholder.replace("queue-key", queueKey), Placeholder.replace("event-index", eventIndex)));
        }

        return lore;
    }

    private void buildActivator(QueueGuiConfigApi config, String queueKey, WorldEventManagerApi eventManager, Player player)
    {
        if (config.getQueueActivate().getSlot() == config.getQueueDeactivate().getSlot())
            buildSingleActivator(config, queueKey, eventManager, player);
        else
            buildDoubleActivator(config, queueKey, eventManager, player);
    }

    private void buildDoubleActivator(QueueGuiConfigApi config, String queueKey, WorldEventManagerApi eventManager, Player player)
    {
        buildQueueActivator(config, queueKey, eventManager, player);
        buildQueueDeactivator(config, queueKey, eventManager, player);
    }

    private void buildSingleActivator(QueueGuiConfigApi config, String queueKey, WorldEventManagerApi eventManager, Player player)
    {
        if (eventManager.getEventQueueMap().get(queueKey).isActive())
            buildQueueDeactivator(config, queueKey, eventManager, player);
        else
            buildQueueActivator(config, queueKey, eventManager, player);
    }

    private void buildQueueActivator(QueueGuiConfigApi config, String queueKey, WorldEventManagerApi eventManager, Player player)
    {
        gui.updateItem(config.getQueueActivate().getSlot(), config.getQueueActivate().applyPlaceholders(player).build(event -> {
            try {
                eventManager.startEventQueue(queueKey);
                updatePage(queueKey, config, eventManager, player);
            } catch (AlreadyActiveException e)
            {
                player.sendMessage(I18n.global.getLegacyPlaceholderComponent(player.locale(), player, "error-queue-already-active",
                        Placeholder.legacy("queue-name", eventManager.getEventQueueMap().get(queueKey).getQueueData().name())));
            }
        }));
    }

    private void buildQueueDeactivator(QueueGuiConfigApi config, String queueKey, WorldEventManagerApi eventManager, Player player)
    {
        gui.updateItem(config.getQueueDeactivate().getSlot(), config.getQueueDeactivate().applyPlaceholders(player).build(event -> {
            try {
                eventManager.stopEventQueue(queueKey);
                updatePage(queueKey, config, eventManager, player);
            } catch (AlreadyInactiveException e)
            {
                player.sendMessage(I18n.global.getLegacyPlaceholderComponent(player.locale(), player, "error-queue-already-inactive",
                        Placeholder.legacy("queue-name", eventManager.getEventQueueMap().get(queueKey).getQueueData().name())));
            }
        }));
    }
}
