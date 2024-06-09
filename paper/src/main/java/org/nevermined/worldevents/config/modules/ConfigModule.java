package org.nevermined.worldevents.config.modules;

import com.google.inject.AbstractModule;
import org.nevermined.worldevents.config.GlobalConfig;
import org.nevermined.worldevents.config.StringConfig;

public class ConfigModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(StringConfig.class);
        bind(GlobalConfig.class);
    }
}
