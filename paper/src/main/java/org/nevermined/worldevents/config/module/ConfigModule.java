package org.nevermined.worldevents.config.module;

import com.google.inject.AbstractModule;
import org.nevermined.worldevents.api.config.*;
import org.nevermined.worldevents.config.*;

public class ConfigModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MainGuiConfigApi.class).to(MainGuiConfig.class);
        bind(CommonGuiConfigApi.class).to(CommonGuiConfig.class);
        bind(QueueGuiConfigApi.class).to(QueueGuiConfig.class);
        bind(GlobalConfigApi.class).to(GlobalConfig.class);
        bind(ConfigProviderApi.class).to(ConfigProvider.class);
    }
}
