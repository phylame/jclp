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

@Data
@RequiredArgsConstructor
public final class Triple<A, B, C> {
    private final A first;
    private final B second;
    private final C third;

    public <T> Triple<T, B, C> withFirst(T first) {
        return new Triple<>(first, second, third);
    }

    public <T> Triple<A, T, C> withSecond(T second) {
        return new Triple<>(first, second, third);
    }

    public <T> Triple<A, B, T> withThird(T third) {
        return new Triple<>(first, second, third);
    }
}
