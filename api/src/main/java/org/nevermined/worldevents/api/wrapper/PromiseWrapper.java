package org.nevermined.worldevents.api.wrapper;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

public interface PromiseWrapper<V> extends Future<V>, TerminableWrapper {

    default boolean cancel() {
        return cancel(true);
    }
    V join();
    V getNow(V valueIfAbsent);
    CompletableFuture<V> toCompletableFuture();

}
