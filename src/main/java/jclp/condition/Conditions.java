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
import jclp.log.Log;
import jclp.text.Converters;
import jclp.util.DateUtils;
import jclp.util.StringUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

import java.text.ParseException;
import java.util.*;

public final class Conditions {
    private static final String TAG = "Conditions";

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

    public static Predicate<Object> forPattern(String pattern) {
        if (StringUtils.isEmpty(pattern)) {
            return null;
        }
        boolean negate = false;
        if (pattern.charAt(0) == '!') {
            pattern = pattern.substring(1);
            negate = true;
        }
        Predicate<Object> condition;
        switch (pattern) {
            case "null":
                condition = isNull();
                break;
            case "empty":
                condition = null;
                break;
            default:
                condition = parseCondition(pattern);
                break;
        }
        if (condition == null) {
            return null;
        }
        return negate ? negate(condition) : condition;
    }

    private static Predicate<Object> parseCondition(String pattern) {
        int index = pattern.indexOf(":");
        if (index == -1) {
            Log.d(TAG, "not found condition type: {0}", pattern);
            return null;
        }
        val type = pattern.substring(0, index);
        val data = pattern.substring(index + 1);
        if (data.isEmpty()) {
            Log.d(TAG, "not found value for condition: {0}", type);
            return null;
        }
        switch (type) {
            case "eq":
                try {
                    return equalTo(parseValue(data));
                } catch (ClassNotFoundException e) {
                    Log.d(TAG, "cannot parse referred value", e);
                    return null;
                }
            case "gt":
            case "ge":
            case "lt":
            case "le":
                Object value;
                try {
                    value = parseValue(data);
                } catch (ClassNotFoundException e) {
                    Log.d(TAG, "cannot parse referred value", e);
                    return null;
                }
                return compareTo(value, CompareType.valueOf(type.toUpperCase()), new GeneralComparator(value.getClass()));
            case "in":
                try {
                    return withIn(parseValues(data));
                } catch (ClassNotFoundException e) {
                    Log.d(TAG, "cannot parse referred value", e);
                    return null;
                }
            case "ag": {
                index = data.indexOf(",");
                if (index == -1) {
                    Log.d(TAG, "not found aggregate type: {0}", data);
                    return null;
                }
                AggregateType aggregateType;
                try {
                    aggregateType = AggregateType.valueOf(pattern.substring(0, index).toUpperCase());
                } catch (IllegalArgumentException e) {
                    Log.d(TAG, "unknown aggregate type", e);
                    return null;
                }
                val conditions = new ArrayList<Predicate<? super Object>>();
                for (val exp : data.substring(index + 1).split(";")) {
                    val condition = forPattern(exp);
                    if (condition != null) {
                        conditions.add(condition);
                    }
                }
                return aggregate(aggregateType, conditions);
            }
            default:
                Log.d(TAG, "unknown condition type: {0}", type);
                return null;
        }
    }

    private static Object parseValue(String str) throws ClassNotFoundException {
        val index = str.indexOf("$");
        return index != -1
                ? Converters.parse(str.substring(index + 1), parseType(str.substring(0, index)))
                : str;
    }

    private static List<Object> parseValues(String str) throws ClassNotFoundException {
        val values = new ArrayList<Object>();
        for (val s : str.split(",")) {
            values.add(parseValue(s));
        }
        return values;
    }

    private static Class<?> parseType(String str) throws ClassNotFoundException {
        switch (str) {
            case "":
            case "s":
                return String.class;
            case "i":
                return Integer.class;
            case "f":
                return Double.class;
            case "b":
                return Boolean.class;
            case "d":
                return Date.class;
            default:
                return Class.forName(str);
        }
    }

    @RequiredArgsConstructor
    private static class GeneralComparator implements Comparator<Object> {
        private final Class<?> type;

        @Override
        public int compare(Object a, Object b) {
            if (a == null) {
                return b != null ? -1 : 0;
            } else if (b == null) {
                return 1;
            }
            if (a.getClass() != b.getClass()) {
                throw new IllegalArgumentException("");
            }
            if (type == String.class) {
                return a.toString().compareTo(b.toString());
            } else if (type == Integer.class) {
                return Integer.compare((int) a, (int) b);
            } else if (type == Double.class) {
                return Double.compare((double) a, (double) b);
            }
            return 0;
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
