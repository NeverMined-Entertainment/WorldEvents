package org.nevermined.worldevents.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.wyne.wutils.log.Log;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.nevermined.worldevents.api.config.ConfigProviderApi;
import org.nevermined.worldevents.expansion.ExpansionLoader;

import java.io.File;
import java.io.IOException;

@Singleton
public class ConfigProvider implements ConfigProviderApi {

    private final File configDirectory;

    @Inject
    public ConfigProvider(@ExpansionLoader.ExpansionDirectory File expansionDirectory) {
        this.configDirectory = new File(expansionDirectory, "config");
        if (!configDirectory.exists())
            configDirectory.mkdir();
    }

    @Override
    public FileConfiguration loadConfiguration(String filePath) {
        return YamlConfiguration.loadConfiguration(getConfigFile(filePath));
    }

    @Override
    public void saveConfiguration(FileConfiguration configuration, String filePath) {
        try {
            configuration.save(getConfigFile(filePath));
        } catch (IOException e) {
            Log.global.exception("An exception occurred trying to save configuration file '" + filePath + "'", e);
        }
    }

    private File getConfigFile(String filePath)
    {
        File configFile = new File(configDirectory, filePath);
        if (!configFile.exists())
        {
            configFile.mkdirs();
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                Log.global.exception("An exception occurred trying to create new configuration file '" + filePath + "'", e);
            }
        }
        return configFile;
    }
}
