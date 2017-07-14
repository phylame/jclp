package jclp.test;

import static jclp.util.CollectionUtils.map;

import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import jclp.function.Function;
import jclp.text.Converters;
import jclp.util.CollectionUtils;
import jclp.value.Pair;
import lombok.NonNull;
import lombok.ToString;
import lombok.val;


@ToString(callSuper = true)
public class PropertiesSettings extends AbstractSettings {
    private final Properties properties;

    public PropertiesSettings(@NonNull Properties properties) {
        this.properties = properties;
    }

    public PropertiesSettings(@NonNull Reader reader, Map<String, Definition> definitions) throws IOException {
        val prop = new Properties();
        prop.load(reader);
        setDefinitions(definitions);
        if (CollectionUtils.isNotEmpty(definitions)) {
            for (val entry : prop.entrySet()) {
                val definition = definitions.get(entry.getKey());
                if (definition != null) {
                    Object value;
                    try {
                        value = Converters.parse(entry.getValue().toString(), definition.getType());
                    } catch (RuntimeException e) {
                        throw new IllegalArgumentException("cannot convert string data", e);
                    }
                    if (value == null) {
                        throw new IllegalArgumentException("cannot convert string data");
                    }
                    entry.setValue(value);
                }
            }
        }
        properties = prop;
    }

    @Override
    public void set(String key, Object value) {
        properties.put(key, value);
    }

    @Override
    public Object remove(String key) {
        return properties.remove(key);
    }

    @Override
    public void clear() {
    }

    @Override
    public Iterator<Pair<String, ?>> iterator() {
        return map(properties.entrySet().iterator(), new Function<Map.Entry<Object, Object>, Pair<String, ?>>() {
            @Override
            public Pair<String, ?> apply(Entry<Object, Object> entry) {
                return new Pair<>(entry.getKey().toString(), entry.getValue());
            }
        });
    }

    @Override
    protected Object handleGet(String key) {
        return properties.get(key);
    }

    @Override
    protected <T> T convertValue(Object value, Class<T> type) {
        return Converters.parse(value.toString(), type, true);
    }
}
