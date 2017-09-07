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

import jclp.DateUtils;
import jclp.MiscUtils;
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
        return parserFor(type) != null;
    }

    public static boolean hasRender(Class<?> type) {
        return renderFor(type) != null;
    }

    public static <T extends Enum<T>> void registerParser(@NonNull Class<T> type) {
        registerParser(type, new EnumParser<>(type));
    }

    public static <T> void registerConverter(@NonNull Class<T> type, Converter<T> converter) {
        registerParser(type, converter);
        registerRender(type, converter);
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

    @SuppressWarnings("unchecked")
    public static <T> Parser<T> parserFor(Class<T> type) {
        if (type == null) {
            return null;
        }
        val parser = parsers.get(type);
        return parser != null ? (Parser<T>) parser : null;
    }

    @SuppressWarnings("unchecked")
    public static <T> Render<T> renderFor(Class<T> type) {
        if (type == null) {
            return null;
        }
        val parser = renders.get(type);
        return parser != null ? (Render<T>) parser : null;
    }

    public static void registerDefaults() {
        registerConverter(Object.class, new DefaultConverter<>(Object.class));
        registerConverter(Locale.class, new DefaultConverter<>(Locale.class));
        registerConverter(String.class, new DefaultConverter<>(String.class));
        registerConverter(Date.class, new DefaultConverter<>(Date.class));
        val byteConverter = new DefaultConverter<Byte>(Byte.class);
        registerConverter(Byte.class, byteConverter);
        registerConverter(byte.class, byteConverter);
        val shortConverter = new DefaultConverter<Short>(Short.class);
        registerConverter(Short.class, shortConverter);
        registerConverter(short.class, shortConverter);
        val integerConverter = new DefaultConverter<Integer>(Integer.class);
        registerConverter(Integer.class, integerConverter);
        registerConverter(int.class, integerConverter);
        val longConverter = new DefaultConverter<Long>(Long.class);
        registerConverter(Long.class, longConverter);
        registerConverter(long.class, longConverter);
        val floatConverter = new DefaultConverter<Float>(Float.class);
        registerConverter(Float.class, floatConverter);
        registerConverter(float.class, floatConverter);
        val doubleConverter = new DefaultConverter<Double>(Double.class);
        registerConverter(Double.class, doubleConverter);
        registerConverter(double.class, doubleConverter);
        val booleanConverter = new DefaultConverter<Boolean>(Boolean.class);
        registerConverter(Boolean.class, booleanConverter);
        registerConverter(boolean.class, booleanConverter);
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
                return type.cast(DateUtils.parse(str, "yyyy-M-d H:m:s", "yyyy-M-d", "H:m:s"));
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
