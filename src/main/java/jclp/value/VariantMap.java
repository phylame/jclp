package jclp.value;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import static jclp.StringUtils.isEmpty;
import static jclp.Validate.requireNotEmpty;
import static jclp.Validate.requireNotNull;

public class VariantMap implements Iterable<Pair<String, Object>>, Cloneable {
    private HashMap<String, Object> values = new HashMap<>();

    private BiConsumer<String, Object> validator;

    public VariantMap() {
        this(null);
    }

    public VariantMap(BiConsumer<String, Object> validator) {
        this.validator = validator;
    }

    public Object set(String name, Object value) {
        requireNotEmpty(name, "name cannot be null or empty");
        requireNotNull(value, "value cannot be null");
        if (validator != null) {
            validator.accept(name, value);
        }
        return values.put(name, value);
    }

    public void update(@NonNull VariantMap others) {
        update(others.values);
    }

    public void update(@NonNull Map<String, Object> values) {
        for (val entry : values.entrySet()) {
            set(entry.getKey(), entry.getValue());
        }
    }

    public void update(@NonNull Iterator<? extends Map.Entry<String, Object>> it) {
        while (it.hasNext()) {
            val pair = it.next();
            set(pair.getKey(), pair.getValue());
        }
    }

    public boolean contains(String name) {
        return !isEmpty(name) && values.containsKey(name);
    }

    public Set<String> names() {
        return values.keySet();
    }

    public Object get(String name) {
        return isEmpty(name) ? null : values.get(name);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name, T fallback) {
        if (isEmpty(name)) {
            return fallback;
        }
        val value = values.get(name);
        return value != null ? (T) value : fallback;
    }

    public int size() {
        return values.size();
    }

    @Override
    public Iterator<Pair<String, Object>> iterator() {
        return values.entrySet().stream().map(e -> new Pair<>(e.getKey(), e.getValue())).iterator();
    }

    public Object remove(String name) {
        return isEmpty(name) ? null : values.remove(name);
    }

    public void clear() {
        values.clear();
    }

    @Override
    @SuppressWarnings("unchecked")
    @SneakyThrows(CloneNotSupportedException.class)
    public VariantMap clone() {
        val copy = (VariantMap) super.clone();
        copy.values = (HashMap<String, Object>) values.clone();
        return copy;
    }

    @Override
    public String toString() {
        return values.toString();
    }
}
