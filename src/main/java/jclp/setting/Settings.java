package jclp.setting;

import jclp.value.Pair;

import java.util.Map;

public interface Settings extends Iterable<Pair<String, ?>> {
    boolean isEnable(String key);

    Object get(String key);

    <T> T get(String key, Class<T> type);

    boolean contains(String key);

    Object set(String key, Object value);

    void update(Map<String, ?> values);

    void update(Settings settings);

    Object remove(String key);

    void clear();
}
