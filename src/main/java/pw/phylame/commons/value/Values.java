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

package pw.phylame.commons.value;

import pw.phylame.commons.function.Function;
import pw.phylame.commons.function.Provider;

public final class Values {
    private Values() {
    }

    public static <T> T get(Value<T> value) {
        return value != null ? value.get() : null;
    }

    public static <T> T get(Value<T> value, T fallback) {
        return value != null ? value.get() : fallback;
    }

    public static <T> Wrapper<T> wrap(T value) {
        return new Wrapper<>(value);
    }

    public static <T> Lazy<T> lazy(Provider<? extends T> provider) {
        return new Lazy<>(provider);
    }

    public static <T> Lazy<T> lazy(Provider<? extends T> provider, T fallback) {
        return new Lazy<>(provider, wrap(fallback));
    }

    public static <T> Lazy<T> lazy(Provider<? extends T> provider, Value<T> fallback) {
        return new Lazy<>(provider, fallback);
    }

    public static <T> Observer<T> observer(Value<T> value, Function<? super T, ? extends T> observer) {
        return new Observer<>(value, observer);
    }
}
