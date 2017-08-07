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

import java.util.List;
import java.util.Random;

import lombok.val;

public final class RandomUtils {
    private RandomUtils() {
    }

    private static final ThreadLocal<Random> random = new ThreadLocal<Random>() {
        protected Random initialValue() {
            return new Random();
        };
    };

    public static int randInteger() {
        val random = RandomUtils.random.get();
        random.setSeed(System.currentTimeMillis());
        return random.nextInt();
    }

    public static int randInteger(int bottom, int top) {
        Validate.require(top >= bottom, "top(%s) must >= bottom(%s)", top, bottom);
        val random = RandomUtils.random.get();
        random.setSeed(System.currentTimeMillis());
        return random.nextInt(top - bottom) + bottom;
    }

    public static <T> T anyOf(T[] items) {
        if (items == null || items.length == 0) {
            return null;
        }
        return items[randInteger(0, items.length)];
    }

    public static <T> T anyOf(List<T> items) {
        if (items == null || items.size() == 0) {
            return null;
        }
        return items.get(randInteger(0, items.size()));
    }
}
