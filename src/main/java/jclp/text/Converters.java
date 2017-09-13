package jclp.text;

import jclp.IllegalImplementationError;
import lombok.NonNull;
import lombok.val;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Locale;

public final class Converters {
    private Converters() {
    }

    private static final IdentityHashMap<Class<?>, Converter<?>> converters = new IdentityHashMap<>();

    public static <T> void register(@NonNull Class<T> type, Converter<T> converter) {
        if (converter == null) {
            converters.remove(type);
        } else {
            converters.put(type, converter);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Converter<T> getConverter(@NonNull Class<T> type) {
        return (Converter<T>) converters.get(type);
    }

    public static boolean hasConverter(Class<?> type) {
        return converters.containsKey(type);
    }

    @SuppressWarnings("unchecked")
    public static <T> String render(@NonNull T obj) {
        return obj instanceof CharSequence ? obj.toString() : render(obj, (Class<T>) obj.getClass());
    }

    public static <T> String render(@NonNull T obj, @NonNull Class<T> type) {
        if (obj instanceof CharSequence || CharSequence.class.isAssignableFrom(type)) {
            return obj.toString();
        }
        String str = null;
        val converter = getConverter(type);
        if (converter != null) {
            str = converter.render(obj);
            if (str == null) {
                throw new IllegalImplementationError("Converter rendered null: " + converter);
            }
        }
        return str;
    }

    @SuppressWarnings("unchecked")
    public static <T> T parse(@NonNull String str, @NonNull Class<T> type) {
        if (type == String.class) {
            return (T) str;
        }
        T obj = null;
        val converter = getConverter(type);
        if (converter != null) {
            obj = converter.parse(str);
            if (obj == null) {
                throw new IllegalImplementationError("Converter parsed null: " + converter);
            }
        }
        return obj;
    }

    private static void registerDefaults() {
        register(Locale.class, new DefaultConverter<>(Locale.class));
        register(String.class, new DefaultConverter<>(String.class));
        register(Date.class, new DefaultConverter<>(Date.class));
        register(Byte.class, new DefaultConverter<>(Byte.class));
        register(Short.class, new DefaultConverter<>(Short.class));
        register(Integer.class, new DefaultConverter<>(Integer.class));
        register(Long.class, new DefaultConverter<>(Long.class));
        register(Float.class, new DefaultConverter<>(Float.class));
        register(Double.class, new DefaultConverter<>(Double.class));
        register(Boolean.class, new DefaultConverter<>(Boolean.class));
        register(LocalDate.class, new DefaultConverter<>(LocalDate.class));
        register(LocalTime.class, new DefaultConverter<>(LocalTime.class));
        register(LocalDateTime.class, new DefaultConverter<>(LocalDateTime.class));
        register(Class.class, new DefaultConverter<>(Class.class));
    }

    static {
        registerDefaults();
    }
}
