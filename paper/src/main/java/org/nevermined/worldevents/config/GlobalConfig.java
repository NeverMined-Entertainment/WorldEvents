package org.nevermined.worldevents.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.nevermined.worldevents.api.config.GlobalConfigApi;
import org.nevermined.worldevents.api.config.MainGuiConfigApi;
import org.nevermined.worldevents.api.config.QueuesGuiConfigApi;

@Singleton
public record GlobalConfig(MainGuiConfigApi mainGuiConfig, QueuesGuiConfigApi queuesGuiConfig) implements GlobalConfigApi {

    @Inject
    public GlobalConfig {
    }

}
