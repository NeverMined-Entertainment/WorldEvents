package org.nevermined.worldevents.config.modules;

import com.google.inject.AbstractModule;
import org.nevermined.worldevents.api.config.GlobalConfigApi;
import org.nevermined.worldevents.api.config.StringConfigApi;
import org.nevermined.worldevents.config.GlobalConfig;
import org.nevermined.worldevents.config.StringConfig;

public class ConfigModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(StringConfigApi.class).to(StringConfig.class);
        bind(GlobalConfigApi.class).to(GlobalConfig.class);
    }
}
