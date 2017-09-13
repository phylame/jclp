package jclp.setting;

import jclp.io.IOUtils;
import jclp.io.Persistable;
import jclp.log.Log;
import jclp.text.ConverterManager;
import jclp.value.Pair;
import lombok.NonNull;
import lombok.ToString;
import lombok.val;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import static jclp.CollectionUtils.isEmpty;
import static jclp.CollectionUtils.isNotEmpty;
import static jclp.Validate.check;

@ToString(callSuper = true)
public class MapSettings extends AbstractSettings implements Persistable {
    private final Properties values = new Properties();

    public MapSettings() {
        this(null, null);
    }

    public MapSettings(Map<?, ?> values) {
        this(values, null);
    }

    public MapSettings(Map<?, ?> values, Map<String, Definition> definitions) {
        if (isNotEmpty(values)) {
            this.values.putAll(values);
        }
        if (isNotEmpty(definitions)) {
            for (val entry : definitions.entrySet()) {
                setDefinition(entry.getKey(), entry.getValue());
            }
        }
        initValues();
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
        check(value instanceof String, "value must be string: %s", value);
        return ConverterManager.parse((String) value, type);
    }

    @Override
    public Iterator<Pair<String, Object>> iterator() {
        return values.entrySet().stream()
                .map(e -> new Pair<>(e.getKey().toString(), e.getValue()))
                .iterator();
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
    public void load(@NonNull InputStream input) throws IOException {
        load(IOUtils.readerFor(input, "UTF-8"));
    }

    public void load(@NonNull Reader reader) throws IOException {
        values.load(reader);
        initValues();
    }

    @Override
    public void sync(@NonNull OutputStream output) throws IOException {
        sync(IOUtils.writerFor(output, "UTF-8"));
    }

    public void sync(@NonNull Writer writer) throws IOException {
        val props = (Properties) values.clone();
        for (val entry : props.entrySet()) {
            val value = entry.getValue();
            if (!(value instanceof CharSequence)) {
                entry.setValue(ConverterManager.render(value));
            }
        }
        props.store(writer, null);
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
            Object value;
            try {
                value = ConverterManager.parse(entry.getValue().toString(), definition.getType());
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), "cannot convert to type({0}): {1}", definition.getType(), entry.getValue());
                continue;
            }
            if (value == null) {
                Log.e(getClass().getSimpleName(), "invalid value({0}) for type({1})", entry.getValue(), definition.getType());
                continue;
            }
            entry.setValue(value);
        }
    }
}
