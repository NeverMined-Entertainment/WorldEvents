package org.nevermined.worldevents;

import com.google.inject.*;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.wyne.wutils.config.Config;
import me.wyne.wutils.i18n.I18n;
import me.wyne.wutils.i18n.PluginI18nBuilder;
import me.wyne.wutils.i18n.language.interpretation.ComponentInterpreters;
import me.wyne.wutils.i18n.language.validation.EmptyValidator;
import me.wyne.wutils.log.JulLevel;
import me.wyne.wutils.log.Level;
import me.wyne.wutils.log.Log;
import me.wyne.wutils.log.Log4jFactory;
import org.bukkit.configuration.MemoryConfiguration;
import org.nevermined.worldevents.api.core.WorldEventManagerApi;
import org.nevermined.worldevents.command.module.CommandModule;
import org.nevermined.worldevents.config.module.ConfigModule;
import org.nevermined.worldevents.core.module.WorldEventManagerModule;
import org.nevermined.worldevents.expansion.ExpansionLoader;
import org.nevermined.worldevents.expansion.module.ExpansionModule;
import org.nevermined.worldevents.hook.module.HooksModule;
import org.nevermined.worldevents.module.PluginModule;
import org.slf4j.Logger;

import java.io.File;
import java.util.concurrent.Executors;

@Singleton
public final class WorldEvents extends ExtendedJavaPlugin {

    private static WorldEvents instance;
    private org.slf4j.Logger log;

    private Injector injector;

    @Override
    protected void load() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this));
    }

    @Override
    public void enable() {
        instance = this;
        CommandAPI.onEnable();

        saveDefaultConfig();
        getConfig().setDefaults(new MemoryConfiguration());
        File expansionDirectory = new File(getDataFolder(), "expansions");
        if (!expansionDirectory.exists())
            expansionDirectory.mkdir();

        initializeLogger();
        initializeI18n();

        try {
            injector = Guice.createInjector(
                    Stage.PRODUCTION,
                    new PluginModule(this),
                    new ConfigModule(),
                    new HooksModule(),
                    new ExpansionModule(expansionDirectory),
                    new WorldEventManagerModule(),
                    new CommandModule()
            );
        } catch (CreationException e)
        {
            log.error("Guice injector creation exception", e);
        }

        initializeConfig();

        try {
            WorldEventManagerApi worldEventManager = injector.getInstance(WorldEventManagerApi.class);
            injector.getInstance(ExpansionLoader.class).reloadExpansions();
            worldEventManager.reloadEventQueues();
        } catch (ConfigurationException | ProvisionException e)
        {
            log.error("Guice configuration/provision exception", e);
        }
    }

    public static WorldEvents getInstance() {
        return instance;
    }

    public Logger getLog() {
        return log;
    }

    @Override
    protected void disable() {
        CommandAPI.onDisable();
    }

    private void initializeLogger()
    {
        Log.global = Log.builder()
                .setLogger(getLogger())
                .setLevel(JulLevel.valueOf(getConfig().getString("logLevel", "INFO")).getLevel())
                .setLogDirectory(new File(getDataFolder(), "log"))
                .setFileWriteExecutor(Executors.newSingleThreadExecutor())
                .build();
        Log.global.deleteOlderLogs();

        log = Log4jFactory.createLogger(
                this,
                Log4jFactory.DEFAULT_FILE_MESSAGE_PATTERN,
                Level.valueOf(getConfig().getString("logLevel", "INFO")),
                new File(getDataFolder(), "log").getPath(),
                Log.global
        );
    }

    private void initializeConfig()
    {
        Config.global.log = log;
        Config.global.setConfigGenerator(this, "config.yml");
        Config.global.generateConfig();
        reloadConfig();
        getConfig().setDefaults(new MemoryConfiguration());
        Config.global.reloadConfig(getConfig());
    }

    private void initializeI18n()
    {
        I18n.global = new PluginI18nBuilder(this)
                .setComponentInterpreter(
                        ComponentInterpreters.valueOf(
                                getConfig().getString("serializer", "LEGACY")
                        ).get(new EmptyValidator())
                )
                .setUsePlayerLanguage(getConfig().getBoolean("userPlayerLanguage", true))
                .loadLanguage("lang/ru.yml")
                .loadLanguage("lang/en.yml")
                .build();
    }

    public void reload()
    {
        reloadConfig();
        Config.global.reloadConfig(getConfig());
        initializeI18n();
    }
}
