package org.nevermined.worldevents.hooks;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nevermined.worldevents.WorldEvents;

@Singleton
public class Placeholders extends PlaceholderExpansion {

    private final WorldEvents plugin;

    @Inject
    public Placeholders(WorldEvents plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "worldevents";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Wyne";
    }

    @Override
    public @NotNull String getVersion() {
        return null;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        return null;
    }
}
