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
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.ServicePriority;
import org.nevermined.worldevents.api.WorldEventsApi;
import org.nevermined.worldevents.api.config.CommonGuiConfigApi;
import org.nevermined.worldevents.api.config.GlobalConfigApi;
import org.nevermined.worldevents.api.config.MainGuiConfigApi;
import org.nevermined.worldevents.api.config.QueueGuiConfigApi;
import org.nevermined.worldevents.api.core.WorldEventAction;
import org.nevermined.worldevents.api.core.WorldEventManagerApi;
import org.nevermined.worldevents.commands.WorldEventsCommand;
import org.nevermined.worldevents.commands.modules.CommandModule;
import org.nevermined.worldevents.config.modules.ConfigModule;
import org.nevermined.worldevents.core.modules.WorldEventManagerModule;
import org.nevermined.worldevents.expansions.DemoExpansion;
import org.nevermined.worldevents.expansions.modules.ExpansionModule;
import org.nevermined.worldevents.hooks.Placeholders;
import org.nevermined.worldevents.hooks.modules.HooksModule;
import org.nevermined.worldevents.modules.PluginModule;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

@Singleton
public final class WorldEvents extends ExtendedJavaPlugin {

    private Injector injector;
    private final Map<String, WorldEventAction> registeredExpansions = new HashMap<>()
    {
        { put("Demo", new DemoExpansion()); }
    };

    private GlobalConfigApi globalConfig;

    private WorldEventManagerApi worldEventManager;

    private static BukkitAudiences adventure;

    @Override
    protected void load() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this));
    }

    @Override
    public void enable() {
        CommandAPI.onEnable();
        adventure = BukkitAudiences.create(this);

        saveDefaultConfig();
        initializeLogger();
        initializeI18n();

        try {
            injector = Guice.createInjector(
                    new PluginModule(this),
                    new ConfigModule(),
                    new HooksModule(),
                    new ExpansionModule(),
                    new WorldEventManagerModule(),
                    new CommandModule()
            );
        } catch (CreationException e)
        {
            Log.global.exception("Guice injector creation exception", e);
        }

        initializeConfig();

        try {
            injector.getInstance(Placeholders.class).register();
            getServer().getServicesManager().register(WorldEventsApi.class, injector.getInstance(WorldEventsApi.class), this, ServicePriority.Normal);
            worldEventManager = injector.getInstance(WorldEventManagerApi.class);
            injector.getInstance(WorldEventsCommand.class);
        } catch (ConfigurationException | ProvisionException e)
        {
            Log.global.exception("Guice configuration/provision exception", e);
        }
    }

    @Override
    protected void disable() {
        CommandAPI.onDisable();
        if(adventure != null) {
            adventure.close();
            adventure = null;
        }
    }

    public void registerEventTypeExpansion(String key, WorldEventAction action)
    {
        registeredExpansions.put(key, action);
        reloadEventQueues();
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
            MainGuiConfigApi mainGuiConfig = injector.getInstance(MainGuiConfigApi.class);
            CommonGuiConfigApi commonGuiConfig = injector.getInstance(CommonGuiConfigApi.class);
            QueueGuiConfigApi queuesGuiConfig = injector.getInstance(QueueGuiConfigApi.class);
            globalConfig = injector.getInstance(GlobalConfigApi.class);
            Config.global.registerConfigObject(mainGuiConfig);
            Config.global.registerConfigObject(commonGuiConfig);
            Config.global.registerConfigObject(queuesGuiConfig);
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
        I18n.global.setStringValidator(new EmptyValidator());
    }

    public void reload()
    {
        reloadConfig();
        Config.global.reloadConfig(getConfig());
        initializeI18n();
    }

    public void reloadEventQueues()
    {
        injector.injectMembers(worldEventManager);
    }

    public static BukkitAudiences adventure() {
        if(adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return adventure;
    }

    public GlobalConfigApi getGlobalConfig() {
        return globalConfig;
    }

    public WorldEventManagerApi getWorldEventManager() {
        return worldEventManager;
    }

    public Map<String, WorldEventAction> getRegisteredExpansions() {
        return registeredExpansions;
    }
}
