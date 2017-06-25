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

package jclp.condition;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import jclp.function.Predicate;

@RequiredArgsConstructor
class CompareCondition<T extends Comparable<T>> implements Predicate<T> {
    private final T referred;

    @NonNull
    private final Type type;

    @Override
    public boolean test(T value) {
        val ret = referred == value ? 0 : referred.compareTo(value);
        switch (type) {
            case EQUAL:
                return ret == 0;
            case LESS_THAN:
                return ret > 0;
            case LESS_THAN_EQUAL:
                return ret >= 0;
            case GREATER_THAN:
                return ret < 0;
            default:
                return ret <= 0;
        }
    }

    public enum Type {
        EQUAL, LESS_THAN, LESS_THAN_EQUAL, GREATER_THAN, GREATER_THAN_EQUAL
    }
}
