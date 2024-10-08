package org.nevermined.worldevents.config.module;

import com.google.inject.AbstractModule;
import org.nevermined.worldevents.api.config.CommonGuiConfigApi;
import org.nevermined.worldevents.api.config.GlobalConfigApi;
import org.nevermined.worldevents.api.config.MainGuiConfigApi;
import org.nevermined.worldevents.api.config.QueueGuiConfigApi;
import org.nevermined.worldevents.config.CommonGuiConfig;
import org.nevermined.worldevents.config.GlobalConfig;
import org.nevermined.worldevents.config.MainGuiConfig;
import org.nevermined.worldevents.config.QueueGuiConfig;

public class ConfigModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MainGuiConfigApi.class).to(MainGuiConfig.class);
        bind(CommonGuiConfigApi.class).to(CommonGuiConfig.class);
        bind(QueueGuiConfigApi.class).to(QueueGuiConfig.class);
        bind(GlobalConfigApi.class).to(GlobalConfig.class);
    }
}
