package org.nevermined.worldevents.api.expansion;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nevermined.worldevents.api.WEApi;
import org.nevermined.worldevents.api.core.WorldEventAction;

import java.io.File;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Logger;

public abstract class WorldEventExpansion implements Plugin {

    private ExpansionData expansionData;
    private DataProvider dataProvider;

    public final void init(DataProvider dataProvider) {
        this.expansionData = getExpansionData();
        this.dataProvider = dataProvider;
        onModuleLoad();
    }

    @NotNull
    public abstract String getKey();

    @NotNull
    public abstract Supplier<WorldEventAction> getAction();

    @NotNull
    public ExpansionData getExpansionData() {
        return expansionData == null ? new ExpansionData(getKey(), getAction(),
                new File(getClass().getProtectionDomain().getCodeSource().getLocation().getFile()).getName(),
                getClass().getName(),
                Instant.now()) : expansionData;
    }

    public void onModuleLoad() {}
    public void reload() {}
    @Override
    public void onDisable() {}

    public DataProvider getDataProvider() {
        return dataProvider;
    }

    @Override
    public @NotNull File getDataFolder() {
        return dataProvider.getDataFolder();
    }

    @Override
    public @NotNull PluginDescriptionFile getDescription() {
        return WEApi.getInstance().getPlugin().getDescription();
    }

    @Override
    public @NotNull FileConfiguration getConfig() {
        return dataProvider.getConfig();
    }

    @Override
    public @Nullable InputStream getResource(@NotNull String filename) {
        return dataProvider.getResource(filename);
    }

    @Override
    public void saveConfig() {
        dataProvider.saveDefaultConfig();
    }

    @Override
    public void saveDefaultConfig() {
        dataProvider.saveDefaultConfig();
    }

    @Override
    public void saveResource(@NotNull String resourcePath, boolean replace) {
        dataProvider.saveResource(resourcePath, replace);
    }

    @Override
    public void reloadConfig() {
        dataProvider.reloadConfig();
    }

    @Override
    public @NotNull PluginLoader getPluginLoader() {
        return WEApi.getInstance().getPlugin().getPluginLoader();
    }

    @Override
    public @NotNull Server getServer() {
        return WEApi.getInstance().getPlugin().getServer();
    }

    @Override
    public boolean isEnabled() {
        return WEApi.getInstance().getPlugin().isEnabled();
    }



    @Override
    public final void onLoad() {

    }

    @Override
    public final void onEnable() {

    }

    @Override
    public boolean isNaggable() {
        return WEApi.getInstance().getPlugin().isNaggable();
    }

    @Override
    public void setNaggable(boolean canNag) {
        WEApi.getInstance().getPlugin().setNaggable(canNag);
    }

    @Override
    public @Nullable ChunkGenerator getDefaultWorldGenerator(@NotNull String worldName, @Nullable String id) {
        return WEApi.getInstance().getPlugin().getDefaultWorldGenerator(worldName, id);
    }

    @Override
    public @NotNull Logger getLogger() {
        return WEApi.getInstance().getPlugin().getLogger();
    }

    @Override
    public @NotNull String getName() {
        return WEApi.getInstance().getPlugin().getName();
    }

    @Override
    public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return false;
    }

    @Override
    public final @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }
}
