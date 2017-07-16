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

import jclp.function.Provider;
import jclp.value.Lazy;
import jclp.value.Values;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;

import java.text.MessageFormat;
import java.util.*;

@RequiredArgsConstructor
public final class Linguist implements FallbackTranslator {
    @NonNull
    private final String path;
    private final Locale locale;
    private final ClassLoader loader;

    @Setter
    private List<? extends Translator> translators;

    public Linguist(String path) {
        this(path, null);
    }

    public Linguist(@NonNull String path, Locale locale) {
        this(path, locale, null);
    }

    private final Lazy<ResourceBundle> bundle = Values.lazy(new Provider<ResourceBundle>() {
        @Override
        public ResourceBundle provide() throws Exception {
            try {
                val l = locale != null ? locale : Locale.getDefault();
                return loader != null ? ResourceBundle.getBundle(path, l, loader) : ResourceBundle.getBundle(path, l);
            } catch (Exception e) {
                return EmptyBundle.EMPTY_BUNDLE;
            }
        }
    });

    private String getString(String key) {
        try {
            return bundle.get().getString(key);
        } catch (MissingResourceException e) {
            if (CollectionUtils.isNotEmpty(translators)) {
                for (val translator : translators) {
                    try {
                        return translator.tr(key);
                    } catch (MissingResourceException ignored) {
                    }
                }
            }
            throw e;
        }
    }

    @Override
    public String tr(String key) {
        return getString(key);
    }

    @Override
    public String optTr(String key, String fallback) {
        try {
            return StringUtils.coalesce(getString(key), fallback);
        } catch (MissingResourceException e) {
            return fallback;
        }
    }

    @Override
    public String tr(String key, Object... args) {
        return MessageFormat.format(tr(key), args);
    }

    @Override
    public String optTr(String key, String fallback, Object... args) {
        return MessageFormat.format(optTr(key, fallback), args);
    }

    private static class EmptyBundle extends ResourceBundle {
        private static final EmptyBundle EMPTY_BUNDLE = new EmptyBundle();

        @Override
        protected Object handleGetObject(String key) {
            return null;
        }

        @Override
        public Enumeration<String> getKeys() {
            return Collections.emptyEnumeration();
        }
    }
}
