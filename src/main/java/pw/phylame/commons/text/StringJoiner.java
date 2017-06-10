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

import lombok.Builder;
import lombok.NonNull;
import lombok.val;
import pw.phylame.commons.util.StringUtils;

import java.util.Iterator;

@Builder
public class StringJoiner<T> {
    @NonNull
    private Iterator<T> iterator;

    @Builder.Default
    private CharSequence separator = ",";

    private Renderer<? super T> transform;

    private CharSequence prefix;
    private CharSequence suffix;

    public String join() {
        val b = new StringBuilder();
        if (StringUtils.isNotEmpty(prefix)) {
            b.append(prefix);
        }
        while (iterator.hasNext()) {
            b.append(transform != null ? transform.render(iterator.next()) : StringUtils.toString(iterator.next()));
            if (iterator.hasNext()) {
                b.append(separator);
            }
        }
        if (StringUtils.isNotEmpty(suffix)) {
            b.append(suffix);
        }
        return b.toString();
    }
}
