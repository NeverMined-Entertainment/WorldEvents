package org.nevermined.worldevents.command.module;

import com.google.inject.AbstractModule;
import org.nevermined.worldevents.command.WorldEventsCommand;

public class CommandModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(WorldEventsCommand.class);
    }

}
