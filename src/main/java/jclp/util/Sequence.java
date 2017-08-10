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

package jclp.util;

import jclp.function.BiFunction;
import jclp.function.Function;
import jclp.function.Predicate;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

@RequiredArgsConstructor
public class Sequence<E> {
    @Getter
    @NonNull
    private final Iterator<? extends E> iterator;

    public Sequence(Collection<? extends E> c) {
        this(c.iterator());
    }

    public Sequence(Enumeration<? extends E> e) {
        this(CollectionUtils.iterator(e));
    }

    public <T> Sequence<T> map(@NonNull Function<? super E, ? extends T> transform) {
        return new Sequence<>(CollectionUtils.map(iterator, transform));
    }

    public <T> Sequence<T> mapIndexed(@NonNull BiFunction<? super E, Integer, ? extends T> transform) {
        return new Sequence<>(CollectionUtils.mapIndexed(iterator, transform));
    }

    public Sequence<E> filter(@NonNull Predicate<? super E> filter) {
        return new Sequence<>(CollectionUtils.filter(iterator, filter));
    }

    public void attach(@NonNull Collection<? super E> target) {
        while (iterator.hasNext()) {
            target.add(iterator.next());
        }
    }

    public String join(String separator) {
        return StringUtils.join(separator, iterator);
    }
}
