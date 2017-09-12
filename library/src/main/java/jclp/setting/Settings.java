package jclp.setting;

import jclp.value.Pair;
import jclp.value.Values;
import lombok.val;

import java.util.Map;

public interface Settings extends Iterable<Pair<String, Object>> {
    boolean isEnable(String key);

    Object get(String key);

    <T> T get(String key, Class<T> type);

    default <T> T get(String key, Class<T> type, T fallback) {
        T value;
        try {
            value = get(key, type);
        } catch (Exception e) {
            value = fallback;
        }
        return Values.get(value != null ? value : fallback);
    }

    default int getInt(String key, int fallback) {
        return get(key, Integer.class, fallback);
    }

    default double getDouble(String key, double fallback) {
        return get(key, Double.class, fallback);
    }

    default String getString(String key, String fallback) {
        return get(key, String.class, fallback);
    }

    default boolean getBoolean(String key, boolean fallback) {
        return get(key, Boolean.class, fallback);
    }

    boolean contains(String key);

    Object set(String key, Object value);

    default void update(Map<String, ?> values) {
        for (val entry : values.entrySet()) {
            set(entry.getKey(), entry.getValue());
        }
    }

    default void update(Settings settings) {
        for (val pair : settings) {
            set(pair.getFirst(), pair.getSecond());
        }
    }

    Object remove(String key);

    void clear();
}
