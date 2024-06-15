package org.nevermined.worldevents.commands.modules;

import com.google.inject.AbstractModule;
import org.nevermined.worldevents.commands.WorldEventsCommand;

public class CommandModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(WorldEventsCommand.class);
    }

}
