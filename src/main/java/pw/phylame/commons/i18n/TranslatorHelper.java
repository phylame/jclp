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

package pw.phylame.commons.i18n;

import lombok.val;
import pw.phylame.commons.CollectionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.function.Predicate;

public class TranslatorHelper implements Translator {
    private final String path;
    private final Predicate<? super String> filter;
    private final Map<String, String> values;

    public TranslatorHelper(String name) {
        this(name, null);
    }

    public TranslatorHelper(String name, Predicate<? super String> filter) {
        this.path = name;
        this.filter = filter;
        values = new LinkedHashMap<>();
    }

    public void sync() throws IOException {
        val separator = System.lineSeparator();
        try (val writer = Files.newBufferedWriter(Paths.get(path))) {
            for (val e : values.entrySet()) {
                writer.append(e.getKey())
                        .append("=")
                        .append(e.getValue())
                        .append(separator);
            }
        }
    }

    @Override
    public String tr(String key) throws MissingResourceException {
        if (filter != null && !filter.test(key)) {
            throw new MissingResourceException(key, getClass().getSimpleName(), key);
        }
        return CollectionUtils.getOrPut(values, key, it -> it);
    }
}
