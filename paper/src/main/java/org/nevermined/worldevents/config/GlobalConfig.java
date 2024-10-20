package org.nevermined.worldevents.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.wyne.wutils.config.Config;
import org.nevermined.worldevents.api.config.CommonGuiConfigApi;
import org.nevermined.worldevents.api.config.GlobalConfigApi;
import org.nevermined.worldevents.api.config.MainGuiConfigApi;
import org.nevermined.worldevents.api.config.QueueGuiConfigApi;

@Singleton
public record GlobalConfig(MainGuiConfigApi mainGuiConfig, CommonGuiConfigApi commonGuiConfig, QueueGuiConfigApi queueGuiConfig) implements GlobalConfigApi {

    @Inject
    public GlobalConfig {
        Config.global.registerConfigObject(mainGuiConfig);
        Config.global.registerConfigObject(commonGuiConfig);
        Config.global.registerConfigObject(queueGuiConfig);
    }

}
