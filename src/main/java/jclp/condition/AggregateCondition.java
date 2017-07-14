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

import jclp.function.Predicate;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.val;

import java.util.Collection;


@ToString
@RequiredArgsConstructor
class AggregateCondition<T> implements Predicate<T> {
    @NonNull
    private final Conditions.AggregateType type;
    @NonNull
    private final Collection<Predicate<? super T>> conditions;

    @Override
    public boolean test(T value) {
        if (type == Conditions.AggregateType.DISABLE || conditions.isEmpty()) {
            return true;
        }
        for (val condition : conditions) {
            val result = condition.test(value);
            switch (type) {
                case ALL:
                    if (!result) {
                        return false;
                    }
                    break;
                case ANY:
                    if (result) {
                        return true;
                    }
                    break;
                case NONE:
                    if (result) {
                        return false;
                    }
                    break;
                default:
                    break;
            }
        }
        switch (type) {
            case ALL:
                return true;
            case ANY:
                return false;
            case NONE:
                return true;
            default:
                return true;
        }
    }
}
