package org.nevermined.worldevents.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.nevermined.worldevents.api.config.GlobalConfigApi;
import org.nevermined.worldevents.api.config.StringConfigApi;

@Singleton
public record GlobalConfig(StringConfigApi stringConfig) implements GlobalConfigApi {

    @Inject
    public GlobalConfig {
    }

}
