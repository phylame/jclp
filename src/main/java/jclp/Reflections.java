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

import jclp.function.Predicate;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;
import static jclp.StringUtils.capitalized;
import static jclp.StringUtils.isEmpty;

public final class Reflections {
    private Reflections() {
    }

    public static String normalized(String name) {
        if (isEmpty(name)) {
            return name;
        }
        val length = name.length();
        char ch = name.charAt(0);
        if (isUpperCase(ch)) {
            if (length > 2) {
                char next = name.charAt(1);
                if (isLowerCase(next)) {
                    throw new IllegalArgumentException("invalid name: " + name);
                } else {
                    return name;
                }
            } else {
                return name;
            }
        } else if (isLowerCase(ch)) {
            if (length > 2) {
                char next = name.charAt(1);
                if (isUpperCase(next)) {
                    return name;
                } else {
                    return capitalized(name);
                }
            } else {
                return name.toUpperCase();
            }
        }
        return name;
    }

    public static void makeAccessible(@NonNull AccessibleObject object) {
        if (!object.isAccessible()) {
            object.setAccessible(true);
        }
    }

    public static Class<?> getGenericType(@NonNull Class<?> clazz) {
        return getGenericType(clazz, 0);
    }

    public static Class<?> getGenericType(@NonNull Class<?> clazz, int index) {
        val type = clazz.getGenericSuperclass();
        if (!(type instanceof ParameterizedType)) {
            return Object.class;
        }
        val types = ((ParameterizedType) type).getActualTypeArguments();
        if ((index >= types.length) || (index < 0)) {
            return Object.class;
        }
        if (!(types[index] instanceof Class)) {
            return Object.class;
        }
        return (Class<?>) types[index];
    }

    public static Field getField(@NonNull Class<?> clazz, String name) {
        if (isEmpty(name)) {
            return null;
        }
        for (; clazz != null; clazz = clazz.getSuperclass()) {
            try {
                return clazz.getDeclaredField(name);
            } catch (NoSuchFieldException | SecurityException ignored) {
            }
        }
        return null;
    }

    public static List<Field> getFields(@NonNull Class<?> clazz, Predicate<? super Field> filter) {
        val fields = new ArrayList<Field>();
        for (; clazz != null; clazz = clazz.getSuperclass()) {
            for (val field : clazz.getDeclaredFields()) {
                if (filter == null || filter.test(field)) {
                    fields.add(field);
                }
            }
        }
        return fields;
    }

    @SneakyThrows(IllegalAccessException.class)
    public static Object getFieldValue(@NonNull Class<?> clazz, String name) {
        val field = getField(clazz, name);
        if (field == null) {
            throw new RuntimeException("no such field: " + name);
        }
        makeAccessible(field);
        return field.get(null);
    }

    @SneakyThrows(IllegalAccessException.class)
    public static Object getFieldValue(@NonNull Object target, String name) {
        val field = getField(target.getClass(), name);
        if (field == null) {
            throw new RuntimeException("no such field: " + name);
        }
        makeAccessible(field);
        return field.get(target);
    }

    @SneakyThrows(IllegalAccessException.class)
    public static void setFieldValue(@NonNull Class<?> clazz, String name, Object value) {
        val field = getField(clazz, name);
        if (field == null) {
            throw new RuntimeException("no such field: " + name);
        }
        makeAccessible(field);
        field.set(null, value);
    }

    @SneakyThrows(IllegalAccessException.class)
    public static void setFieldValue(@NonNull Object target, String name, Object value) {
        val field = getField(target.getClass(), name);
        if (field == null) {
            throw new RuntimeException("no such field: " + name);
        }
        makeAccessible(field);
        field.set(target, value);
    }

    public static Method getMethod(@NonNull Class<?> clazz, String name, Class<?>... types) {
        if (isEmpty(name)) {
            return null;
        }
        for (; clazz != null; clazz = clazz.getSuperclass()) {
            try {
                return clazz.getDeclaredMethod(name, types);
            } catch (NoSuchMethodException | SecurityException ignored) {
            }
        }
        return null;
    }

    public static List<Method> getMethods(@NonNull Class<?> clazz, Predicate<? super Method> filter) {
        val methods = new ArrayList<Method>();
        for (; clazz != null; clazz = clazz.getSuperclass()) {
            for (val method : clazz.getDeclaredMethods()) {
                if (filter == null || filter.test(method)) {
                    methods.add(method);
                }
            }
        }
        return methods;
    }

    public static Method getGetter(@NonNull Class<?> clazz, String name) {
        if (isEmpty(name)) {
            return null;
        }
        name = normalized(name);
        try {
            val method = clazz.getMethod("get" + name);
            if (method != null) {
                return method;
            } else {
                return clazz.getMethod("is" + name);
            }
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static Method getGetter(@NonNull Class<?> clazz, String name, @NonNull Class<?> type) {
        try {
            return isEmpty(name)
                    ? null
                    : clazz.getMethod((type == boolean.class ? "is" : "get") + normalized(name));
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static Method getSetter(@NonNull Class<?> clazz, String name) {
        if (isEmpty(name)) {
            return null;
        }
        name = "set" + normalized(name);
        for (val method : clazz.getMethods()) {
            if (name.equals(method.getName()) && method.getParameterTypes().length == 1) {
                return method;
            }
        }
        return null;
    }

    public static Method getSetter(@NonNull Class<?> clazz, String name, @NonNull Class<?> type) {
        try {
            return isEmpty(name)
                    ? null
                    : clazz.getMethod("set" + normalized(name), type);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    @SneakyThrows(IllegalAccessException.class)
    public static Object invokeMethod(@NonNull Method method, Object target, Object... args) {
        makeAccessible(method);
        try {
            return method.invoke(target, args);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getTargetException());
        }
    }

    public static Object getProperty(@NonNull Object target, String name) {
        val getter = getGetter(target.getClass(), name);
        if (getter == null) {
            throw new RuntimeException("no such getter for : " + name);
        }
        return invokeMethod(getter, target);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getProperty(@NonNull Object target, String name, @NonNull Class<? extends T> type) {
        val getter = getGetter(target.getClass(), name, type);
        if (getter == null) {
            throw new RuntimeException("no such getter for : " + name);
        }
        return (T) invokeMethod(getter, target);
    }

    public static void setProperty(@NonNull Object target, String name, Object value) {
        val setter = getSetter(target.getClass(), name, value.getClass());
        if (setter == null) {
            throw new RuntimeException("no such setter for : " + name);
        }
        invokeMethod(setter, target, value);
    }

    public static <T> void setProperty(@NonNull Object target, String name, @NonNull Class<? super T> type, T value) {
        val setter = getSetter(target.getClass(), name, type);
        if (setter == null) {
            throw new RuntimeException("no such setter for : " + name);
        }
        invokeMethod(setter, target, value);
    }

    public static Object p(Object target, String name) {
        return getProperty(target, name);
    }

    public static void p(Object target, String name, Object value) {
        setProperty(target, name, value);
    }

    public static <T> void p(Object target, String name, Class<? super T> type, T value) {
        setProperty(target, name, type, value);
    }

    @Builder
    public static class Invocation {
        /**
         * Target of the invocation.
         */
        @NonNull
        private Object target;

        /**
         * Name of the method.
         */
        @NonNull
        private String name;

        /**
         * Types of method parameters.
         */
        @NonNull
        private Class<?>[] types;

        /**
         * Arguments for the method.
         */
        private Object[] arguments;

        /**
         * Invokes the method.
         *
         * @return the return value
         */
        @SneakyThrows({IllegalAccessException.class})
        public Object invoke() throws InvocationTargetException, NoSuchMethodException {
            val method = getMethod(target instanceof Class ? (Class<?>) target : target.getClass(), name, types);
            if (method == null) {
                throw new NoSuchMethodException(name);
            }
            makeAccessible(method);
            return method.invoke(target, arguments);
        }
    }
}
