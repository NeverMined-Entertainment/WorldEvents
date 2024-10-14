package org.nevermined.worldevents.api.wrapper;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public interface PromiseWrapper<V> extends Future<V>, TerminableWrapper {

    boolean cancel();
    V join();
    V getNow(V valueIfAbsent);
    CompletableFuture<V> toCompletableFuture();
    Object getPromise();

}
