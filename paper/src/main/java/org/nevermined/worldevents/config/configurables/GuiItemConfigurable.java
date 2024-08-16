package org.nevermined.worldevents.config.configurables;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import me.clip.placeholderapi.PlaceholderAPI;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.Configurable;
import me.wyne.wutils.i18n.I18n;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Nullable;
import org.nevermined.worldevents.api.config.configurables.GuiItemConfigurableApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GuiItemConfigurable implements Configurable, GuiItemConfigurableApi {

    private int slot;
    private Material material;
    private String name;
    private List<String> lore;
    private String print;
    private Sound sound;

    private List<TagResolver> tagResolvers = new ArrayList<>();

    public GuiItemConfigurable(int slot, Material material, String name, List<String> lore, @Nullable String print, @Nullable Sound sound) {
        this.slot = slot;
        this.material = material;
        this.name = name;
        this.lore = lore;
        this.print = print;
        this.sound = sound;
    }

    public GuiItemConfigurable(int slot, Material material, String name, List<String> lore, @Nullable String print, @Nullable Sound sound, List<TagResolver> tagResolvers) {
        this.slot = slot;
        this.material = material;
        this.name = name;
        this.lore = lore;
        this.print = print;
        this.sound = sound;
        this.tagResolvers.addAll(tagResolvers);
    }

    @Override
    public String toConfig(ConfigEntry configEntry) {
        StringBuilder stringBuilder = new StringBuilder();
        if (slot != -1) stringBuilder.append("\n  slot: " + slot);
        stringBuilder.append("\n  material: " + material.toString() + "\n");
        stringBuilder.append("  name: '" + name + "'\n");
        if (!lore.isEmpty())
        {
            stringBuilder.append("  lore:\n");
            for (String loreString : lore)
            {
                stringBuilder.append("    - '" + loreString + "'\n");
            }
        }
        if (print != null) stringBuilder.append("  print: '" + print.replace("'", "\"") + "'\n");
        if (sound != null) stringBuilder.append("  sound: '" + sound.name().asString() + "'\n");
        return stringBuilder.toString();
    }

    @Override
    public void fromConfig(Object configObject) {
        ConfigurationSection configurationSection = (ConfigurationSection) configObject;
        if (configurationSection.contains("slot")) slot = configurationSection.getInt("slot");
        material = Material.getMaterial(configurationSection.getString("material"));
        name = configurationSection.getString("name");
        if (configurationSection.contains("lore"))
        {
            lore.clear();
            lore.addAll(configurationSection.getStringList("lore"));
        }
        if (configurationSection.contains("print")) print = configurationSection.getString("print");
        if (configurationSection.contains("sound")) sound = Sound.sound(Key.key(configurationSection.getString("sound")), Sound.Source.MASTER, 1f, 1f);
    }

    @Override
    public GuiItemConfigurable applyPlaceholders(Player player)
    {
        String newName = I18n.global.getPlaceholderString(player.locale(), player, name);
        List<String> newLore =
                new ArrayList<>(lore.stream().map(s -> I18n.global.getPlaceholderString(player.locale(), player, s)).toList());
        String newPrint = print;
        if (print != null) newPrint = I18n.global.getPlaceholderString(player.locale(), player, print);
        return new GuiItemConfigurable(slot, material, newName, newLore, newPrint, sound);
    }

    @Override
    public GuiItemConfigurableApi applyReplacements(Map<String, Component> replacements) {
        return new GuiItemConfigurable(slot, material, name, lore, print, sound, replacements.keySet().stream().map(key -> (TagResolver)Placeholder.component(key, replacements.get(key))).toList());
    }

    @Override
    public GuiItem build()
    {
        return ItemBuilder.from(material).name(getName()).lore(getLore())
                .asGuiItem(event -> {
                    if (getPrint() != null)
                        event.getWhoClicked().sendMessage(getPrint());
                    if (getSound() != null)
                        event.getWhoClicked().playSound(getSound());
                });
    }

    @Override
    public GuiItem build(GuiAction<InventoryClickEvent> action)
    {
        return ItemBuilder.from(material).name(getName()).lore(getLore())
                .asGuiItem(event -> {
                    if (getPrint() != null)
                        event.getWhoClicked().sendMessage(getPrint());
                    if (getSound() != null)
                        event.getWhoClicked().playSound(getSound());
                    action.execute(event);
                });
    }

    @Override
    public int getSlot() {
        return slot;
    }

    public Material getMaterial() {
        return material;
    }

    @Override
    public Component getName() {
        return MiniMessage.miniMessage().deserialize(name, tagResolvers.toArray(new TagResolver[0]));
    }

    @Override
    public List<Component> getLore() {
        return new ArrayList<>(lore.stream()
                .map(component -> MiniMessage.miniMessage().deserialize(component, tagResolvers.toArray(TagResolver[]::new)))
                .toList());
    }

    @Override
    @Nullable
    public Component getPrint() {
        if (print == null)
            return null;
        return MiniMessage.miniMessage().deserialize(print, tagResolvers.toArray(TagResolver[]::new));
    }

    @Override
    @Nullable
    public Sound getSound() {
        return sound;
    }

}
