package org.nevermined.worldevents.api.wrapper;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class PlaceholderWrapper {

    public static TextReplacementWrapper replace(String key, String value)
    {
        return (string) -> string.replaceAll("<"+key+">", value);
    }

    public static TextReplacementWrapper legacy(String key, Component value)
    {
        return (string) -> string.replaceAll("<"+key+">", LegacyComponentSerializer.legacyAmpersand().serialize(value));
    }

    public static TextReplacementWrapper regex(String regex, String value)
    {
        return (string) -> string.replaceAll(regex, value);
    }

}
