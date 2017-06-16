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

package pw.phylame.commons.text;

import lombok.NonNull;
import lombok.val;

public final class Converters {
    public static <T> String render(T o) {
        return render(o, false);
    }

    @SuppressWarnings("unchecked")
    public static <T> String render(T o, boolean forwardingSuptype) {
        return o == null ? "null" : render(o, (Class<T>) o.getClass(), forwardingSuptype);
    }

    public static <T> String render(@NonNull T o, @NonNull Class<? super T> type) {
        return render(o, type, false);
    }

    public static <T> String render(@NonNull T o, @NonNull Class<? super T> type, boolean forwardingSuptype) {
        val render = ConverterManager.renderFor(type, forwardingSuptype);
        return render != null ? render.render(o) : null;
    }

    public static <T> T parse(@NonNull String str, @NonNull Class<T> type) {
        return parse(str, type, false);
    }

    public static <T> T parse(@NonNull String str, @NonNull Class<T> type, boolean forwardingSubtype) {
        val parser = ConverterManager.parserFor(type, forwardingSubtype);
        return parser != null ? parser.parse(str) : null;
    }
}
