package org.nevermined.worldevents.config.modules;

import com.google.inject.AbstractModule;
import org.nevermined.worldevents.api.config.GlobalConfigApi;
import org.nevermined.worldevents.api.config.MainGuiConfigApi;
import org.nevermined.worldevents.api.config.QueuesGuiConfigApi;
import org.nevermined.worldevents.config.GlobalConfig;
import org.nevermined.worldevents.config.MainGuiConfig;
import org.nevermined.worldevents.config.QueuesGuiConfig;

public class ConfigModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MainGuiConfigApi.class).to(MainGuiConfig.class);
        bind(QueuesGuiConfigApi.class).to(QueuesGuiConfig.class);
        bind(GlobalConfigApi.class).to(GlobalConfig.class);
    }
}
