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

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Map;

public final class ConverterManager {
    public static final String DISABLE_BUILTIN_CONVERTERS = "commons.converter.disableBuiltins";

    private static final Map<Class<?>, Parser<?>> parsers = new IdentityHashMap<>();

    private static final Map<Class<?>, Renderer<?>> renderers = new IdentityHashMap<>();

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

    public static boolean hasRenderer(Class<?> type) {
        return hasRenderer(type, false);
    }

    public static boolean hasRenderer(Class<?> type, boolean forwardingSuptype) {
        if (type == null) {
            return false;
        }
        if (renderers.containsKey(type)) {
            return true;
        }
        if (forwardingSuptype) {
            for (val clazz : renderers.keySet()) {
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

    public static <T> void registerRenderer(@NonNull Class<T> type, Renderer<? super T> renderer) {
        if (renderer == null) {
            renderers.remove(type);
        } else {
            renderers.put(type, renderer);
        }
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

    public static <T> Renderer<T> rendererFor(Class<T> type) {
        return rendererFor(type, false);
    }

    @SuppressWarnings("unchecked")
    public static <T> Renderer<T> rendererFor(Class<T> type, boolean forwardingSuptype) {
        if (type == null) {
            return null;
        }
        val parser = renderers.get(type);
        if (parser != null) {
            return (Renderer<T>) parser;
        }
        if (forwardingSuptype) {
            for (val item : renderers.entrySet()) {
                if (item.getKey().isAssignableFrom(type)) {
                    return (Renderer<T>) item.getValue();
                }
            }
        }
        return null;
    }

    public static void registerBuiltinConverters() {
        val toStringRenderer = new Renderer<Object>() {
            @Override
            public String render(Object obj) {
                return String.valueOf(obj);
            }
        };
        registerRenderer(Object.class, toStringRenderer);
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
        registerRenderer(Date.class, dateConverter);
        registerParser(Date.class, dateConverter);
    }

    static {
        if (!Boolean.getBoolean(DISABLE_BUILTIN_CONVERTERS)) {
            registerBuiltinConverters();
        }
    }
}
