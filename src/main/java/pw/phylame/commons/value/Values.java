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
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import pw.phylame.commons.function.Function;
import pw.phylame.commons.function.Provider;

import java.lang.ref.Reference;

public final class Values {
    private Values() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Object value) {
        return value instanceof Value<?> ? ((Value<T>) value).get() : (T) value;
    }

    public static <T> T get(Value<T> value) {
        return value != null ? value.get() : null;
    }

    public static <T> T get(Value<T> value, T fallback) {
        return value != null ? value.get() : fallback;
    }

    public static <T> WrapperValue<T> wrap(T value) {
        return new WrapperValue<>(value);
    }

    public static <T> Value<T> reference(@NonNull Reference<? extends T> reference) {
        return new ReferenceValue<>(reference);
    }

    public static <T> ProviderValue<T> provider(@NonNull Provider<? extends T> provider) {
        return new ProviderValue<>(provider);
    }

    public static <T> ObserverValue<T> observer(@NonNull Value<T> value, @NonNull Function<? super T, ? extends T> observer) {
        return new ObserverValue<>(value, observer);
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

    @RequiredArgsConstructor
    private static final class WrapperValue<T> implements Value<T> {
        private final T value;

        @Override
        public T get() {
            return value;
        }
    }

    @RequiredArgsConstructor
    private static final class ProviderValue<T> implements Value<T> {
        private final Provider<? extends T> provider;

        @Override
        @SneakyThrows(Exception.class)
        public T get() {
            return provider.provide();
        }
    }

    @RequiredArgsConstructor
    private static final class ReferenceValue<T> implements Value<T> {
        private final Reference<? extends T> reference;

        @Override
        public T get() {
            return reference.get();
        }
    }

    @RequiredArgsConstructor
    private static final class ObserverValue<T> implements Value<T> {
        private final Value<T> value;

        private final Function<? super T, ? extends T> observer;

        @Override
        public T get() {
            return observer.apply(value.get());
        }
    }
}
