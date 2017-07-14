package jclp.test;

import java.util.Map;

import jclp.value.Pair;


public interface Settings extends Iterable<Pair<String, ?>> {
    void setDefinitions(Map<String, Definition> definitions);

    boolean isEnable(String key);

    Object get(String key);

    <T> T get(String key, Class<T> type);

    boolean contains(String key);

    void set(String key, Object value);

    void update(Map<String, ?> values);

    void update(Settings settings);

    Object remove(String key);

    void clear();
}
