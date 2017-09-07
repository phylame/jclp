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

package jclp.i18n;

import jclp.CollectionUtils;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.*;

@RequiredArgsConstructor
public class Linguist implements AttachableTranslator {
    private final String name;
    private final Locale locale;
    private final ClassLoader loader;

    private final Set<Translator> attachments = new LinkedHashSet<>();

    public Linguist(String path) {
        this(path, Locale.getDefault(), getDefaultClassLoader());
    }

    public Linguist(String path, Locale locale) {
        this(path, locale, getDefaultClassLoader());
    }

    @Override
    public String tr(String key) throws MissingResourceException {
        try {
            return getBundle().getString(key);
        } catch (MissingResourceException e) {
            if (CollectionUtils.isNotEmpty(attachments)) {
                for (val translator : attachments) {
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
    public void attach(Translator... translators) {
        Collections.addAll(attachments, translators);
    }

    @Override
    public void detach(Translator... translators) {
        for (val translator : translators) {
            attachments.remove(translator);
        }
    }

    protected ResourceBundle getBundle() {
        try {
            return ResourceBundle.getBundle(name, locale, loader);
        } catch (MissingResourceException e) {
            return DummyBundle.INSTANCE;
        }
    }

    private static ClassLoader getDefaultClassLoader() {
        return Linguist.class.getClassLoader();
    }

    private static class DummyBundle extends ResourceBundle {
        private static final DummyBundle INSTANCE = new DummyBundle();

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
