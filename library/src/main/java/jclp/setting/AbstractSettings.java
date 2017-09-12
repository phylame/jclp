package jclp.setting;

import jclp.value.Values;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.val;

import java.util.HashMap;

import static jclp.CollectionUtils.isEmpty;
import static jclp.Validate.require;

@ToString
public abstract class AbstractSettings implements Settings {
    @Setter
    protected HashMap<String, Definition> definitions = new HashMap<>();

    protected abstract Object handleGet(String key);

    protected abstract Object handleSet(String key, Object value);

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

    public final void setDefinition(String key, Definition definition) {
        definitions.put(key, definition);
    }

    public final Definition getDefinition(String key) {
        return definitions.get(key);
    }

    protected final Object getDefault(String key) {
        val definition = definitions.get(key);
        return definition != null ? Values.get(definition.getDefaults()) : null;
    }

    protected final Class<?> getType(String key) {
        val definition = definitions.get(key);
        return definition != null ? definition.getType() : null;
    }

    @Override
    public Object get(String key) {
        val value = handleGet(key);
        return value == null ? getDefault(key) : value;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key, @NonNull Class<T> type) {
        val value = handleGet(key);
        if (value == null) {
            return (T) getDefault(key);
        } else if (type.isInstance(value)) {
            return (T) value;
        } else {
            return convertValue(value, type);
        }
    }

    @Override
    public Object set(String key, @NonNull Object value) {
        val type = getType(key);
        require(type == null || type.isInstance(value), "%s require %s", key, type);
        return handleSet(key, value);
    }

    @Override
    public boolean contains(String key) {
        return handleGet(key) != null;
    }
}
