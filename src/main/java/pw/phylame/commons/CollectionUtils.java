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

package pw.phylame.commons;

import lombok.Cleanup;
import lombok.NonNull;
import lombok.val;
import pw.phylame.commons.io.ResourceUtils;

import java.io.IOException;
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

    public static <E> E getFirst(Collection<E> c) {
        if (isEmpty(c)) {
            return null;
        } else if (c instanceof List) {
            val list = (List<E>) c;
            return list.isEmpty() ? null : list.get(0);
        }
        val it = c.iterator();
        return it.hasNext() ? it.next() : null;
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
            m.put(key, value);
        }
        return value;
    }

}
