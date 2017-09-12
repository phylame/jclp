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

import jclp.Attachable;
import jclp.CollectionUtils;
import jclp.io.ResourceUtils;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
public class Linguist implements Translator, Attachable<Translator> {
    private final String name;
    private final Locale locale;
    private final ClassLoader loader;
    private final boolean dummy;

    private final LinkedHashSet<Translator> attachments = new LinkedHashSet<>();

    public Linguist(String path) {
        this(path, null, null, true);
    }

    public Linguist(String path, Locale locale) {
        this(path, locale, null, true);
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
    public void attach(Collection<? extends Translator> translators) {
        attachments.addAll(translators);
    }

    @Override
    public void detach(Collection<? extends Translator> translators) {
        attachments.removeAll(translators);
    }

    protected ResourceBundle getBundle() {
        try {
            return ResourceBundle.getBundle(name,
                    locale != null ? locale : Locale.getDefault(),
                    loader != null ? loader : ResourceUtils.getContextLoader(),
                    ResourceControl.INSTANCE);
        } catch (MissingResourceException e) {
            if (dummy) {
                return DummyBundle.INSTANCE;
            } else {
                throw e;
            }
        }
    }

    private static class ResourceControl extends ResourceBundle.Control {
        private static final ResourceControl INSTANCE = new ResourceControl();

        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
            if (format.equals("java.properties")) {
                val stream = ResourceUtils.openResource(toBundleName(baseName, locale) + ".properties", loader, reload);
                if (stream != null) {
                    return new PropertyResourceBundle(stream);
                }
            }
            return super.newBundle(baseName, locale, format, loader, reload);
        }
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
