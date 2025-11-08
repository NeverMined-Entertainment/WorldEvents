package org.nevermined.worldevents.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import me.wyne.wutils.config.Config;

@Singleton
public record GlobalConfig(MainGuiConfig mainGuiConfig, CommonGuiConfig commonGuiConfig, QueueGuiConfig queueGuiConfig) {

    @Inject
    public GlobalConfig {
        Config.global.registerConfigObject(mainGuiConfig);
        Config.global.registerConfigObject(commonGuiConfig);
        Config.global.registerConfigObject(queueGuiConfig);
    }

}
