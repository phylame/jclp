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

import lombok.NonNull;

import java.lang.ref.Reference;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Values {
    private Values() {
    }

    public static <T> T get(Value<T> value) {
        return value != null ? value.get() : null;
    }

    public static <T> T get(Value<T> value, T fallback) {
        return value != null ? value.get() : fallback;
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Object value) {
        return value instanceof Value<?> ? ((Value<T>) value).get() : (T) value;
    }

    public static <T> Value<T> wrap(T value) {
        return () -> value;
    }

    public static <T> Lazy<T> lazy(@NonNull Supplier<? extends T> supplier) {
        return new Lazy<>(supplier);
    }

    public static <T> Value<T> supplier(@NonNull Supplier<? extends T> supplier) {
        return supplier::get;
    }

    public static <T> Value<T> reference(@NonNull Reference<? extends T> reference) {
        return reference::get;
    }

    public static <T> Value<T> observer(@NonNull Value<T> value, @NonNull Function<? super T, ? extends T> observer) {
        return () -> observer.apply(value.get());
    }
}
