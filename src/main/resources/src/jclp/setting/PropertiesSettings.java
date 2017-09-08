/*
 * Copyright 2017 Peng Wan <phylame@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package src.jclp.setting;

import src.jclp.Validate;
import src.jclp.io.IOUtils;
import src.jclp.text.Converters;
import pw.phylame.commons.value.Pair;
import lombok.NonNull;
import lombok.ToString;
import lombok.val;
import src.jclp.CollectionUtils;

import java.io.*;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import static src.jclp.CollectionUtils.isEmpty;

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
    public Iterator<Pair<String, Object>> iterator() {
        return values.entrySet()
                .stream()
                .map(e -> new Pair<>(e.getKey().toString(), e.getValue()))
                .iterator();
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
        val props = (Properties) values.clone();
        for (val e : props.entrySet()) {
            e.setValue(Converters.render(e.getValue()));
        }
        props.store(writer, null);
    }

    private void initValues() {
        if (CollectionUtils.isEmpty(definitions)) {
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
