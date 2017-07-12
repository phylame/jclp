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

package jclp.cond;

import jclp.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Objects;


@ToString
@RequiredArgsConstructor
class EqualCondition<T> implements Predicate<T> {
    private final T referred;

    @Override
    public boolean test(T value) {
        return Objects.equals(value, referred);
    }
}
