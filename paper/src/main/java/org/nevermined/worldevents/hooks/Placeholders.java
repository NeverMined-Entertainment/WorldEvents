package org.nevermined.worldevents.hooks;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nevermined.worldevents.WorldEvents;
import org.nevermined.worldevents.api.core.WorldEventManagerApi;
import org.nevermined.worldevents.core.WorldEventManager;

@Singleton
public class Placeholders extends PlaceholderExpansion {

    private final WorldEvents plugin;
    private final WorldEventManagerApi worldEventManager;

    @Inject
    public Placeholders(WorldEvents plugin, WorldEventManagerApi worldEventManager)
    {
        this.plugin = plugin;
        this.worldEventManager = worldEventManager;
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
        return plugin.getDescription().getVersion();
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
