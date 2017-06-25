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

import java.util.Collection;
import java.util.List;

public final class Conditions {
    private Conditions() {
    }

    public static Predicate<Object> isNull() {
        return Holder.IS_NULL;
    }

    public static Predicate<Object> isNotNull() {
        return Holder.IS_NOT_NULL;
    }

    public static <T> Predicate<T> inversed(Predicate<T> source) {
        return new InverseCondition<>(source);
    }

    public static <T> Predicate<T> contained(Collection<T> c) {
        return new ContainCondition<>(c);
    }

    public static <T> Predicate<T> combined(List<? extends Predicate<T>> conditions, ConditionGroup.Trigger trigger) {
        return new ConditionGroup<>(conditions, trigger);
    }

    public static Predicate<Object> equalTo(Object referred) {
        return new EqualCondition(referred);
    }

    public static <T extends Comparable<T>> Predicate<T> lessThan(T referred) {
        return new CompareCondition<>(referred, CompareCondition.Type.LESS_THAN);
    }

    public static <T extends Comparable<T>> Predicate<T> lessThanEqual(T referred) {
        return new CompareCondition<>(referred, CompareCondition.Type.LESS_THAN_EQUAL);
    }

    public static <T extends Comparable<T>> Predicate<T> greaterThan(T referred) {
        return new CompareCondition<>(referred, CompareCondition.Type.GREATER_THAN);
    }

    public static <T extends Comparable<T>> Predicate<T> greaterThanEqual(T referred) {
        return new CompareCondition<>(referred, CompareCondition.Type.GREATER_THAN_EQUAL);
    }

    private static class Holder {
        static final Predicate<Object> IS_NULL = new Predicate<Object>() {
            @Override
            public boolean test(Object value) {
                return value == null;
            }
        };

        static final Predicate<Object> IS_NOT_NULL = new Predicate<Object>() {
            @Override
            public boolean test(Object value) {
                return value != null;
            }
        };
    }
}
