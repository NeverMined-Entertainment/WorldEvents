package org.nevermined.worldevents.api;

public final class WEApi {

    private static WorldEventsApi instance;

    public static void setInstance(WorldEventsApi instance) {
        WEApi.instance = instance;
    }

    public static WorldEventsApi getInstance() {
        return instance;
    }
}
