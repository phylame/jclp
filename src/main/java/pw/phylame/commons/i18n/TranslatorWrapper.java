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

import lombok.Getter;
import lombok.Setter;

import java.text.MessageFormat;
import java.util.MissingResourceException;

import static pw.phylame.commons.Validate.check;
import static pw.phylame.commons.Validate.checkNotNull;

public class TranslatorWrapper implements AttachableTranslator {
    private static final String ERR_NO_TRANSLATOR = "translator is not initialized";
    private static final String ERR_NOT_ATTACHABLE = "translator is not attachable";

    @Getter
    @Setter
    private Translator translator;

    @Override
    public String tr(String key) throws MissingResourceException {
        checkNotNull(translator, ERR_NO_TRANSLATOR);
        return translator.tr(key);
    }

    @Override
    public String optTr(String key, String fallback) {
        return translator != null ? translator.optTr(key, fallback) : fallback;
    }

    @Override
    public String tr(String key, Object... args) throws MissingResourceException {
        checkNotNull(translator, ERR_NO_TRANSLATOR);
        return translator.tr(key, args);
    }

    @Override
    public String optTr(String key, String fallback, Object... args) {
        return translator != null ? translator.optTr(key, fallback, args) : MessageFormat.format(fallback, args);
    }

    @Override
    public void attach(Translator... translators) {
        checkNotNull(translator, ERR_NO_TRANSLATOR);
        check(translator instanceof AttachableTranslator, ERR_NOT_ATTACHABLE);
        ((AttachableTranslator) translator).attach(translators);
    }

    @Override
    public void detach(Translator... translators) {
        checkNotNull(translator, ERR_NO_TRANSLATOR);
        check(translator instanceof AttachableTranslator, ERR_NOT_ATTACHABLE);
        ((AttachableTranslator) translator).detach(translators);
    }
}
