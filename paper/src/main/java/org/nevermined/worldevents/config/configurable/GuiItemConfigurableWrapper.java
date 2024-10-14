package org.nevermined.worldevents.config.configurable;

import me.wyne.wutils.config.configurables.GuiItemConfigurable;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.nevermined.worldevents.api.config.configurable.GuiItemConfigurableApi;
import org.nevermined.worldevents.api.wrapper.TextReplacementWrapper;
import org.nevermined.worldevents.wrapper.WrapperAdapter;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class GuiItemConfigurableWrapper implements GuiItemConfigurableApi {

    private final GuiItemConfigurable guiItemConfigurable;

    public GuiItemConfigurableWrapper(GuiItemConfigurable guiItemConfigurable) {
        this.guiItemConfigurable = guiItemConfigurable;
    }

    @Override
    public ItemStack build(TextReplacementWrapper... textReplacements) {
        return guiItemConfigurable.buildLegacy(WrapperAdapter.adaptTextReplacement(textReplacements));
    }

    @Override
    public ItemStack build(@Nullable Player player, TextReplacementWrapper... textReplacements) {
        return guiItemConfigurable.buildLegacy(player, WrapperAdapter.adaptTextReplacement(textReplacements));
    }

    @Override
    public String getName() {
        return guiItemConfigurable.getName();
    }

    @Override
    public Component getName(@Nullable Player player, TextReplacementWrapper... textReplacements) {
        return guiItemConfigurable.getLegacyName(player, WrapperAdapter.adaptTextReplacement(textReplacements));
    }

    @Override
    public Component getNameWithLore(@Nullable Player player, TextReplacementWrapper... textReplacements) {
        return guiItemConfigurable.getLegacyNameWithLore(player, WrapperAdapter.adaptTextReplacement(textReplacements));
    }

    @Override
    public Material getMaterial() {
        return guiItemConfigurable.getMaterial();
    }

    @Override
    public int getSlot() {
        return guiItemConfigurable.getSlot();
    }

    @Override
    public int getModel() {
        return guiItemConfigurable.getModel();
    }

    @Override
    public Collection<String> getLore() {
        return guiItemConfigurable.getLore();
    }

    @Override
    public List<Component> getLore(@Nullable Player player, TextReplacementWrapper... textReplacements) {
        return guiItemConfigurable.getLegacyLore(player, WrapperAdapter.adaptTextReplacement(textReplacements));
    }

    @Override
    public Optional<String> getPrint() {
        return guiItemConfigurable.getPrint();
    }

    @Override
    public Optional<Component> getPrint(@Nullable Player player, TextReplacementWrapper... textReplacements) {
        return guiItemConfigurable.getLegacyPrint(player, WrapperAdapter.adaptTextReplacement(textReplacements));
    }

    @Override
    public Optional<Sound> getSound() {
        return guiItemConfigurable.getSound();
    }

    @Override
    public Object getGuiItemConfigurable() {
        return guiItemConfigurable;
    }
}
