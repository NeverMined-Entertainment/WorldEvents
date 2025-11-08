package org.nevermined.worldevents.config.module;

import com.google.inject.AbstractModule;
import org.nevermined.worldevents.config.*;

public class ConfigModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MainGuiConfig.class);
        bind(CommonGuiConfig.class);
        bind(QueueGuiConfig.class);
        bind(GlobalConfig.class);
    }
}
