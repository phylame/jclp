package jclp.test;

import static jclp.util.CollectionUtils.isEmpty;

import java.util.Map;

import jclp.value.Values;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.val;


@ToString
public abstract class AbstractSettings implements Settings {
    @Getter
    @Setter
    private Map<String, Definition> definitions;

    protected abstract Object handleGet(String key);

    protected abstract <T> T convertValue(Object value, Class<T> type);

    @Override
    public final boolean isEnable(String key) {
        if (isEmpty(definitions)) {
            return true;
        }
        val definition = definitions.get(key);
        if (definition == null) {
            return true;
        }
        val dependencies = definition.getDependencies();
        if (isEmpty(dependencies)) {
            return true;
        }
        for (val dependency : dependencies) {
            if (!isEnable(dependency.getKey()) || !dependency.getCondition().test(get(dependency.getKey()))) {
                return false;
            }
        }
        return true;
    }

    protected final Object getDefault(String key) {
        if (isEmpty(definitions)) {
            return null;
        }
        val definition = definitions.get(key);
        return definition != null && definition.getDefaults() != null
                ? Values.get(definition.getDefaults())
                : null;
    }

    @Override
    public Object get(String key) {
        val value = handleGet(key);
        return value == null ? getDefault(key) : value;
    }

    @Override
    public <T> T get(String key, @NonNull Class<T> type) {
        val value = handleGet(key);
        if (value == null) {
            return type.cast(getDefault(key));
        } else if (type.isInstance(value)) {
            return type.cast(value);
        } else {
            return convertValue(value, type);
        }
    }

    @Override
    public void update(@NonNull Settings settings) {
        for (val p : settings) {
            set(p.getFirst(), p.getSecond());
        }
    }

    @Override
    public void update(@NonNull Map<String, ?> values) {
        for (val e : values.entrySet()) {
            set(e.getKey(), e.getValue());
        }
    }

    @Override
    public boolean contains(String key) {
        return handleGet(key) != null;
    }
}
