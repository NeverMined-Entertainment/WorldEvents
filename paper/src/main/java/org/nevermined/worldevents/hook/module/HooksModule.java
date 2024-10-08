package org.nevermined.worldevents.hook.module;

import com.google.inject.AbstractModule;
import org.nevermined.worldevents.hook.Placeholders;

public class HooksModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Placeholders.class);
    }
}
