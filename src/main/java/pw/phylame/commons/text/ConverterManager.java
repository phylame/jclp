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

package pw.phylame.commons.text;

import lombok.NonNull;
import lombok.val;
import pw.phylame.commons.util.MiscUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Locale;
import java.util.Map;

public final class ConverterManager {
    private ConverterManager() {
    }

    public static final String DISABLE_BUILTIN_CONVERTERS = "commons.converter.disableBuiltins";

    private static final Map<Class<?>, Parser<?>> parsers = new IdentityHashMap<>();

    private static final Map<Class<?>, Render<?>> renders = new IdentityHashMap<>();

    public static boolean hasParser(Class<?> type) {
        return hasParser(type, false);
    }

    public static boolean hasParser(Class<?> type, boolean forwardingSubtype) {
        if (type == null) {
            return false;
        }
        if (parsers.containsKey(type)) {
            return true;
        }
        if (forwardingSubtype) {
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

    public static boolean hasRender(Class<?> type, boolean forwardingSuptype) {
        if (type == null) {
            return false;
        }
        if (renders.containsKey(type)) {
            return true;
        }
        if (forwardingSuptype) {
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
    public static <T> Parser<T> parserFor(Class<T> type, boolean forwardingSubtype) {
        if (type == null) {
            return null;
        }
        val parser = parsers.get(type);
        if (parser != null) {
            return (Parser<T>) parser;
        }
        if (forwardingSubtype) {
            for (val item : parsers.entrySet()) {
                if (type.isAssignableFrom(item.getKey())) {
                    return (Parser<T>) item.getValue();
                }
            }
        }
        return null;
    }

    public static <T> Render<T> renderFor(Class<T> type) {
        return renderFor(type, false);
    }

    @SuppressWarnings("unchecked")
    public static <T> Render<T> renderFor(Class<T> type, boolean forwardingSuptype) {
        if (type == null) {
            return null;
        }
        val parser = renders.get(type);
        if (parser != null) {
            return (Render<T>) parser;
        }
        if (forwardingSuptype) {
            for (val item : renders.entrySet()) {
                if (item.getKey().isAssignableFrom(type)) {
                    return (Render<T>) item.getValue();
                }
            }
        }
        return null;
    }

    public static void registerBuiltinConverters() {
        val toStringRender = new Render<Object>() {
            @Override
            public String render(Object obj) {
                return String.valueOf(obj);
            }
        };
        registerRender(Object.class, toStringRender);
        registerParser(Byte.class, new Parser<Byte>() {
            @Override
            public Byte parse(String str) {
                return Byte.valueOf(str);
            }
        });
        registerParser(Short.class, new Parser<Short>() {
            @Override
            public Short parse(String str) {
                return Short.valueOf(str);
            }
        });
        registerParser(Integer.class, new Parser<Integer>() {
            @Override
            public Integer parse(String str) {
                return Integer.valueOf(str);
            }
        });
        registerParser(Long.class, new Parser<Long>() {
            @Override
            public Long parse(String str) {
                return Long.valueOf(str);
            }
        });
        registerParser(Float.class, new Parser<Float>() {
            @Override
            public Float parse(String str) {
                return Float.valueOf(str);
            }
        });
        registerParser(Double.class, new Parser<Double>() {
            @Override
            public Double parse(String str) {
                return Double.valueOf(str);
            }
        });
        registerParser(Boolean.class, new Parser<Boolean>() {
            @Override
            public Boolean parse(String str) {
                return Boolean.valueOf(str);
            }
        });
        val dateConverter = new Converter<Date>() {
            @Override
            public String render(Date date) {
                return DateFormat.getDateTimeInstance().format(date);
            }

            @Override
            public Date parse(String str) {
                try {
                    return DateFormat.getDateTimeInstance().parse(str);
                } catch (ParseException e) {
                    return null;
                }
            }
        };
        registerRender(Date.class, dateConverter);
        registerParser(Date.class, dateConverter);
        val localeConverter = new Converter<Locale>() {
            @Override
            public String render(Locale locale) {
                return MiscUtils.renderLocale(locale);
            }

            @Override
            public Locale parse(String str) {
                return MiscUtils.parseLocale(str);
            }
        };
        registerRender(Locale.class, localeConverter);
        registerParser(Locale.class, localeConverter);
    }

    static {
        if (!Boolean.getBoolean(DISABLE_BUILTIN_CONVERTERS)) {
            registerBuiltinConverters();
        }
    }
}
