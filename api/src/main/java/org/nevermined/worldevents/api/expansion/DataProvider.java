package org.nevermined.worldevents.api.expansion;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.InputStream;

public interface DataProvider {
    File getDataFolder();
    File getConfigFile();
    void reloadConfig();
    FileConfiguration getConfig();
    InputStream getResource(String path);
    void saveResource(String path, boolean replace);
    void saveResource(String source, String destination, boolean replace);
    void saveDefaultConfig();
}
