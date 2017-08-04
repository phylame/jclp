package jclp.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import jclp.log.Log;
import lombok.NonNull;
import lombok.val;

public abstract class AsyncTask<V> {
    private static final String TAG = "AsyncTask";

    protected abstract V handleGet() throws Exception;

    public final void reset() {
        processed.set(false);
    }

    public final void schedule(@NonNull ExecutorService executor) {
        synchronized (this) {
            if (future.get() != null) {
                Log.t(TAG, "already submitted in some thread");
                return;
            }
            future.set(executor.submit(action));
        }
    }

    public final V get() throws Exception {
        if (processed.get()) {
            return value;
        }
        val future = this.future.get();
        if (future != null) {
            return future.get();
        } else {
            return action.call();
        }
    }

    private V value = null;
    private final AtomicBoolean processed = new AtomicBoolean();
    private final AtomicReference<Future<V>> future = new AtomicReference<>();

    private final Callable<V> action = new Callable<V>() {
        @Override
        public V call() throws Exception {
            if (!processed.get()) {
                synchronized (this) {
                    if (!processed.get()) {
                        value = handleGet();
                        processed.set(true);
                        future.set(null);
                    }
                }
            }
            return value;
        }
    };
}
