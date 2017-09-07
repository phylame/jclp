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

import jclp.io.IOUtils;
import lombok.NonNull;
import lombok.val;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

import static jclp.Validate.require;

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
            m.put(key, value);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    public static <E> void extend(@NonNull Collection<E> c, Iterable<? extends E> i) {
        if (i != null) {
            if (i instanceof Collection) {
                c.addAll((Collection<? extends E>) i);
            } else {
                extend(c, i.iterator());
            }
        }
    }

    public static <E> void extend(@NonNull Collection<E> c, Iterator<? extends E> i) {
        if (i != null) {
            while (i.hasNext()) {
                c.add(i.next());
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <K, V> void update(@NonNull Map<K, V> m, Iterable<? extends Map.Entry<? extends K, ? extends V>> i) {
        if (i != null) {
            if (i instanceof Collection) {
                for (val e : ((Collection<? extends Map.Entry<? extends K, ? extends V>>) i)) {
                    m.put(e.getKey(), e.getValue());
                }
            } else {
                update(m, i.iterator());
            }
        }
    }

    public static <K, V> void update(@NonNull Map<K, V> m, Iterator<? extends Map.Entry<? extends K, ? extends V>> i) {
        if (i != null) {
            while (i.hasNext()) {
                val e = i.next();
                m.put(e.getKey(), e.getValue());
            }
        }
    }

    public static void update(@NonNull Map<? super String, ? super String> m, Properties p) {
        if (isNotEmpty(p)) {
            for (val e : p.entrySet()) {
                m.put(e.getKey().toString(), e.getValue().toString());
            }
        }
    }

    @SafeVarargs
    public static <E> List<E> listOf(E... items) {
        return Arrays.asList(items);
    }

    @SuppressWarnings("unchecked")
    public static <E> List<E> listOf(Iterable<? extends E> i) {
        if (i == null) {
            return Collections.emptyList();
        } else if (i instanceof List) {
            return Collections.unmodifiableList((List<E>) i);
        } else {
            return listOf(i.iterator());
        }
    }

    public static <E> List<E> listOf(Iterator<? extends E> i) {
        if (i == null) {
            return Collections.emptyList();
        } else {
            val list = new ArrayList<E>();
            extend(list, i);
            return Collections.unmodifiableList(list);
        }
    }

    @SafeVarargs
    public static <E> Set<E> setOf(E... items) {
        val set = new HashSet<E>();
        Collections.addAll(set, items);
        return Collections.unmodifiableSet(set);
    }

    @SuppressWarnings("unchecked")
    public static <E> Set<E> setOf(Iterable<? extends E> i) {
        if (i == null) {
            return Collections.emptySet();
        } else if (i instanceof Set) {
            return Collections.unmodifiableSet((Set<E>) i);
        } else {
            return setOf(i.iterator());
        }
    }

    public static <E> Set<E> setOf(Iterator<? extends E> i) {
        if (i == null) {
            return Collections.emptySet();
        } else {
            val set = new HashSet<E>();
            extend(set, i);
            return Collections.unmodifiableSet(set);
        }
    }

    public static <K, V> Map<K, V> mapOf(Object... objects) {
        val map = new HashMap<K, V>();
        fillMap(map, objects);
        return Collections.unmodifiableMap(map);
    }

    public static <K, V> Map<K, V> mapOf(@NonNull Collection<V> c) {
        val map = new HashMap<K, V>();
        fillMap(map, c);
        return Collections.unmodifiableMap(map);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> void fillMap(@NonNull Map<K, V> m, Object... objects) {
        if (ArrayUtils.isEmpty(objects)) {
            return;
        }
        val size = objects.length;
        require(size % 2 == 0, "length(%d) of objects must % 2 = 0", size);
        for (int i = 0; i < size; i += 2) {
            m.put((K) objects[i], (V) objects[i + 1]);
        }
    }

    @SuppressWarnings("unchecked")
    public static <K, V> void fillMap(@NonNull Map<K, V> m, Collection<?> c) {
        if (isEmpty(c)) {
            return;
        }
        val size = c.size();
        require(size % 2 == 0, "length(%d) of objects must % 2 = 0", size);
        for (Iterator<?> i = c.iterator(); i.hasNext(); ) {
            m.put((K) i.next(), (V) i.next());
        }
    }

    public static Properties getProperties(@NonNull String path) throws IOException {
        return getProperties(path, null);
    }

    public static Properties getProperties(@NonNull String path, ClassLoader loader) throws IOException {
        val in = IOUtils.openResource(path, loader);
        if (in != null) {
            val prop = new Properties();
            try {
                prop.load(in);
            } finally {
                in.close();
            }
            return prop;
        }
        return null;
    }
}
