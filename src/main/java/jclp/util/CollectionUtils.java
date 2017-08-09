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

package jclp.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.RandomAccess;
import java.util.Set;

import jclp.function.BiFunction;
import jclp.function.Function;
import jclp.function.Predicate;
import jclp.io.IOUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

public final class CollectionUtils {
    private CollectionUtils() {
    }

    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> c) {
        return !isEmpty(c);
    }

    public static boolean isEmpty(Map<?, ?> m) {
        return m == null || m.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> m) {
        return !isEmpty(m);
    }

    public static <E> Iterator<E> repeated(E item, int count) {
        return new RepeatIterator<>(item, count);
    }

    public static <E> Iterator<E> iterator(@NonNull Enumeration<E> e) {
        return new EnumerationIterator<>(e);
    }

    public static <E> Iterable<E> iterable(@NonNull final Iterator<E> i) {
        return new Iterable<E>() {
            @Override
            public Iterator<E> iterator() {
                return i;
            }
        };
    }

    public static <E> E firstOf(Iterable<E> i) {
        if (i == null) {
            return null;
        } else if (i instanceof RandomAccess) {
            val list = (List<E>) i;
            return list.isEmpty() ? null : list.get(0);
        }
        return firstOf(i.iterator());
    }

    public static <E> E firstOf(Iterator<E> i) {
        return i == null ? null : (i.hasNext() ? i.next() : null);
    }

    public static <E> E lastOf(Iterable<E> i) {
        if (i == null) {
            return null;
        } else if (i instanceof RandomAccess) {
            val list = (List<E>) i;
            return list.isEmpty() ? null : list.get(list.size() - 1);
        } else {
            return lastOf(i.iterator());
        }
    }

    public static <E> E lastOf(Iterator<E> i) {
        if (i == null) {
            return null;
        }
        E obj = null;
        while (i.hasNext()) {
            obj = i.next();
        }
        return obj;
    }

    public static <E, T> Iterator<T> map(Iterator<E> i, Function<? super E, ? extends T> transform) {
        return new MapIterator<>(i, transform);
    }

    public static <E, T> Iterator<T> mapIndexed(Iterator<E> i, BiFunction<? super E, Integer, ? extends T> transform) {
        return new IndexedMapIterator<>(i, transform);
    }

    public static <E> Iterator<E> filter(Iterator<E> i, Predicate<? super E> filter) {
        return new FilterIterator<>(i, filter);
    }

    public static <K, V> V getOrElse(Map<K, V> m, K key, Function<K, ? extends V> supplier) {
        return getOrElse(m, key, false, supplier);
    }

    public static <K, V> V getOrElse(@NonNull Map<K, V> m, K key, boolean nullabe, Function<K, ? extends V> supplier) {
        val value = m.get(key);
        if (value != null || (nullabe && m.containsKey(key))) {
            return value;
        }
        return supplier.apply(key);
    }

    public static <K, V> V getOrPut(Map<K, V> m, K key, Function<K, ? extends V> supplier) {
        return getOrPut(m, key, false, supplier);
    }

    public static <K, V> V getOrPut(@NonNull Map<K, V> m, K key, boolean nullabe, Function<K, ? extends V> supplier) {
        V value = m.get(key);
        if (value != null || (nullabe && m.containsKey(key))) {
            return value;
        }
        value = supplier.apply(key);
        if (value != null || nullabe) {
            m.put(key, value);
        }
        return value;
    }

    public static <E> void extend(@NonNull Collection<E> c, Iterable<? extends E> i) {
        if (i == null) {
            return;
        }
        if (i instanceof RandomAccess) {
            val list = (List<? extends E>) i;
            for (int j = 0, end = list.size(); j < end; ++j) {
                c.add(list.get(j));
            }
        } else {
            extend(c, i.iterator());
        }
    }

    public static <E> void extend(@NonNull Collection<E> c, Iterator<? extends E> i) {
        if (i == null) {
            return;
        }
        while (i.hasNext()) {
            c.add(i.next());
        }
    }

    public static <K, V> void update(@NonNull Map<K, V> m, Iterable<? extends Map.Entry<? extends K, ? extends V>> i) {
        if (i == null) {
            return;
        }
        if (i instanceof RandomAccess) {
            val list = (List<? extends Map.Entry<? extends K, ? extends V>>) i;
            for (int j = 0, end = list.size(); j < end; ++j) {
                val e = list.get(j);
                m.put(e.getKey(), e.getValue());
            }
        } else {
            update(m, i.iterator());
        }
    }

    public static <K, V> void update(@NonNull Map<K, V> m, Iterator<? extends Map.Entry<? extends K, ? extends V>> i) {
        if (i == null) {
            return;
        }
        while (i.hasNext()) {
            val e = i.next();
            m.put(e.getKey(), e.getValue());
        }
    }

    public static void update(@NonNull Map<? super String, ? super String> m, Properties p) {
        if (isEmpty(p)) {
            return;
        }
        for (val e : p.entrySet()) {
            m.put(e.getKey().toString(), StringUtils.toString(e.getValue()));
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
        val m = new HashMap<K, V>();
        fillMap(m, objects);
        return Collections.unmodifiableMap(m);
    }

    public static <K, V> Map<K, V> mapOf(@NonNull Collection<V> c) {
        val m = new HashMap<K, V>();
        fillMap(m, c);
        return Collections.unmodifiableMap(m);
    }

    @SuppressWarnings("unchecked")
    public static <K, V> void fillMap(@NonNull Map<K, V> m, Collection<?> c) {
        if (isEmpty(c)) {
            return;
        }
        val size = c.size();
        Validate.require(size % 2 == 0, "length(%d) of objects must % 2 = 0", size);
        for (Iterator<?> i = c.iterator(); i.hasNext();) {
            m.put((K) i.next(), (V) i.next());
        }
    }

    @SuppressWarnings("unchecked")
    public static <K, V> void fillMap(@NonNull Map<K, V> m, Object... objects) {
        if (ArrayUtils.isEmpty(objects)) {
            return;
        }
        val size = objects.length;
        Validate.require(size % 2 == 0, "length(%d) of objects must % 2 = 0", size);
        for (int i = 0; i < size; i += 2) {
            m.put((K) objects[i], (V) objects[i + 1]);
        }
    }

    public static Properties propertiesFor(@NonNull String path) throws IOException {
        return propertiesFor(path, null);
    }

    public static Properties propertiesFor(@NonNull String path, ClassLoader loader) throws IOException {
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

    @RequiredArgsConstructor
    private static class EnumerationIterator<E> implements Iterator<E> {
        private final Enumeration<E> e;

        @Override
        public boolean hasNext() {
            return e.hasMoreElements();
        }

        @Override
        public E next() {
            return e.nextElement();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @RequiredArgsConstructor
    private static class MapIterator<E, T> implements Iterator<T> {
        @NonNull
        private final Iterator<E> source;

        @NonNull
        private final Function<? super E, ? extends T> transform;

        @Override
        public boolean hasNext() {
            return source.hasNext();
        }

        @Override
        public T next() {
            return transform.apply(source.next());
        }

        @Override
        public void remove() {
            source.remove();
        }
    }

    @RequiredArgsConstructor
    private static class IndexedMapIterator<E, T> implements Iterator<T> {
        @NonNull
        private final Iterator<E> source;

        @NonNull
        private final BiFunction<? super E, Integer, ? extends T> transform;

        private int index = 0;

        @Override
        public boolean hasNext() {
            return source.hasNext();
        }

        @Override
        public T next() {
            return transform.apply(source.next(), index++);
        }

        @Override
        public void remove() {
            source.remove();
        }
    }

    @RequiredArgsConstructor
    private static class FilterIterator<E> implements Iterator<E> {
        @NonNull
        private final Iterator<E> source;

        @NonNull
        private final Predicate<? super E> filter;

        private E next;

        private boolean nextFound = false;

        @Override
        public boolean hasNext() {
            if (nextFound) {
                return true;
            }
            E e;
            while (source.hasNext()) {
                e = source.next();
                if (filter.test(e)) {
                    next = e;
                    nextFound = true;
                    return true;
                }
            }
            return false;
        }

        @Override
        public E next() {
            if (!nextFound) {
                throw new NoSuchElementException();
            }
            nextFound = false;
            return next;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static class RepeatIterator<E> implements Iterator<E> {
        private E item;
        private int count;
        private int i = 0;

        RepeatIterator(E item, int count) {
            this.item = item;
            this.count = count;
        }

        @Override
        public boolean hasNext() {
            return i < count;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            i++;
            return item;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
