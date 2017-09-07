/*
 * Copyright 2017 Peng Wan <phylame@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jclp;

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
