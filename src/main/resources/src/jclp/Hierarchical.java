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

package src.jclp;

import lombok.val;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public interface Hierarchical<T extends Hierarchical<T>> extends Iterable<T> {
    int size();

    T getParent();

    T get(int index);

    static <T extends Hierarchical<T>> int getDepth(T item) {
        if (item.size() == 0) {
            return 0;
        }
        int depth = 0;
        for (val stub : item) {
            depth = Math.max(depth, getDepth(stub));
        }
        return depth + 1;
    }

    static <T extends Hierarchical<T>> T locate(T item, int[] indices) {
        for (val index : indices) {
            item = item.get(index < 0 ? item.size() + index : index);
        }
        return item;
    }

    static <T extends Hierarchical<T>> T locate(T item, Collection<Integer> indices) {
        for (val index : indices) {
            item = item.get(index < 0 ? item.size() + index : index);
        }
        return item;
    }

    static <T extends Hierarchical<T>> T find(T item, Predicate<? super T> filter) {
        return find(item, 0, filter, false);
    }

    static <T extends Hierarchical<T>> T find(T item, int from, Predicate<? super T> filter, boolean recursion) {
        for (int i = from, end = item.size(); i != end; ++i) {
            T stub = item.get(i);
            if (filter.test(stub)) {
                return stub;
            }
            if (stub.size() > 0 && recursion) {
                stub = find(stub, 0, filter, true);
                if (stub != null) {
                    return stub;
                }
            }
        }
        return null;
    }

    static <T extends Hierarchical<T>> int select(T item, Predicate<? super T> filter, List<? super T> results) {
        return select(item, filter, results, -1, true);
    }

    static <T extends Hierarchical<T>> int select(T item, Predicate<? super T> filter, List<? super T> results, int limit, boolean recursion) {
        if (limit <= 0) {
            return 0;
        }
        int count = 0;
        for (val stub : item) {
            if (count++ == limit) {
                break;
            } else if (filter.test(stub)) {
                results.add(stub);
            }
            if (recursion && stub.size() > 0) {
                count += select(stub, filter, results, limit, true);
            }
        }
        return count;
    }
}
