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
import jclp.util.DateUtils;
import jclp.util.StringUtils;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;


public class Conditions {
    public enum CompareType {
        EQ, LT, GT, LE, GE
    }

    public enum AggregateType {
        ALL, ANY, NONE, DISABLE
    }

    @SuppressWarnings("unchecked")
    public static <T> Predicate<T> isNull() {
        return (Predicate<T>) Holder.IsNull;
    }

    @SuppressWarnings("unchecked")
    public static <T extends CharSequence> Predicate<T> isEmpty() {
        return (Predicate<T>) Holder.IsEmpty;
    }

    public static <T> Predicate<T> equalTo(T referred) {
        return new EqualCondition<>(referred);
    }

    public static <T> Predicate<T> withIn(Collection<T> referred) {
        return new WithInCondition<>(referred);
    }

    public static <T> Predicate<T> compareTo(T referred, @NonNull CompareType type, Comparator<? super T> comparator) {
        return new CompareCondition<T>(referred, type, comparator);
    }

    public static <T> Predicate<T> aggregate(AggregateType type, Collection<Predicate<? super T>> conditions) {
        return new AggregateCondition<T>(type, conditions);
    }

    public static <T> Predicate<T> negate(Predicate<T> condition) {
        return new NegateCondition<>(condition);
    }

    public static Predicate<String> forPattern(String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return null;
        }
        boolean negate = false;
        if (pattern.charAt(0) == '!') {
            pattern = pattern.substring(1);
            negate = true;
        }
        Predicate<String> condition;
        switch (pattern) {
            case "null":
                condition = isNull();
                break;
            case "empty":
                condition = isEmpty();
                break;
            default:
                condition = parseCondition(pattern);
                if (condition == null) {
                    return null;
                }
                break;
        }
        return negate ? negate(condition) : condition;
    }

    private static Predicate<String> parseCondition(String pattern) {
        int index = pattern.indexOf(":");
        if (index == -1) {
            return null;
        }
        val op = pattern.substring(0, index);
        switch (op) {
            case "eq": {
                pattern = pattern.substring(index + 1);
                return !pattern.isEmpty()
                        ? equalTo(pattern)
                        : null;
            }
            case "gt":
            case "ge":
            case "lt":
            case "le": {
                pattern = pattern.substring(index + 1);
                return !pattern.isEmpty()
                        ? compareTo(pattern, CompareType.valueOf(op.toUpperCase()), new StringComparator())
                        : null;
            }
            case "in":
                pattern = pattern.substring(index + 1);
                return !pattern.isEmpty()
                        ? withIn(Arrays.asList(pattern.split(",")))
                        : null;
            case "ag": {
                pattern = pattern.substring(index + 1);
                index = pattern.indexOf(",");
                if (index == -1) {
                    return null;
                }
                AggregateType type;
                try {
                    type = AggregateType.valueOf(pattern.substring(0, index).toUpperCase());
                } catch (IllegalArgumentException e) {
                    return null;
                }
                val conditions = new ArrayList<Predicate<? super String>>();
                for (val exp : pattern.substring(index + 1).split(";")) {
                    val condition = forPattern(exp);
                    if (condition != null) {
                        conditions.add(condition);
                    }
                }
                return aggregate(type, conditions);
            }
            default:
                throw new IllegalArgumentException("unsupported op: " + op);
        }
    }

    private static class StringComparator implements Comparator<String> {
        @Override
        @SneakyThrows(ParseException.class)
        public int compare(String o1, String o2) {
            if (o2 != null && o2.length() > 1) {
                val pt = o2.charAt(0);
                o2 = o2.substring(1);
                switch (pt) {
                    case 'i':
                        return Integer.compare(Integer.parseInt(o1), Integer.parseInt(o2));
                    case 'f':
                        return Double.compare(Double.parseDouble(o1), Double.parseDouble(o2));
                    case 'd':
                        return DateUtils.forISO(o1).compareTo(DateUtils.forISO(o2));
                }
                if (o2.startsWith("i")) {
                    return Integer.compare(Integer.parseInt(o1), Integer.parseInt(o2.substring(1)));
                }
            }
            return o1 != null
                    ? o1.compareTo(o2)
                    : -1;
        }
    }

    private static class Holder {
        private final static Predicate<?> IsNull = new Predicate<Object>() {
            @Override
            public boolean test(Object obj) {
                return obj == null;
            }

            @Override
            public String toString() {
                return "IsNull";
            }
        };

        private final static Predicate<?> IsEmpty = new Predicate<CharSequence>() {
            @Override
            public boolean test(CharSequence str) {
                return StringUtils.isEmpty(str);
            }

            @Override
            public String toString() {
                return "IsEmpty";
            }
        };
    }
}
