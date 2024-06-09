package org.nevermined.worldevents;

import com.google.inject.*;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.wyne.wutils.config.Config;
import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.log.BasicLogConfig;
import me.wyne.wutils.log.ConfigurableLogConfig;
import me.wyne.wutils.log.Log;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.nevermined.worldevents.config.GlobalConfig;
import org.nevermined.worldevents.config.modules.ConfigModule;
import org.nevermined.worldevents.expansions.modules.ExpansionModule;
import org.nevermined.worldevents.hooks.Placeholders;
import org.nevermined.worldevents.modules.PluginModule;

import java.io.File;
import java.util.concurrent.Executors;

@Singleton
public final class WorldEvents extends ExtendedJavaPlugin {

    private Injector injector;

    private GlobalConfig globalConfig;

    @Override
    public void enable() {
        saveDefaultConfig();
        initializeLogger();
        initializeI18n();

        try {
            injector = Guice.createInjector(
                    new PluginModule(this),
                    new ConfigModule(),
                    new ExpansionModule()
            );
        } catch (CreationException | ConfigurationException | ProvisionException e)
        {
            Log.global.exception("Guice exception", e);
        }

        initializeConfig();
        injector.getInstance(Placeholders.class).register();
    }

    private void initializeLogger()
    {
        Log.global = Log.builder()
                .setLogger(getLogger())
                .setConfig(new ConfigurableLogConfig("global", Config.global, new BasicLogConfig(true, true, true, true)))
                .setLogDirectory(new File(getDataFolder(), "log"))
                .setFileWriteExecutor(Executors.newSingleThreadExecutor())
                .build();
    }

    private void initializeConfig()
    {
        globalConfig = injector.getInstance(GlobalConfig.class);
        Config.global.setConfigGenerator(this, "config.yml");
        Config.global.generateConfig(getDescription().getVersion());
        reloadConfig();
        Config.global.reloadConfig(getConfig());
    }

    private void initializeI18n()
    {
        I18n.global.loadLanguage("lang/ru.yml", this);
        I18n.global.loadDefaultPluginLanguage(this);
        I18n.global.setDefaultLanguage(I18n.getDefaultLanguageFile(this));
    }

    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }
}
