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

package jclp.util;

import jclp.text.Converters;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static jclp.io.IOUtils.*;

public class Settings {
    private static final String TAG = "Settings";
    private static final String ENCODING = "UTF-8";
    private static final String FILE_EXTENSION = ".pref";

    @Getter
    @Setter
    private String comment = null;

    @Getter
    private boolean modified = false;

    private final Map<String, String> values = new LinkedHashMap<>();

    @Getter
    private final String path;

    public Settings(String path) throws IOException {
        this(path, true);
    }

    public Settings(String path, boolean loading) throws IOException {
        Validate.requireNotEmpty(path);
        if (!path.endsWith(FILE_EXTENSION)) {
            path += FILE_EXTENSION;
        }
        this.path = path;
        if (loading) {
            load();
        }
    }

    public boolean contains(String key) {
        return values.containsKey(key);
    }

    public String get(String key) {
        return values.get(key);
    }

    public <T> T get(String key, @NonNull Class<T> type) {
        val value = values.get(key);
        if (value == null) {
            return null;
        }
        return Converters.parse(value, type, true);
    }

    public Set<Map.Entry<String, String>> getValues() {
        return values.entrySet();
    }

    public void set(@NonNull String key, @NonNull Object value) {
        if (value instanceof String) {
            values.put(key, value.toString());
        } else {
            val str = Converters.render(value, true);
            if (str == null) {
                throw new IllegalArgumentException("Cannot convert " + value + " to string");
            }
            values.put(key, str);
        }
        modified = true;
    }

    public <T> void set(@NonNull String key, @NonNull T value, @NonNull Class<T> type) {
        if (value instanceof String) {
            values.put(key, value.toString());
        } else {
            val str = Converters.render(value, type, true);
            if (str == null) {
                throw new IllegalArgumentException("Cannot convert " + value + " to string");
            }
            values.put(key, str);
        }
        modified = true;
    }

    /**
     * Resets all values to default.
     */
    public void reset() {
        modified = true;
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
        this.values.putAll(values);
        modified = true;
    }

    public String remove(String key) {
        val value = values.remove(key);
        modified = true;
        return value;
    }

    public void clear() {
        values.clear();
        modified = true;
    }

    /**
     * Loads values from settings file {@code path}.
     */
    public final void load() throws IOException {
        val url = resourceFor(path);
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
}
