package jclp.setting;

import jclp.util.Validate;
import jclp.value.Values;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.val;

import java.util.Map;

import static jclp.util.CollectionUtils.isEmpty;
import static jclp.util.CollectionUtils.isNotEmpty;

@ToString
public abstract class AbstractSettings implements Settings {
    @Setter
    protected Map<String, Definition> definitions;

    protected AbstractSettings(Map<String, Definition> definitions) {
        this.definitions = definitions;
    }

    protected abstract <T> T convertValue(Object value, Class<T> type);

    protected abstract Object handleSet(String key, Object value);

    protected abstract Object handleGet(String key);

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

    public final Definition getDefinition(String key) {
        return isNotEmpty(definitions) ? definitions.get(key) : null;
    }

    protected final Object getDefault(String key) {
        val definition = getDefinition(key);
        return definition != null ? Values.get(definition.getDefaults()) : null;
    }

    protected final Class<?> getType(String key) {
        val definition = getDefinition(key);
        return definition != null ? definition.getType() : null;
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
    public Object set(String key, @NonNull Object value) {
        val type = getType(key);
        Validate.require(type == null || type.isInstance(value), "instance of %s expected", type);
        return handleSet(key, value);
    }

    @Override
    public void update(@NonNull Settings settings) {
        for (val entry : settings) {
            set(entry.getFirst(), entry.getSecond());
        }
    }

    @Override
    public void update(@NonNull Map<String, ?> values) {
        for (val entry : values.entrySet()) {
            set(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public boolean contains(String key) {
        return handleGet(key) != null;
    }
}
