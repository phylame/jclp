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

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static src.jclp.Validate.require;

public final class RandomUtils {
    private RandomUtils() {
    }

    public static int nextInt() {
        return ThreadLocalRandom.current().nextInt();
    }

    public static int nextInt(int bottom, int top) {
        require(top >= bottom, "top(%s) must >= bottom(%s)", top, bottom);
        return ThreadLocalRandom.current().nextInt(bottom, top - bottom);
    }

    public static <T> T choose(T[] items) {
        return ArrayUtils.isNotEmpty(items) ? items[nextInt(0, items.length)] : null;
    }

    public static <T> T choose(List<T> items) {
        return CollectionUtils.isNotEmpty(items) ? items.get(nextInt(0, items.size())) : null;
    }
}
