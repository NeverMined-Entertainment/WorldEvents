package org.nevermined.worldevents.expansion.module;

import com.google.inject.AbstractModule;
import org.nevermined.worldevents.api.expansion.ExpansionRegistryApi;
import org.nevermined.worldevents.expansion.ExpansionLoader;
import org.nevermined.worldevents.expansion.ExpansionRegistry;

import java.io.File;

public class ExpansionModule extends AbstractModule {

    private final File expansionDirectory;

    public ExpansionModule(File expansionDirectory) {
        this.expansionDirectory = expansionDirectory;
    }

    @Override
    protected void configure() {
        bind(File.class)
                .annotatedWith(ExpansionLoader.ExpansionDirectory.class)
                .toInstance(expansionDirectory);
        bind(ExpansionRegistryApi.class).to(ExpansionRegistry.class);
        bind(ExpansionLoader.class);
    }

}
