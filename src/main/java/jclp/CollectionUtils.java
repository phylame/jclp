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

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;

import java.util.*;
import java.util.function.Function;

/**
 * Utilities for collection.
 */
public final class CollectionUtils {
    private CollectionUtils() {
    }

    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> c) {
        return c != null && !c.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> c) {
        return c == null || c.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> c) {
        return c != null && !c.isEmpty();
    }

    public static <K, V> V getOrElse(@NonNull Map<K, V> m, K key, Function<K, ? extends V> creator) {
        return getOrElse(m, key, !(m instanceof Dictionary), creator);
    }

    public static <K, V> V getOrElse(@NonNull Map<K, V> m, K key, boolean nullabe, Function<K, ? extends V> creator) {
        V value = m.get(key);
        if (value == null && (!nullabe || !m.containsKey(key))) {
            value = creator.apply(key);
        }
        return value;
    }

    public static <K, V> V getOrPut(@NonNull Map<K, V> m, K key, Function<K, ? extends V> creator) {
        return getOrPut(m, key, !(m instanceof Dictionary), creator);
    }

    public static <K, V> V getOrPut(@NonNull Map<K, V> m, K key, boolean nullabe, Function<K, ? extends V> creator) {
        V value = m.get(key);
        if (value == null && (!nullabe || !m.containsKey(key))) {
            value = creator.apply(key);
            if (nullabe || value != null) {
                m.put(key, value);
            }
        }
        return value;
    }

    public static <E> Set<E> setOf(E element) {
        return Collections.singleton(element);
    }

    @SafeVarargs
    public static <E> Set<E> setOf(E... elements) {
        val set = new HashSet<E>();
        Collections.addAll(set, elements);
        return Collections.unmodifiableSet(set);
    }

    public static <E> List<E> listOf(E element) {
        return Collections.singletonList(element);
    }

    @SafeVarargs
    public static <E> List<E> listOf(E... elements) {
        return Arrays.asList(elements);
    }
}
