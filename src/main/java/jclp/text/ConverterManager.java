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

package jclp.text;

import jclp.util.DateUtils;
import jclp.util.MiscUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;

public final class ConverterManager {
    private ConverterManager() {
    }

    public static final String DISABLE_BUILTIN_CONVERTERS = "jclp.text.disableConverter";

    private static final Map<Class<?>, Parser<?>> parsers = new IdentityHashMap<>();

    private static final Map<Class<?>, Render<?>> renders = new IdentityHashMap<>();

    public static boolean hasParser(Class<?> type) {
        return hasParser(type, false);
    }

    public static boolean hasParser(Class<?> type, boolean forwarding) {
        if (type == null) {
            return false;
        }
        if (parsers.containsKey(type)) {
            return true;
        }
        if (forwarding) {
            for (val clazz : parsers.keySet()) {
                if (type.isAssignableFrom(clazz)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasRender(Class<?> type) {
        return hasRender(type, false);
    }

    public static boolean hasRender(Class<?> type, boolean forwarding) {
        if (type == null) {
            return false;
        }
        if (renders.containsKey(type)) {
            return true;
        }
        if (forwarding) {
            for (val clazz : renders.keySet()) {
                if (clazz.isAssignableFrom(type)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static <T> void registerParser(@NonNull Class<T> type, Parser<? extends T> parser) {
        if (parser == null) {
            parsers.remove(type);
        } else {
            parsers.put(type, parser);
        }
    }

    public static <T> void registerRender(@NonNull Class<T> type, Render<? super T> render) {
        if (render == null) {
            renders.remove(type);
        } else {
            renders.put(type, render);
        }
    }

    public static <T> void registerConverter(@NonNull Class<T> type, Converter<T> converter) {
        registerParser(type, converter);
        registerRender(type, converter);
    }

    public static <T> Parser<T> parserFor(Class<T> type) {
        return parserFor(type, false);
    }

    @SuppressWarnings("unchecked")
    public static <T> Parser<T> parserFor(Class<T> type, boolean forwarding) {
        if (type == null) {
            return null;
        }
        val parser = parsers.get(type);
        if (parser != null) {
            return (Parser<T>) parser;
        }
        if (forwarding) {
            for (val e : parsers.entrySet()) {
                if (type.isAssignableFrom(e.getKey())) {
                    return (Parser<T>) e.getValue();
                }
            }
        }
        return null;
    }

    public static <T> Render<T> renderFor(Class<T> type) {
        return renderFor(type, false);
    }

    @SuppressWarnings("unchecked")
    public static <T> Render<T> renderFor(Class<T> type, boolean forwarding) {
        if (type == null) {
            return null;
        }
        val parser = renders.get(type);
        if (parser != null) {
            return (Render<T>) parser;
        }
        if (forwarding) {
            for (val e : renders.entrySet()) {
                if (e.getKey().isAssignableFrom(type)) {
                    return (Render<T>) e.getValue();
                }
            }
        }
        return null;
    }

    public static void registerDefaults() {
        registerConverter(Object.class, new DefaultConverter<>(Object.class));
        registerConverter(Locale.class, new DefaultConverter<>(Locale.class));
        registerParser(String.class, new DefaultConverter<>(String.class));
        registerConverter(Date.class, new DefaultConverter<>(Date.class));
        DefaultConverter<Byte> bc = new DefaultConverter<>(Byte.class);
        registerParser(Byte.class, bc);
        registerConverter(byte.class, bc);
        DefaultConverter<Short> sc = new DefaultConverter<>(Short.class);
        registerParser(Short.class, sc);
        registerConverter(short.class, sc);
        DefaultConverter<Integer> ic = new DefaultConverter<>(Integer.class);
        registerParser(Integer.class, ic);
        registerConverter(int.class, ic);
        DefaultConverter<Long> lc = new DefaultConverter<>(Long.class);
        registerParser(Long.class, lc);
        registerConverter(long.class, lc);
        DefaultConverter<Float> fc = new DefaultConverter<>(Float.class);
        registerParser(Float.class, fc);
        registerConverter(float.class, fc);
        DefaultConverter<Double> dc = new DefaultConverter<>(Double.class);
        registerParser(Double.class, dc);
        registerConverter(double.class, dc);
        DefaultConverter<Boolean> blc = new DefaultConverter<>(Boolean.class);
        registerParser(Boolean.class, blc);
        registerConverter(boolean.class, blc);
    }

    static {
        if (!Boolean.getBoolean(DISABLE_BUILTIN_CONVERTERS)) {
            registerDefaults();
        }
    }

    @RequiredArgsConstructor
    private static class DefaultConverter<T> implements Converter<T> {
        private final Class<T> type;

        @Override
        public T parse(@NonNull String str) {
            if (type == String.class) {
                return type.cast(str);
            } else if (type == Byte.class) {
                return type.cast(Byte.decode(str));
            } else if (type == Short.class) {
                return type.cast(Short.decode(str));
            } else if (type == Integer.class) {
                return type.cast(Integer.decode(str));
            } else if (type == Long.class) {
                return type.cast(Long.decode(str));
            } else if (type == Float.class) {
                return type.cast(Float.valueOf(str));
            } else if (type == Double.class) {
                return type.cast(Double.valueOf(str));
            } else if (type == Boolean.class) {
                return type.cast(Boolean.valueOf(str));
            } else if (type == Date.class) {
                return type.cast(DateUtils.parse(str, DateUtils.ISO_FORMAT, null));
            } else if (type == Locale.class) {
                return type.cast(MiscUtils.parseLocale(str));
            } else {
                throw new IllegalArgumentException("Unsupported type: " + type);
            }
        }

        @Override
        public String render(@NonNull T obj) {
            if (type == Date.class) {
                return DateUtils.toISO((Date) obj);
            } else if (type == Locale.class) {
                return MiscUtils.renderLocale((Locale) obj);
            } else {
                return obj.toString();
            }
        }
    }
}
