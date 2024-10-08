package org.nevermined.worldevents.api.config.configurable;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.nevermined.worldevents.api.wrapper.TextReplacementWrapper;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GuiItemConfigurableApi {

    ItemStack build(TextReplacementWrapper... textReplacements);
    ItemStack build(@Nullable Player player, TextReplacementWrapper... textReplacements);

    String getName();
    Component getName(@Nullable Player player, TextReplacementWrapper... textReplacements);
    Component getNameWithLore(@Nullable Player player, TextReplacementWrapper... textReplacements);
    Material getMaterial();
    int getSlot();
    int getModel();
    Collection<String> getLore();
    List<Component> getLore(@Nullable Player player, TextReplacementWrapper... textReplacements);
    Optional<String> getPrint();
    Optional<Component> getPrint(@Nullable Player player, TextReplacementWrapper... textReplacements);
    Optional<Sound> getSound();

}
