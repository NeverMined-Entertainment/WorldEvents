package org.nevermined.worldevents.api.wrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public interface PromiseProvider {

    @Nonnull
    <U> PromiseWrapper<U> empty();

    /**
     * Returns a new base promise to be built on top of.
     *
     * @return a new promise
     */
    @Nonnull
    PromiseWrapper<Void> start();

    /**
     * Returns a Promise which is already completed with the given value.
     *
     * @param value the value
     * @param <U> the result type
     * @return a new completed promise
     */
    @Nonnull
    <U> PromiseWrapper<U> completed(@Nullable U value);

    /**
     * Returns a Promise which is already completed with the given exception.
     *
     * @param exception the exception
     * @param <U> the result type
     * @return the new completed promise
     */
    @Nonnull
    <U> PromiseWrapper<U> exceptionally(@Nonnull Throwable exception);

    /**
     * Returns a Promise which represents the given future.
     *
     * <p>The implementation will make an attempt to wrap the future without creating a new process
     * to await the result (by casting to {@link java.util.concurrent.CompletionStage} or
     * {@link com.google.common.util.concurrent.ListenableFuture}).</p>
     *
     * <p>Calls to {@link org.nevermined.worldevents.api.wrapper.PromiseWrapper#cancel() cancel} the returned promise will not affected the wrapped
     * future.</p>
     *
     * @param future the future to wrap
     * @param <U> the result type
     * @return the new promise
     */
    @Nonnull
    <U> PromiseWrapper<U> wrapFuture(@Nonnull Future<U> future);

    /**
     * Returns a new Promise, and schedules it's population via the given supplier.
     *
     * @param context the type of executor to use to supply the promise
     * @param supplier the value supplier
     * @param <U> the result type
     * @return the promise
     */
    @Nonnull
    <U> PromiseWrapper<U> supplying(@Nonnull ThreadContextWrapper context, @Nonnull Supplier<U> supplier);

    /**
     * Returns a new Promise, and schedules it's population via the given supplier.
     *
     * @param supplier the value supplier
     * @param <U> the result type
     * @return the promise
     */
    @Nonnull
    <U> PromiseWrapper<U> supplyingSync(@Nonnull Supplier<U> supplier);

    /**
     * Returns a new Promise, and schedules it's population via the given supplier.
     *
     * @param supplier the value supplier
     * @param <U> the result type
     * @return the promise
     */
    @Nonnull
    <U> PromiseWrapper<U> supplyingAsync(@Nonnull Supplier<U> supplier);

    /**
     * Returns a new Promise, and schedules it's population via the given supplier,
     * after the delay has elapsed.
     *
     * @param context the type of executor to use to supply the promise
     * @param supplier the value supplier
     * @param delayTicks the delay in ticks
     * @param <U> the result type
     * @return the promise
     */
    @Nonnull
    <U> PromiseWrapper<U> supplyingDelayed(@Nonnull ThreadContextWrapper context, @Nonnull Supplier<U> supplier, long delayTicks);

    /**
     * Returns a new Promise, and schedules it's population via the given supplier,
     * after the delay has elapsed.
     *
     * @param context the type of executor to use to supply the promise
     * @param supplier the value supplier
     * @param delay the delay
     * @param unit the unit of delay
     * @param <U> the result type
     * @return the promise
     */
    @Nonnull
    <U> PromiseWrapper<U> supplyingDelayed(@Nonnull ThreadContextWrapper context, @Nonnull Supplier<U> supplier, long delay, @Nonnull TimeUnit unit);

    /**
     * Returns a new Promise, and schedules it's population via the given supplier,
     * after the delay has elapsed.
     *
     * @param supplier the value supplier
     * @param delayTicks the delay in ticks
     * @param <U> the result type
     * @return the promise
     */
    @Nonnull
    <U> PromiseWrapper<U> supplyingDelayedSync(@Nonnull Supplier<U> supplier, long delayTicks);

    /**
     * Returns a new Promise, and schedules it's population via the given supplier,
     * after the delay has elapsed.
     *
     * @param supplier the value supplier
     * @param delay the delay
     * @param unit the unit of delay
     * @param <U> the result type
     * @return the promise
     */
    @Nonnull
    <U> PromiseWrapper<U> supplyingDelayedSync(@Nonnull Supplier<U> supplier, long delay, @Nonnull TimeUnit unit);

    /**
     * Returns a new Promise, and schedules it's population via the given supplier,
     * after the delay has elapsed.
     *
     * @param supplier the value supplier
     * @param delayTicks the delay in ticks
     * @param <U> the result type
     * @return the promise
     */
    @Nonnull
    <U> PromiseWrapper<U> supplyingDelayedAsync(@Nonnull Supplier<U> supplier, long delayTicks);

    /**
     * Returns a new Promise, and schedules it's population via the given supplier,
     * after the delay has elapsed.
     *
     * @param supplier the value supplier
     * @param delay the delay
     * @param unit the unit of delay
     * @param <U> the result type
     * @return the promise
     */
    @Nonnull
    <U> PromiseWrapper<U> supplyingDelayedAsync(@Nonnull Supplier<U> supplier, long delay, @Nonnull TimeUnit unit);

    /**
     * Returns a new Promise, and schedules it's population via the given callable.
     *
     * @param context the type of executor to use to supply the promise
     * @param callable the value callable
     * @param <U> the result type
     * @return the promise
     */
    @Nonnull
    <U> PromiseWrapper<U> supplyingExceptionally(@Nonnull ThreadContextWrapper context, @Nonnull Callable<U> callable);

    /**
     * Returns a new Promise, and schedules it's population via the given callable.
     *
     * @param callable the value callable
     * @param <U> the result type
     * @return the promise
     */
    @Nonnull
    <U> PromiseWrapper<U> supplyingExceptionallySync(@Nonnull Callable<U> callable);

    /**
     * Returns a new Promise, and schedules it's population via the given callable.
     *
     * @param callable the value callable
     * @param <U> the result type
     * @return the promise
     */
    @Nonnull
    <U> PromiseWrapper<U> supplyingExceptionallyAsync(@Nonnull Callable<U> callable);

    /**
     * Returns a new Promise, and schedules it's population via the given callable,
     * after the delay has elapsed.
     *
     * @param context the type of executor to use to supply the promise
     * @param callable the value callable
     * @param delayTicks the delay in ticks
     * @param <U> the result type
     * @return the promise
     */
    @Nonnull
    <U> PromiseWrapper<U> supplyingExceptionallyDelayed(@Nonnull ThreadContextWrapper context, @Nonnull Callable<U> callable, long delayTicks);

    /**
     * Returns a new Promise, and schedules it's population via the given callable,
     * after the delay has elapsed.
     *
     * @param context the type of executor to use to supply the promise
     * @param callable the value callable
     * @param delay the delay
     * @param unit the unit of delay
     * @param <U> the result type
     * @return the promise
     */
    @Nonnull
    <U> PromiseWrapper<U> supplyingExceptionallyDelayed(@Nonnull ThreadContextWrapper context, @Nonnull Callable<U> callable, long delay, @Nonnull TimeUnit unit);

    /**
     * Returns a new Promise, and schedules it's population via the given callable,
     * after the delay has elapsed.
     *
     * @param callable the value callable
     * @param delayTicks the delay in ticks
     * @param <U> the result type
     * @return the promise
     */
    @Nonnull
    <U> PromiseWrapper<U> supplyingExceptionallyDelayedSync(@Nonnull Callable<U> callable, long delayTicks);

    /**
     * Returns a new Promise, and schedules it's population via the given callable,
     * after the delay has elapsed.
     *
     * @param callable the value callable
     * @param delay the delay
     * @param unit the unit of delay
     * @param <U> the result type
     * @return the promise
     */
    @Nonnull
    <U> PromiseWrapper<U> supplyingExceptionallyDelayedSync(@Nonnull Callable<U> callable, long delay, @Nonnull TimeUnit unit);

    /**
     * Returns a new Promise, and schedules it's population via the given callable,
     * after the delay has elapsed.
     *
     * @param callable the value callable
     * @param delayTicks the delay in ticks
     * @param <U> the result type
     * @return the promise
     */
    @Nonnull
    <U> PromiseWrapper<U> supplyingExceptionallyDelayedAsync(@Nonnull Callable<U> callable, long delayTicks);

    /**
     * Returns a new Promise, and schedules it's population via the given callable,
     * after the delay has elapsed.
     *
     * @param callable the value callable
     * @param delay the delay
     * @param unit the unit of delay
     * @param <U> the result type
     * @return the promise
     */
    @Nonnull
    <U> PromiseWrapper<U> supplyingExceptionallyDelayedAsync(@Nonnull Callable<U> callable, long delay, @Nonnull TimeUnit unit);

}
