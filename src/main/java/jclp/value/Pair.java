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

package jclp.value;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Data
@RequiredArgsConstructor
public final class Pair<A, B> implements Map.Entry<A, B> {
    private final A first;
    private final B second;

    public Pair<A, B> withFirst(A first) {
        return new Pair<>(first, second);
    }

    public Pair<A, B> withSecond(B second) {
        return new Pair<>(first, second);
    }

    @Override
    public A getKey() {
        return first;
    }

    @Override
    public B getValue() {
        return second;
    }

    @Override
    public B setValue(B value) {
        throw new UnsupportedOperationException();
    }
}
