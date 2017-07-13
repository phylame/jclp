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

package jclp.setting;

import jclp.function.EntryToPair;
import jclp.text.Converters;
import jclp.util.StringUtils;
import jclp.util.Validate;
import jclp.value.Pair;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static jclp.io.IOUtils.*;
import static jclp.util.CollectionUtils.map;

public class Settings {
    private static final String TAG = "Settings";
    private static final String ENCODING = "UTF-8";
    private static final String SETTINGS_EXTENSION = ".prf";
    private static final String DEPENDENCY_EXTENSION = ".dep";

    @Getter
    private final String path;

    @Getter
    @Setter
    private String comment = null;

    @Setter
    private String section = null;

    @Getter
    private boolean modified = false;

    private final Map<String, String> values = new HashMap<>();

    private final Set<SettingsListener> listeners = new HashSet<>();

    @Getter
    private final Dependencies dependencies = new Dependencies();

    public Settings(String path) throws IOException {
        this(path, true);
    }

    public Settings(String path, boolean loading) throws IOException {
        Validate.requireNotEmpty(path);
        if (!path.endsWith(SETTINGS_EXTENSION)) {
            path += SETTINGS_EXTENSION;
        }
        this.path = path;
        if (loading) {
            load();
        }
    }

    private String convkey(String key) {
        return StringUtils.isNotEmpty(section)
                ? section + key
                : key;
    }

    public boolean contains(String key) {
        return values.containsKey(convkey(key));
    }

    public String get(String key) {
        return getRaw(convkey(key));
    }

    public <T> T get(String key, @NonNull Class<T> type) {
        val value = getRaw(convkey(key));
        if (value == null) {
            return null;
        }
        return Converters.parse(value, type, true);
    }

    public Iterator<Pair<String, String>> getValues() {
        return map(values.entrySet().iterator(), new EntryToPair<String, String>());
    }

    public void set(@NonNull String key, @NonNull String value) {
        key = convkey(key);
        fireValueChanged(key, setRaw(key, value), value);
        modified = true;
    }

    public <T> void set(@NonNull String key, @NonNull T value, @NonNull Class<T> type) {
        key = convkey(key);
        if (value instanceof String) {
            fireValueChanged(key, setRaw(key, (String) value), value);
        } else {
            val str = Converters.render(value, type, true);
            if (str == null) {
                throw new IllegalArgumentException("Cannot convert " + value + " to string");
            }
            val prev = setRaw(key, str);
            fireValueChanged(key, Converters.parse(prev, type, true), value);
        }
        modified = true;
    }

    private String getRaw(String key) {
        return values.get(key);
    }

    private String setRaw(String key, String value) {
        return values.put(key, value);
    }

    public void update(Settings settings) {
        update(settings, false);
    }

    public void update(Settings settings, boolean clearing) {
        update(settings.values, clearing);
    }

    public void update(Map<String, String> values) {
        update(values, false);
    }

    public void update(Map<String, String> values, boolean clearing) {
        if (clearing) {
            clear();
        }
        for (val e : values.entrySet()) {
            set(e.getKey(), e.getValue());
        }
        modified = true;
    }

    public String remove(String key) {
        key = convkey(key);
        val value = values.remove(key);
        fireValueRemoved(key, value);
        modified = true;
        return value;
    }

    public void clear() {
        for (val e : values.entrySet()) {
            fireValueRemoved(e.getKey(), e.getValue());
        }
        values.clear();
        modified = true;
    }

    public final void addListener(SettingsListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public final void removeListener(SettingsListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    private void fireValueChanged(String key, Object oldValue, Object newValue) {
        for (val listener : listeners) {
            listener.valueChanged(key, oldValue, newValue);
        }
    }

    private void fireValueRemoved(String key, Object value) {
        for (val listener : listeners) {
            listener.valueRemove(key, value);
        }
    }

    public final void fireValueEnable(String key) {

    }

    public final boolean isEnable(String key) {
        return dependencies.isEnable(convkey(key), values);
    }

    /**
     * Loads values from settings file {@code path}.
     */
    public final void load() throws IOException {
        URL url = resourceFor(path);
        if (url == null) {
            return;
        }
        try (val r = readerFor(url, ENCODING)) {
            val props = new Properties();
            props.load(r);
            for (val e : props.entrySet()) {
                values.put(e.getKey().toString(), e.getValue().toString());
            }
        }
        url = resourceFor(path + DEPENDENCY_EXTENSION);
        if (url == null) {
            return;
        }
        try (val r = readerFor(url)) {
            dependencies.load(r);
        }
    }

    public final void sync() throws IOException {
        sync(false);
    }

    /**
     * Writes values to settings file {@code path}.
     *
     * @param forcing {@literal false} to write values if modified
     */
    public final void sync(boolean forcing) throws IOException {
        if (!modified && !forcing) {
            return;
        }
        Validate.check(!path.startsWith(CLASS_PATH_PREFIX), "cannot sync settings to file in class path: %s", path);
        val props = new Properties();
        props.putAll(values);
        try (val w = writerFor(new File(path), ENCODING)) {
            w.append("#Encoding: ").append(ENCODING).append(System.lineSeparator());
            props.store(w, comment);
        }
    }

    @Override
    public String toString() {
        return "Settings{path='" + path + '\'' + ", modified=" + modified + '}';
    }
}
