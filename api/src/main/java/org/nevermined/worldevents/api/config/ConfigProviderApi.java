package org.nevermined.worldevents.api.config;

import org.bukkit.configuration.file.FileConfiguration;

public interface ConfigProviderApi {

    FileConfiguration loadConfiguration(String filePath);
    void saveConfiguration(FileConfiguration configuration, String filePath);

}
