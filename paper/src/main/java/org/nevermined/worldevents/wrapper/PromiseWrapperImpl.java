package org.nevermined.worldevents.wrapper;

import me.lucko.helper.promise.Promise;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nevermined.worldevents.api.wrapper.PromiseWrapper;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PromiseWrapperImpl<V> implements PromiseWrapper<V> {

    private final Promise<V> promise;

    public PromiseWrapperImpl(Promise<V> promise) {
        this.promise = promise;
    }

    @Override
    public boolean cancel() {
        return promise.cancel();
    }

    @Override
    public V join() {
        return promise.join();
    }

    @Override
    public V getNow(V valueIfAbsent) {
        return promise.getNow(valueIfAbsent);
    }

    @Override
    public CompletableFuture<V> toCompletableFuture() {
        return promise.toCompletableFuture();
    }

    @Override
    public boolean cancel(boolean b) {
        return promise.cancel(b);
    }

    @Override
    public boolean isCancelled() {
        return promise.isCancelled();
    }

    @Override
    public boolean isDone() {
        return promise.isDone();
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return promise.get();
    }

    @Override
    public V get(long l, @NotNull TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
        return promise.get(l, timeUnit);
    }

    @Override
    public void close() throws Exception {
        promise.close();
    }

    @Override
    public boolean isClosed() {
        return promise.isClosed();
    }

    @Override
    public @Nullable Exception closeSilently() {
        return promise.closeSilently();
    }

    @Override
    public void closeAndReportException() {
        promise.closeAndReportException();
    }

    @Override
    public Object getTerminable() {
        return promise;
    }

    @Override
    public Promise<V> getPromise() {
        return promise;
    }
}
