package org.nevermined.worldevents;

import com.google.inject.*;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.wyne.wutils.config.Config;
import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.language.validation.EmptyValidator;
import me.wyne.wutils.log.BasicLogConfig;
import me.wyne.wutils.log.ConfigurableLogConfig;
import me.wyne.wutils.log.Log;
import org.nevermined.worldevents.api.core.WorldEventManagerApi;
import org.nevermined.worldevents.command.module.CommandModule;
import org.nevermined.worldevents.config.module.ConfigModule;
import org.nevermined.worldevents.core.module.WorldEventManagerModule;
import org.nevermined.worldevents.expansion.ExpansionLoader;
import org.nevermined.worldevents.expansion.module.ExpansionModule;
import org.nevermined.worldevents.hook.module.HooksModule;
import org.nevermined.worldevents.module.PluginModule;

import java.io.File;
import java.util.concurrent.Executors;

@Singleton
public final class WorldEvents extends ExtendedJavaPlugin {

    private Injector injector;

    @Override
    protected void load() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this));
    }

    @Override
    public void enable() {
        CommandAPI.onEnable();

        saveDefaultConfig();
        File expansionsFolder = new File(getDataFolder(), "expansions");
        if (!expansionsFolder.exists())
            expansionsFolder.mkdir();

        initializeLogger();
        initializeI18n();

        try {
            injector = Guice.createInjector(
                    Stage.PRODUCTION,
                    new PluginModule(this),
                    new ConfigModule(),
                    new HooksModule(),
                    new ExpansionModule(expansionsFolder),
                    new WorldEventManagerModule(),
                    new CommandModule()
            );
        } catch (CreationException e)
        {
            Log.global.exception("Guice injector creation exception", e);
        }

        initializeConfig();

        try {
            WorldEventManagerApi worldEventManager = injector.getInstance(WorldEventManagerApi.class);
            injector.getInstance(ExpansionLoader.class).reloadExpansions();
            worldEventManager.reloadEventQueues();
        } catch (ConfigurationException | ProvisionException e)
        {
            Log.global.exception("Guice configuration/provision exception", e);
        }
    }

    @Override
    protected void disable() {
        CommandAPI.onDisable();
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
        Config.global.setConfigGenerator(this, "config.yml");
        Config.global.generateConfig(getDescription().getVersion());
        reloadConfig();
        Config.global.reloadConfig(getConfig());
    }

    private void initializeI18n()
    {
        I18n.global.loadLanguage("lang/ru.yml", this);
        I18n.global.loadLanguage("lang/en.yml", this);
        I18n.global.loadDefaultPluginLanguage(this);
        I18n.global.setDefaultLanguage(I18n.getDefaultLanguageFile(this));
        I18n.global.loadLanguages(this);
        I18n.global.setStringValidator(new EmptyValidator());
    }

    public void reload()
    {
        reloadConfig();
        Config.global.reloadConfig(getConfig());
        initializeI18n();
    }
}
