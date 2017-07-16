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

import jclp.function.BiFunction;
import jclp.io.IOUtils;
import jclp.log.Log;
import lombok.NonNull;
import lombok.val;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import static jclp.util.Validate.checkNotNull;
import static jclp.util.Validate.requireNotEmpty;

public final class Implementor<T> {
    private static final String TAG = "Implementor";

    private final Class<T> type;
    private final boolean reusable;
    private final ClassLoader loader;

    private final ReentrantLock lock = new ReentrantLock();
    private final Map<String, ImpHolder<T>> impHolders = new LinkedHashMap<>();

    /**
     * Constructs a reusable instance for specified type.
     *
     * @param type class of the base type
     */
    public Implementor(@NonNull Class<T> type) {
        this(type, null, true);
    }

    /**
     * Constructs an instance for specified type.
     *
     * @param type     class of the base type
     * @param reusable {@literal true} for reusable instance
     */
    public Implementor(@NonNull Class<T> type, boolean reusable) {
        this(type, null, reusable);
    }

    /**
     * Constructs object for specified class type.
     *
     * @param type     class of the base type
     * @param loader   the class loader for loading sub-class
     * @param reusable {@literal true} for reusable instance
     */
    public Implementor(@NonNull Class<T> type, ClassLoader loader, boolean reusable) {
        this.type = type;
        this.loader = loader;
        this.reusable = reusable;
    }

    /**
     * Registers new implementation with name and class path.
     * <p>
     * <strong>NOTE:</strong> The previous implementation will be
     * overwritten.
     *
     * @param name name of the implementation
     * @param path full class path of the implementation
     */
    public void register(String name, String path) {
        requireNotEmpty(name, "name cannot be null or empty");
        requireNotEmpty(path, "path cannot be null or empty");
        lock.lock();
        try {
            val imp = impHolders.get(name);
            if (imp != null) {
                imp.reset().path = path;
            } else {
                impHolders.put(name, new ImpHolder<T>(path));
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Registers new implementation with name and class.
     * <p>
     * <strong>NOTE:</strong> The previous implementation will be overwritten.
     *
     * @param name  name of the implementation
     * @param clazz class of the implementation
     */
    public void register(String name, @NonNull Class<? extends T> clazz) {
        requireNotEmpty(name, "name cannot be null or empty");
        lock.lock();
        try {
            val imp = impHolders.get(name);
            if (imp != null) {
                imp.reset().clazz = clazz;
            } else {
                impHolders.put(name, new ImpHolder<>(clazz));
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Gets names of registered implementations.
     *
     * @return the set of names
     */
    public Set<String> names() {
        lock.lock();
        try {
            return Collections.unmodifiableSet(impHolders.keySet());
        } finally {
            lock.unlock();
        }
    }

    /**
     * Tests if implementation with the specified name registered.
     *
     * @param name the name of implementation
     * @return {@literal true} if registered, otherwise {@literal false}
     */
    public boolean contains(String name) {
        lock.lock();
        try {
            return impHolders.containsKey(name);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Removes the implementation for specified name.
     *
     * @param name name of the implementation
     */
    public void remove(String name) {
        lock.lock();
        try {
            impHolders.remove(name);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Removes all implementation.
     */
    public void reset() {
        lock.lock();
        try {
            impHolders.clear();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns an instance for specified implementation name.
     *
     * @param name name of the implementation
     * @see #getInstance(String, ClassLoader)
     */
    public T getInstance(@NonNull String name) throws ReflectiveOperationException {
        return getInstance(name, null);
    }

    /**
     * Returns an instance for specified implementation name.
     *
     * @param name name of the implementation
     * @return instance for the implementation, return {@literal null} if no implementation found
     * @throws ReflectiveOperationException if the instance or implementation class cannot be created
     */
    public T getInstance(@NonNull String name, ClassLoader loader) throws ReflectiveOperationException {
        lock.lock();
        try {
            val imp = impHolders.get(name);
            return imp != null
                    ? imp.getInstance(type, loader != null ? loader : this.loader, reusable)
                    : null;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Loads registry from specified input.
     * <p>
     * The content of the input must be: [name]=[path to class].
     *
     * @param path   path to input
     * @param parser the parser for parse the value in each line
     */
    public void load(String path, BiFunction<? super String, ? super String, ? extends String> parser) {
        lock.lock();
        try {
            load(path, loader, parser);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Loads registry from specified input.
     * <p>
     * The content of the input must be: [name]=[path to class].
     *
     * @param path   path to input
     * @param loader the class loader for load resources
     * @param parser the parser for parse the value in each line
     */
    public void load(String path, ClassLoader loader, BiFunction<? super String, ? super String, ? extends String> parser) {
        lock.lock();
        try {
            val urls = IOUtils.resourcesFor(path, loader);
            if (urls == null) {
                return;
            }
            val prop = new Properties();
            for (val url : urls) {
                try (val in = url.openStream()) {
                    prop.load(in);
                } catch (IOException e) {
                    Log.e(TAG, "cannot load register file", e);
                }
            }
            for (val e : prop.entrySet()) {
                val name = e.getKey().toString();
                val value = e.getValue().toString();
                register(name, parser == null ? value.trim() : parser.apply(name, value));
            }
        } finally {
            lock.unlock();
        }
    }

    private static class ImpHolder<T> {
        private String path;
        private T cache = null;
        private Class<? extends T> clazz;

        ImpHolder(String path) {
            this.path = path;
        }

        ImpHolder(Class<? extends T> clazz) {
            this.clazz = clazz;
        }

        ImpHolder<T> reset() {
            path = null;
            clazz = null;
            cache = null;
            return this;
        }

        /**
         * Creates a new instance of implement for {@code T}.
         *
         * @param loader the class loader
         * @return the new instance or {@code null} if class for path does not extends from {@code T}.
         * @throws ReflectiveOperationException if the instance or implementation class cannot be created
         */
        @SuppressWarnings("unchecked")
        T getInstance(Class<T> type, ClassLoader loader, boolean reusable) throws ReflectiveOperationException {
            if (reusable && cache != null) {
                return cache;
            }
            if (clazz == null) {
                checkNotNull(path, "No path and class specified");
                val klass = loader != null ? Class.forName(path, true, loader) : Class.forName(path);
                if (!type.isAssignableFrom(klass)) {
                    Log.d(TAG, "{0} is not sub class of {1}", klass, type);
                    return null;
                }
                clazz = (Class<T>) klass;
            }
            val inst = clazz.newInstance();
            if (reusable) {
                cache = inst;
            }
            return inst;
        }
    }
}
