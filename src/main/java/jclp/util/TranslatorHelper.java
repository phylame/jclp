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

import jclp.function.Function;
import jclp.function.Predicate;
import jclp.io.IOUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.MissingResourceException;

@RequiredArgsConstructor
public class TranslatorHelper extends AbstractTranslator {
    @NonNull
    private final String name;

    @NonNull
    private final String[] locales;

    private final Predicate<? super String> filter;

    private final Map<String, String> strings = new LinkedHashMap<>();

    @Override
    public String tr(String key) throws MissingResourceException {
        if (filter != null && !filter.test(key)) {
            return key;
        }
        return CollectionUtils.getOrPut(strings, key, new Function<String, String>() {
            @Override
            public String apply(String arg) {
                return arg;
            }
        });
    }

    public void sync() throws IOException {
        val b = new StringBuilder();
        for (val locale : locales) {
            b.append(name);
            if (StringUtils.isNotEmpty(name)) {
                b.append("_").append(locale);
            }
            b.append(".properties");
            try (val out = IOUtils.writerFor(new File(b.toString()))) {
                for (val e : strings.entrySet()) {
                    out.append(e.getKey()).append("=").append(e.getValue()).append(System.lineSeparator());
                }
                out.flush();
            }
            b.setLength(0);
        }
    }
}
