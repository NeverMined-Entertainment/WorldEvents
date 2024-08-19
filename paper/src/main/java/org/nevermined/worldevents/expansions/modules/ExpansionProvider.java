package org.nevermined.worldevents.expansions.modules;

import com.google.inject.Inject;
import com.google.inject.Provider;
import me.wyne.wutils.log.Log;
import org.nevermined.worldevents.WorldEvents;
import org.nevermined.worldevents.api.core.WorldEventAction;

import java.util.Map;

public class ExpansionProvider implements Provider<Map<String, WorldEventAction>> {

    private final WorldEvents plugin;

    @Inject
    public ExpansionProvider(WorldEvents plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public Map<String, WorldEventAction> get() {
        plugin.getRegisteredExpansions().forEach((s, worldEventAction) -> Log.global.warn("Get: " + s));
        return plugin.getRegisteredExpansions();
    }

}
