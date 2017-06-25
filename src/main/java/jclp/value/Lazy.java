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

package jclp.value;

import jclp.function.Provider;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class Lazy<T> implements Value<T> {
    @Getter
    private volatile boolean initialized = false;

    @NonNull
    private final Provider<? extends T> provider;

    private final Value<? extends T> fallback;

    @Getter
    private Exception error = null;

    private T value = null;

    public Lazy(@NonNull Provider<? extends T> provider) {
        this.provider = provider;
        this.fallback = null;
    }

    @Override
    public final T get() {
        if (!initialized) {
            synchronized (this) {
                if (!initialized) {
                    try {
                        value = provider.provide();
                    } catch (Exception e) {
                        value = Values.get(fallback);
                        error = e;
                    }
                    initialized = true;
                }
            }
        }
        return value;
    }
}
