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

package pw.phylame.commons.util;

import lombok.NonNull;
import lombok.val;
import pw.phylame.commons.function.Provider;
import pw.phylame.commons.value.Lazy;

import java.text.MessageFormat;
import java.util.*;

public final class Linguist {
    private final String path;
    private final Locale locale;
    private final ClassLoader loader;

    public Linguist(String path) {
        this(path, null);
    }

    public Linguist(@NonNull String path, Locale locale) {
        this(path, locale, null);
    }

    public Linguist(String path, Locale locale, ClassLoader loader) {
        this.path = path;
        this.locale = locale;
        this.loader = loader;
    }

    private final Lazy<ResourceBundle> bundle = new Lazy<>(new Provider<ResourceBundle>() {
        @Override
        public ResourceBundle provide() throws Exception {
            val l = locale != null ? locale : Locale.getDefault();
            try {
                return loader != null ? ResourceBundle.getBundle(path, l, loader) : ResourceBundle.getBundle(path, l);
            } catch (MissingResourceException e) {
                return EmptyBundle.EMPTY_BUNDLE;
            }
        }
    });

    public ResourceBundle getBundle() {
        return bundle.get();
    }

    public String tr(@NonNull String key) {
        return getBundle().getString(key);
    }

    public String optTr(@NonNull String key, String fallback) {
        try {
            return StringUtils.coalesce(getBundle().getString(key), fallback);
        } catch (MissingResourceException e) {
            return fallback;
        }
    }

    public String tr(@NonNull String key, Object... args) {
        return MessageFormat.format(getBundle().getString(key), args);
    }

    public String optTr(@NonNull String key, String fallback, Object... args) {
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
