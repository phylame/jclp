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

import lombok.Setter;

import java.util.MissingResourceException;

public class LocalizableWrapper implements Localizable {
    @Setter
    private Localizable localizable;

    @Override
    public String tr(String key) throws MissingResourceException {
        return localizable.tr(key);
    }

    @Override
    public String optTr(String key, String fallback) {
        return localizable.optTr(key, fallback);
    }

    @Override
    public String tr(String key, Object... args) throws MissingResourceException {
        return localizable.tr(key, args);
    }

    @Override
    public String optTr(String key, String fallback, Object... args) {
        return localizable.optTr(key, fallback, args);
    }
}
