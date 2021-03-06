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

package jclp.text;

import jclp.function.Function;
import jclp.StringUtils;

public class StringTrimmer implements Render<String>, Function<String, String> {
    @Override
    public String render(String str) {
        return StringUtils.trimmed(str);
    }

    @Override
    public String apply(String str) {
        return render(str);
    }
}
