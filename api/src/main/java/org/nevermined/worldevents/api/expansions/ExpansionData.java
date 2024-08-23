package org.nevermined.worldevents.api.expansions;

import org.nevermined.worldevents.api.core.WorldEventAction;

import java.time.Instant;
import java.util.function.Supplier;

public record ExpansionData(String key, Supplier<WorldEventAction> actionSupplier, String jarName, String className, Instant registryTime) {
}
