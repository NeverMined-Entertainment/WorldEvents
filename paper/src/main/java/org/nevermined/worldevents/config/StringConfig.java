package org.nevermined.worldevents.config;

import com.google.inject.Singleton;
import me.wyne.wutils.config.Config;
import me.wyne.wutils.config.ConfigEntry;

@Singleton
public class StringConfig {

    @ConfigEntry(section = "Strings.Format", path = "event-expire-format")
    private String eventExpireFormat = "%2dч:%2dм:%2dс";

    public String getEventExpireFormat() {
        return eventExpireFormat;
    }
}
