package jclp.setting;

import jclp.function.Function;
import jclp.io.IOUtils;
import jclp.text.Converters;
import jclp.util.Validate;
import jclp.value.Pair;
import lombok.NonNull;
import lombok.ToString;
import lombok.val;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import static jclp.util.CollectionUtils.isEmpty;
import static jclp.util.CollectionUtils.map;

@ToString(callSuper = true)
public class PropertiesSettings extends AbstractSettings implements PersistableSettings {
    private static final String ENCODING = "UTF-8";

    private final Properties values;

    public PropertiesSettings() {
        this(new Properties(), null);
    }

    public PropertiesSettings(@NonNull Properties values) {
        this(values, null);
    }

    public PropertiesSettings(@NonNull Properties values, Map<String, Definition> definitions) {
        super(definitions);
        this.values = values;
        initValues();
    }

    public PropertiesSettings(@NonNull File file) throws IOException {
        this(file, null);
    }

    public PropertiesSettings(@NonNull File file, Map<String, Definition> definitions) throws IOException {
        super(definitions);
        values = new Properties();
        try (val reader = IOUtils.readerFor(file)) {
            load(reader);
        }
    }

    public PropertiesSettings(@NonNull Reader reader) throws IOException {
        this(reader, null);
    }

    public PropertiesSettings(@NonNull Reader reader, Map<String, Definition> definitions) throws IOException {
        super(definitions);
        values = new Properties();
        load(reader);
    }

    @Override
    protected Object handleGet(String key) {
        return values.get(key);
    }

    @Override
    protected Object handleSet(String key, Object value) {
        return values.put(key, value);
    }

    @Override
    protected <T> T convertValue(Object value, Class<T> type) {
        Validate.check(value instanceof String, "cannot convert %s to %s", value, type);
        return Converters.parse(value.toString(), type);
    }

    @Override
    public Object remove(String key) {
        return values.remove(key);
    }

    @Override
    public void clear() {
        values.clear();
    }

    @Override
    public Iterator<Pair<String, ?>> iterator() {
        return map(values.entrySet().iterator(), new Function<Map.Entry<Object, Object>, Pair<String, ?>>() {
            @Override
            public Pair<String, ?> apply(Entry<Object, Object> entry) {
                return new Pair<>(entry.getKey().toString(), entry.getValue());
            }
        });
    }

    @Override
    public void load(@NonNull InputStream in) throws IOException {
        load(IOUtils.readerFor(in, ENCODING));
    }

    public void load(@NonNull Reader reader) throws IOException {
        values.load(reader);
        initValues();
    }

    @Override
    public void sync(@NonNull OutputStream out) throws IOException {
        sync(IOUtils.writerFor(out, ENCODING));
    }

    public void sync(@NonNull Writer writer) throws IOException {
        values.store(writer, null);
    }

    private void initValues() {
        if (isEmpty(definitions)) {
            return;
        }
        for (val entry : values.entrySet()) {
            val definition = definitions.get(entry.getKey().toString());
            if (definition == null) {
                continue;
            }
            val value = Converters.parse(entry.getValue().toString(), definition.getType());
            if (value == null) {
                throw new RuntimeException("cannot convert string " + entry.getValue() + " for type " + definition.getType());
            }
            entry.setValue(value);
        }
    }
}
