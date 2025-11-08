package org.nevermined.worldevents.expansion;

import me.wyne.wutils.log.Log;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;
import org.nevermined.worldevents.api.expansion.DataProvider;
import org.nevermined.worldevents.api.expansion.WorldEventExpansion;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ExpansionDataProvider implements DataProvider {

    public final static String CONFIG_PATH = "config.yml";

    private final WorldEventExpansion expansion;

    private final File expansionDirectory;
    private final File dataFolder;
    private final File configFile;
    private FileConfiguration config;

    private JarFile expansionJar;

    public ExpansionDataProvider(WorldEventExpansion expansion, File expansionDirectory) {
        this.expansion = expansion;
        this.expansionDirectory = expansionDirectory;
        this.dataFolder = getDataFolder();
        this.configFile = getConfigFile();
        this.config = getConfig();
        try {
            this.expansionJar = new JarFile(new File(expansionDirectory, expansion.getExpansionData().jarName()));
        } catch (IOException e) {
            Log.global.exception("An exception occurred trying to get expansion jar file", e);
        }
    }

    @Override
    public File getDataFolder() {
        if (dataFolder != null)
            return dataFolder;
        File newDataFolder = new File(expansionDirectory, expansion.getKey());
        if (!newDataFolder.exists())
            newDataFolder.mkdir();
        return newDataFolder;
    }

    @Override
    public File getConfigFile() {
        return configFile == null ? new File(getDataFolder(), CONFIG_PATH) : configFile;
    }

    @Override
    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(getConfigFile());
    }

    @Override
    public FileConfiguration getConfig() {
        return config == null ? YamlConfiguration.loadConfiguration(getConfigFile()) : config;
    }

    @Override
    @Nullable
    public InputStream getResource(String path) {
        try {
            JarEntry jarEntry = expansionJar.getJarEntry(path);
            return expansionJar.getInputStream(jarEntry);
        } catch (IOException e) {
            Log.global.exception("An exception occurred trying to get resource '" + path + "'", e);
        }
        return null;
    }

    @Override
    public void saveResource(String path, boolean replace) {
        try {
            JarEntry jarEntry = expansionJar.getJarEntry(path);
            File resource = new File(getDataFolder(), path);
            if (resource.exists() && !replace)
                return;
            FileUtils.copyInputStreamToFile(expansionJar.getInputStream(jarEntry), resource);
        } catch (IOException e) {
            Log.global.exception("An exception occurred trying to save resource '" + path + "'", e);
        }
    }

    @Override
    public void saveResource(String source, String destination, boolean replace) {
        try {
            JarEntry jarEntry = expansionJar.getJarEntry(source);
            File resource = new File(getDataFolder(), destination);
            if (resource.exists() && !replace)
                return;
            FileUtils.copyInputStreamToFile(expansionJar.getInputStream(jarEntry), resource);
        } catch (IOException e) {
            Log.global.exception("An exception occurred trying to save resource '" + source + "'", e);
        }
    }

    @Override
    public void saveDefaultConfig() {
        saveResource(CONFIG_PATH, false);
        config = YamlConfiguration.loadConfiguration(getConfigFile());
    }

}
