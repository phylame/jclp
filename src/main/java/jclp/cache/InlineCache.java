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

package jclp.cache;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

public class InlineCache implements Cacheable {
    private static final TextHolder EMPTY = new TextHolder("");

    @Override
    public Object add(@NonNull String text) {
        return text.isEmpty()
                ? EMPTY
                : new TextHolder(text);
    }

    @Override
    public String get(Object tag) {
        if (tag instanceof TextHolder) {
            return ((TextHolder) tag).text;
        }
        return null;
    }

    @Override
    public void close() throws IOException {
    }

    @RequiredArgsConstructor
    private static class TextHolder {
        private final String text;
    }
}
