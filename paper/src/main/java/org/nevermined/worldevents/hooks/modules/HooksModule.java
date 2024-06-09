package org.nevermined.worldevents.hooks.modules;

import com.google.inject.AbstractModule;
import org.nevermined.worldevents.hooks.Placeholders;

public class HooksModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Placeholders.class);
    }
}
