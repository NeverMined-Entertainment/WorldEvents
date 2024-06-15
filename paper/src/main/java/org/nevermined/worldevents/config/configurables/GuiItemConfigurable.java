package org.nevermined.worldevents.config.configurables;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import me.clip.placeholderapi.PlaceholderAPI;
import me.wyne.wutils.config.ConfigEntry;
import me.wyne.wutils.config.Configurable;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GuiItemConfigurable implements Configurable {

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

    public GuiItemConfigurable applyPlaceholders(Player player)
    {
        String newName = PlaceholderAPI.setPlaceholders(player, name);
        List<String> newLore =
                new ArrayList<>(lore.stream().map(s -> PlaceholderAPI.setPlaceholders(player, s)).toList());
        String newPrint = print;
        if (print != null) newPrint = PlaceholderAPI.setPlaceholders(player, print);
        return new GuiItemConfigurable(slot, material, newName, newLore, newPrint, sound);
    }

    public GuiItemConfigurable applyTagResolvers(TagResolver... tagResolvers)
    {
        return new GuiItemConfigurable(slot, material, name, lore, print, sound, Arrays.stream(tagResolvers).toList());
    }

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

    public int getSlot() {
        return slot;
    }

    public Material getMaterial() {
        return material;
    }

    public Component getName() {
        return MiniMessage.miniMessage().deserialize(name, tagResolvers.toArray(new TagResolver[0]));
    }

    public List<Component> getLore() {
        return new ArrayList<>(lore.stream()
                .map(component -> MiniMessage.miniMessage().deserialize(component, tagResolvers.toArray(new TagResolver[0])))
                .toList());
    }

    public Component getPrint() {
        if (print == null)
            return null;
        return MiniMessage.miniMessage().deserialize(print, tagResolvers.toArray(new TagResolver[0]));
    }

    public Sound getSound() {
        if (sound == null)
            return null;
        return sound;
    }

}
