package org.nevermined.worldevents;

import com.google.inject.*;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.wyne.wutils.config.Config;
import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.log.BasicLogConfig;
import me.wyne.wutils.log.ConfigurableLogConfig;
import me.wyne.wutils.log.Log;
import org.nevermined.worldevents.api.config.GlobalConfigApi;
import org.nevermined.worldevents.api.config.StringConfigApi;
import org.nevermined.worldevents.api.core.WorldEventManagerApi;
import org.nevermined.worldevents.config.modules.ConfigModule;
import org.nevermined.worldevents.core.modules.WorldEventManagerModule;
import org.nevermined.worldevents.expansions.modules.ExpansionModule;
import org.nevermined.worldevents.hooks.Placeholders;
import org.nevermined.worldevents.hooks.modules.HooksModule;
import org.nevermined.worldevents.modules.PluginModule;

import java.io.File;
import java.util.concurrent.Executors;

@Singleton
public final class WorldEvents extends ExtendedJavaPlugin {

    private Injector injector;

    private GlobalConfigApi globalConfig;

    @Override
    public void enable() {
        saveDefaultConfig();
        initializeLogger();
        initializeI18n();

        try {
            injector = Guice.createInjector(
                    new PluginModule(this),
                    new ConfigModule(),
                    new HooksModule(),
                    new ExpansionModule(),
                    new WorldEventManagerModule()
            );
        } catch (CreationException e)
        {
            Log.global.exception("Guice injector creation exception", e);
        }

        initializeConfig();

        try {
            injector.getInstance(Placeholders.class).register();
            injector.getInstance(WorldEventManagerApi.class).startEventQueue("demo-queue-1");
        } catch (ConfigurationException | ProvisionException e)
        {
            Log.global.exception("Guice configuration/provision exception", e);
        }
    }

    private void initializeLogger()
    {
        Log.global = Log.builder()
                .setLogger(getLogger())
                .setConfig(new ConfigurableLogConfig("Global", Config.global, new BasicLogConfig(true, true, true, true)))
                .setLogDirectory(new File(getDataFolder(), "log"))
                .setFileWriteExecutor(Executors.newSingleThreadExecutor())
                .build();
    }

    private void initializeConfig()
    {
        try {
            StringConfigApi stringConfig = injector.getInstance(StringConfigApi.class);
            globalConfig = injector.getInstance(GlobalConfigApi.class);
            Config.global.registerConfigObject(stringConfig);
        } catch (ConfigurationException | ProvisionException e)
        {
            Log.global.exception("Guice exception while creating configuration", e);
        }

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

    public GlobalConfigApi getGlobalConfig() {
        return globalConfig;
    }
}
