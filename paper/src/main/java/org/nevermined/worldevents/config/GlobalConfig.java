package org.nevermined.worldevents.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public record GlobalConfig(StringConfig stringConfig) {
    @Inject
    public GlobalConfig {
    }
}
