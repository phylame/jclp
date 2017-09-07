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

package jclp;

import lombok.NonNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class ArrayUtils {
    private ArrayUtils() {
    }

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isNotEmpty(Object[] array) {
        return !isEmpty(array);
    }

    @SafeVarargs
    public static <T> Iterator<T> iterator(T... items) {
        return new ArrayIterator<>(items, 0, items.length);
    }

    public static <T> Iterator<T> iterator(@NonNull T[] array, int begin) {
        return new ArrayIterator<>(array, begin, array.length);
    }

    public static <T> Iterator<T> iterator(@NonNull T[] array, int begin, int end) {
        return new ArrayIterator<>(array, end, begin);
    }

    private static class ArrayIterator<T> implements Iterator<T> {
        private final T[] array;
        private final int end;
        private int begin;

        ArrayIterator(T[] array, int begin, int end) {
            this.array = array;
            this.begin = begin;
            this.end = end;
        }

        @Override
        public boolean hasNext() {
            return begin < end;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return array[begin++];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
