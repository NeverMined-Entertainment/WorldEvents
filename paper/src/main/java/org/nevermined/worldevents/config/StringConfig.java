package org.nevermined.worldevents.config;

import com.google.inject.Singleton;
import me.wyne.wutils.config.Config;
import me.wyne.wutils.config.ConfigEntry;
import org.nevermined.worldevents.api.config.StringConfigApi;

@Singleton
public class StringConfig implements StringConfigApi {

    @ConfigEntry(section = "Strings.Format", path = "event-expire-format")
    private String eventExpireFormat = "%2dч:%2dм:%2dс";

    @Override
    public String getEventExpireFormat() {
        return eventExpireFormat;
    }
}
