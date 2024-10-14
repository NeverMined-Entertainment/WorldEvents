package org.nevermined.worldevents.wrapper;

import me.lucko.helper.promise.Promise;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nevermined.worldevents.api.wrapper.PromiseProvider;
import org.nevermined.worldevents.api.wrapper.PromiseWrapper;
import org.nevermined.worldevents.api.wrapper.ThreadContextWrapper;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class PromiseProviderImpl implements PromiseProvider {

    @Override
    public @NotNull <U> PromiseWrapper<U> empty() {
        return new PromiseWrapperImpl<>(Promise.empty());
    }

    @Override
    public @NotNull PromiseWrapper<Void> start() {
        return new PromiseWrapperImpl<>(Promise.start());
    }

    @Override
    public @NotNull <U> PromiseWrapper<U> completed(@Nullable U value) {
        return new PromiseWrapperImpl<>(Promise.completed(value));
    }

    @Override
    public @NotNull <U> PromiseWrapper<U> exceptionally(@NotNull Throwable exception) {
        return new PromiseWrapperImpl<>(Promise.exceptionally(exception));
    }

    @Override
    public @NotNull <U> PromiseWrapper<U> wrapFuture(@NotNull Future<U> future) {
        return new PromiseWrapperImpl<>(Promise.wrapFuture(future));
    }

    @Override
    public @NotNull <U> PromiseWrapper<U> supplying(@NotNull ThreadContextWrapper context, @NotNull Supplier<U> supplier) {
        return new PromiseWrapperImpl<>(Promise.supplying(WrapperAdapter.adaptThreadContext(context), supplier));
    }

    @Override
    public @NotNull <U> PromiseWrapper<U> supplyingSync(@NotNull Supplier<U> supplier) {
        return new PromiseWrapperImpl<>(Promise.supplyingSync(supplier));
    }

    @Override
    public @NotNull <U> PromiseWrapper<U> supplyingAsync(@NotNull Supplier<U> supplier) {
        return new PromiseWrapperImpl<>(Promise.supplyingAsync(supplier));
    }

    @Override
    public @NotNull <U> PromiseWrapper<U> supplyingDelayed(@NotNull ThreadContextWrapper context, @NotNull Supplier<U> supplier, long delayTicks) {
        return new PromiseWrapperImpl<>(Promise.supplyingDelayed(WrapperAdapter.adaptThreadContext(context), supplier, delayTicks));
    }

    @Override
    public @NotNull <U> PromiseWrapper<U> supplyingDelayed(@NotNull ThreadContextWrapper context, @NotNull Supplier<U> supplier, long delay, @NotNull TimeUnit unit) {
        return new PromiseWrapperImpl<>(Promise.supplyingDelayed(WrapperAdapter.adaptThreadContext(context), supplier, delay, unit));
    }

    @Override
    public @NotNull <U> PromiseWrapper<U> supplyingDelayedSync(@NotNull Supplier<U> supplier, long delayTicks) {
        return new PromiseWrapperImpl<>(Promise.supplyingDelayedSync(supplier, delayTicks));
    }

    @Override
    public @NotNull <U> PromiseWrapper<U> supplyingDelayedSync(@NotNull Supplier<U> supplier, long delay, @NotNull TimeUnit unit) {
        return new PromiseWrapperImpl<>(Promise.supplyingDelayedSync(supplier, delay, unit));
    }

    @Override
    public @NotNull <U> PromiseWrapper<U> supplyingDelayedAsync(@NotNull Supplier<U> supplier, long delayTicks) {
        return new PromiseWrapperImpl<>(Promise.supplyingDelayedAsync(supplier, delayTicks));
    }

    @Override
    public @NotNull <U> PromiseWrapper<U> supplyingDelayedAsync(@NotNull Supplier<U> supplier, long delay, @NotNull TimeUnit unit) {
        return new PromiseWrapperImpl<>(Promise.supplyingDelayedAsync(supplier, delay, unit));
    }

    @Override
    public @NotNull <U> PromiseWrapper<U> supplyingExceptionally(@NotNull ThreadContextWrapper context, @NotNull Callable<U> callable) {
        return new PromiseWrapperImpl<>(Promise.supplyingExceptionally(WrapperAdapter.adaptThreadContext(context), callable));
    }

    @Override
    public @NotNull <U> PromiseWrapper<U> supplyingExceptionallySync(@NotNull Callable<U> callable) {
        return new PromiseWrapperImpl<>(Promise.supplyingExceptionallySync(callable));
    }

    @Override
    public @NotNull <U> PromiseWrapper<U> supplyingExceptionallyAsync(@NotNull Callable<U> callable) {
        return new PromiseWrapperImpl<>(Promise.supplyingExceptionallyAsync(callable));
    }

    @Override
    public @NotNull <U> PromiseWrapper<U> supplyingExceptionallyDelayed(@NotNull ThreadContextWrapper context, @NotNull Callable<U> callable, long delayTicks) {
        return new PromiseWrapperImpl<>(Promise.supplyingExceptionallyDelayed(WrapperAdapter.adaptThreadContext(context), callable, delayTicks));
    }

    @Override
    public @NotNull <U> PromiseWrapper<U> supplyingExceptionallyDelayed(@NotNull ThreadContextWrapper context, @NotNull Callable<U> callable, long delay, @NotNull TimeUnit unit) {
        return new PromiseWrapperImpl<>(Promise.supplyingExceptionallyDelayed(WrapperAdapter.adaptThreadContext(context), callable, delay, unit));
    }

    @Override
    public @NotNull <U> PromiseWrapper<U> supplyingExceptionallyDelayedSync(@NotNull Callable<U> callable, long delayTicks) {
        return new PromiseWrapperImpl<>(Promise.supplyingExceptionallyDelayedSync(callable, delayTicks));
    }

    @Override
    public @NotNull <U> PromiseWrapper<U> supplyingExceptionallyDelayedSync(@NotNull Callable<U> callable, long delay, @NotNull TimeUnit unit) {
        return new PromiseWrapperImpl<>(Promise.supplyingExceptionallyDelayedSync(callable, delay, unit));
    }

    @Override
    public @NotNull <U> PromiseWrapper<U> supplyingExceptionallyDelayedAsync(@NotNull Callable<U> callable, long delayTicks) {
        return new PromiseWrapperImpl<>(Promise.supplyingExceptionallyDelayedAsync(callable, delayTicks));
    }

    @Override
    public @NotNull <U> PromiseWrapper<U> supplyingExceptionallyDelayedAsync(@NotNull Callable<U> callable, long delay, @NotNull TimeUnit unit) {
        return new PromiseWrapperImpl<>(Promise.supplyingExceptionallyDelayedAsync(callable, delay, unit));
    }
}
