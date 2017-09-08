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

package pw.phylame.commons;

import static pw.phylame.commons.StringUtils.isNotEmpty;

/**
 * Utilities for validation.
 */
public final class Validate {
    private static final String NULL_NOTE = "object cannot be null";

    private static final String EMPTY_NOTE = "string cannot be null or empty";

    private Validate() {
    }

    public static void require(boolean condition, String msg) {
        if (!condition) {
            throw new IllegalArgumentException(msg);
        }
    }

    public static void require(boolean condition, String msg, Object arg1) {
        if (!condition) {
            throw new IllegalArgumentException(String.format(msg, arg1));
        }
    }

    public static void require(boolean condition, String msg, Object arg1, Object arg2) {
        if (!condition) {
            throw new IllegalArgumentException(String.format(msg, arg1, arg2));
        }
    }

    public static void require(boolean condition, String msg, Object... args) {
        if (!condition) {
            throw new IllegalArgumentException(String.format(msg, args));
        }
    }

    public static void requireNotNull(Object obj) {
        require(obj != null, NULL_NOTE);
    }

    public static void requireNotNull(Object obj, String msg) {
        require(obj != null, msg);
    }

    public static void requireNotNull(Object obj, String msg, Object arg1) {
        require(obj != null, msg, arg1);
    }

    public static void requireNotNull(Object obj, String msg, Object arg1, Object arg2) {
        require(obj != null, msg, arg1, arg2);
    }

    public static void requireNotNull(Object obj, String msg, Object... args) {
        require(obj != null, msg, args);
    }

    public static <T extends CharSequence> void requireNotEmpty(T str) {
        require(isNotEmpty(str), EMPTY_NOTE);
    }

    public static <T extends CharSequence> void requireNotEmpty(T str, String msg) {
        require(isNotEmpty(str), msg);
    }

    public static <T extends CharSequence> void requireNotEmpty(T str, String msg, Object arg1) {
        require(isNotEmpty(str), msg, arg1);
    }

    public static <T extends CharSequence> void requireNotEmpty(T str, String msg, Object arg1, Object arg2) {
        require(isNotEmpty(str), msg, arg1, arg2);
    }

    public static <T extends CharSequence> void requireNotEmpty(T str, String msg, Object... args) {
        require(isNotEmpty(str), msg, args);
    }

    public static void check(boolean condition, String msg) {
        if (!condition) {
            throw new IllegalStateException(msg);
        }
    }

    public static void check(boolean condition, String msg, Object arg1) {
        if (!condition) {
            throw new IllegalStateException(String.format(msg, arg1));
        }
    }

    public static void check(boolean condition, String msg, Object arg1, Object arg2) {
        if (!condition) {
            throw new IllegalStateException(String.format(msg, arg1, arg2));
        }
    }

    public static void check(boolean condition, String msg, Object... args) {
        if (!condition) {
            throw new IllegalStateException(String.format(msg, args));
        }
    }

    public static void checkNotNull(Object o) {
        check(o != null, NULL_NOTE);
    }

    public static void checkNotNull(Object o, String msg) {
        check(o != null, msg);
    }

    public static void checkNotNull(Object o, String msg, Object arg1) {
        check(o != null, msg, arg1);
    }

    public static void checkNotNull(Object o, String msg, Object arg1, Object arg2) {
        check(o != null, msg, arg1, arg2);
    }

    public static void checkNotNull(Object o, String msg, Object... args) {
        check(o != null, msg, args);
    }

    public static void checkNotEmpty(CharSequence str) {
        check(isNotEmpty(str), EMPTY_NOTE);
    }

    public static void checkNotEmpty(CharSequence str, String msg) {
        check(isNotEmpty(str), msg);
    }

    public static void checkNotEmpty(CharSequence str, String msg, Object arg1) {
        check(isNotEmpty(str), msg, arg1);
    }

    public static void checkNotEmpty(CharSequence str, String msg, Object arg1, Object arg2) {
        check(isNotEmpty(str), msg, arg1, arg2);
    }

    public static void checkNotEmpty(CharSequence str, String msg, Object... args) {
        check(isNotEmpty(str), msg, args);
    }
}
