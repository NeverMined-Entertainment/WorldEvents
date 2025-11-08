package org.nevermined.worldevents.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import me.wyne.wutils.config.configurables.GuiConfigurable;
import me.wyne.wutils.config.configurables.gui.attribute.GuiActionAttribute;
import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.language.component.LocalizedString;
import me.wyne.wutils.i18n.language.replacement.Placeholder;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.nevermined.worldevents.api.core.WorldEventApi;
import org.nevermined.worldevents.api.core.WorldEventManagerApi;
import org.nevermined.worldevents.api.core.WorldEventQueueApi;
import org.nevermined.worldevents.api.core.exception.AlreadyActiveException;
import org.nevermined.worldevents.api.core.exception.AlreadyInactiveException;
import org.nevermined.worldevents.config.GlobalConfig;
import org.nevermined.worldevents.config.QueueGuiConfig;

import java.util.ArrayList;
import java.util.List;

public class QueueGui extends PaginatedGui {

    public QueueGui(QueuesGui queuesGui, String queueKey, GlobalConfig config, WorldEventManagerApi eventManager, Player player)
    {
        gui = Gui.paginated()
                .title(I18n.global.accessor(player.locale(), "queue-gui-header").getPlaceholderComponent(player, Placeholder.replace("queue-key", queueKey)).get())
                .rows(6)
                .disableAllInteractions()
                .create();
        gui.getFiller().fillBorder(ItemBuilder.from(config.commonGuiConfig().getBorder()).asGuiItem());
        buildNavigation(queuesGui, config.commonGuiConfig(), player);
        updatePage(queueKey, config.queueGuiConfig(), eventManager, player);
    }

    private void updatePage(String queueKey, QueueGuiConfig config, WorldEventManagerApi eventManager, Player player)
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
        lore.add(I18n.global.accessor(player.locale(), "queue-capacity-format").getPlaceholderComponent(player, Placeholder.replace("queue-key", queueKey)).get());
        lore.add(I18n.global.accessor(player.locale(), "queue-active-format").getPlaceholderComponent(player, Placeholder.replace("queue-key", queueKey)).get());

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
            lore.addAll(getEventLore(queueKey, String.valueOf(i), player, I18n.global.accessor(player.locale(), "event-lore-order").getStringList().stream().map(LocalizedString::get).toArray(String[]::new)));

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
            lore.add(I18n.global.accessor(player.locale(), line).getPlaceholderComponent(player, Placeholder.replace("queue-key", queueKey), Placeholder.replace("event-index", eventIndex)).get());
        }

        return lore;
    }

    private void buildActivator(QueueGuiConfig config, String queueKey, WorldEventManagerApi eventManager, Player player)
    {
        if (config.getQueueActivateImpl().getSlot() == config.getQueueDeactivateImpl().getSlot())
            buildSingleActivator(config, queueKey, eventManager, player);
        else
            buildDoubleActivator(config, queueKey, eventManager, player);
    }

    private void buildDoubleActivator(QueueGuiConfig config, String queueKey, WorldEventManagerApi eventManager, Player player)
    {
        buildQueueActivator(config, queueKey, eventManager, player);
        buildQueueDeactivator(config, queueKey, eventManager, player);
    }

    private void buildSingleActivator(QueueGuiConfig config, String queueKey, WorldEventManagerApi eventManager, Player player)
    {
        if (eventManager.getEventQueueMap().get(queueKey).isActive())
            buildQueueDeactivator(config, queueKey, eventManager, player);
        else
            buildQueueActivator(config, queueKey, eventManager, player);
    }

    private void buildQueueActivator(QueueGuiConfig config, String queueKey, WorldEventManagerApi eventManager, Player player)
    {
        gui.updateItem(config.getQueueActivateImpl().getSlot(), config.getQueueActivateImpl()
                .<GuiConfigurable>getImmutableAccessor()
                .with(new GuiActionAttribute(event -> {
                    try {
                        eventManager.startEventQueue(queueKey);
                        updatePage(queueKey, config, eventManager, player);
                    } catch (AlreadyActiveException e)
                    {
                        I18n.global.accessor(player.locale(), "error-queue-already-active")
                                .getPlaceholderComponent(player, Placeholder.replace("queue-name", eventManager.getEventQueueMap().get(queueKey).getQueueData().name()))
                                .sendMessage(player);
                    }
                }))
                .buildGuiItem(player));
    }

    private void buildQueueDeactivator(QueueGuiConfig config, String queueKey, WorldEventManagerApi eventManager, Player player)
    {
        gui.updateItem(config.getQueueDeactivateImpl().getSlot(), config.getQueueDeactivateImpl()
                .<GuiConfigurable>getImmutableAccessor()
                .with(new GuiActionAttribute(event -> {
                    try {
                        eventManager.stopEventQueue(queueKey);
                        updatePage(queueKey, config, eventManager, player);
                    } catch (AlreadyInactiveException e)
                    {
                        I18n.global.accessor(player.locale(), "error-queue-already-inactive")
                                .getPlaceholderComponent(player, Placeholder.replace("queue-name", eventManager.getEventQueueMap().get(queueKey).getQueueData().name()))
                                .sendMessage(player);
                    }
                }))
                .buildGuiItem(player));
    }
}
