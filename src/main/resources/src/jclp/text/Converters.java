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

package src.jclp.text;

import lombok.NonNull;
import lombok.val;

public final class Converters {
    private Converters() {
    }

    @SuppressWarnings("unchecked")
    public static <T> String render(T o) {
        if (o == null) {
            return null;
        } else if (o instanceof String) {
            return (String) o;
        } else {
            return render(o, (Class<T>) o.getClass());
        }
    }

    public static <T> String render(@NonNull T o, @NonNull Class<T> type) {
        if (o instanceof String && type == String.class) {
            return (String) o;
        } else {
            val render = ConverterManager.renderFor(type);
            return render != null ? render.render(o) : null;
        }
    }

    public static <T> T parse(@NonNull String str, @NonNull Class<T> type) {
        if (type == String.class) {
            return type.cast(str);
        } else {
            val parser = ConverterManager.parserFor(type);
            return parser != null ? parser.parse(str) : null;
        }
    }
}
