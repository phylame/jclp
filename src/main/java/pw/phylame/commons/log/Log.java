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

package pw.phylame.commons.log;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import static pw.phylame.commons.log.LogLevel.*;

public final class Log {
    private Log() {
    }

    @Getter
    @Setter
    @NonNull
    private static LogLevel level = DEFAULT;

    @Getter
    @Setter
    @NonNull
    private static LogFacade facade = new SimpleFacade();

    public static boolean isEnable(LogLevel level) {
        return level.getCode() <= Log.level.getCode();
    }

    public static void t(String tag, String msg) {
        if (isEnable(TRACE))
            facade.log(tag, LogLevel.TRACE, msg);
    }

    public static void t(String tag, String format, Object arg1) {
        if (isEnable(TRACE))
            facade.log(tag, LogLevel.TRACE, format, arg1);
    }

    public static void t(String tag, String format, Object arg1, Object arg2) {
        if (isEnable(TRACE))
            facade.log(tag, LogLevel.TRACE, format, arg1, arg2);
    }

    public static void t(String tag, String format, Object... args) {
        if (isEnable(TRACE))
            facade.log(tag, LogLevel.TRACE, format, args);
    }

    public static void t(String tag, String msg, Throwable t) {
        if (isEnable(TRACE))
            facade.log(tag, LogLevel.TRACE, msg, t);
    }

    public static void d(String tag, String msg) {
        if (isEnable(DEBUG))
            facade.log(tag, LogLevel.DEBUG, msg);
    }

    public static void d(String tag, String format, Object arg1) {
        if (isEnable(DEBUG))
            facade.log(tag, LogLevel.DEBUG, format, arg1);
    }

    public static void d(String tag, String format, Object arg1, Object arg2) {
        if (isEnable(DEBUG))
            facade.log(tag, LogLevel.DEBUG, format, arg1, arg2);
    }

    public static void d(String tag, String format, Object... args) {
        if (isEnable(DEBUG))
            facade.log(tag, LogLevel.DEBUG, format, args);
    }

    public static void d(String tag, String msg, Throwable t) {
        if (isEnable(DEBUG))
            facade.log(tag, LogLevel.DEBUG, msg, t);
    }

    public static void i(String tag, String msg) {
        if (isEnable(INFO))
            facade.log(tag, LogLevel.INFO, msg);
    }

    public static void i(String tag, String format, Object arg1) {
        if (isEnable(INFO))
            facade.log(tag, LogLevel.INFO, format, arg1);
    }

    public static void i(String tag, String format, Object arg1, Object arg2) {
        if (isEnable(INFO))
            facade.log(tag, LogLevel.INFO, format, arg1, arg2);
    }

    public static void i(String tag, String format, Object... args) {
        if (isEnable(INFO))
            facade.log(tag, LogLevel.INFO, format, args);
    }

    public static void i(String tag, String msg, Throwable t) {
        if (isEnable(INFO))
            facade.log(tag, LogLevel.INFO, msg, t);
    }

    public static void w(String tag, String msg) {
        if (isEnable(WARN))
            facade.log(tag, LogLevel.WARN, msg);
    }

    public static void w(String tag, String format, Object arg1) {
        if (isEnable(WARN))
            facade.log(tag, LogLevel.WARN, format, arg1);
    }

    public static void w(String tag, String format, Object arg1, Object arg2) {
        if (isEnable(WARN))
            facade.log(tag, LogLevel.WARN, format, arg1, arg2);
    }

    public static void w(String tag, String format, Object... args) {
        if (isEnable(WARN))
            facade.log(tag, LogLevel.WARN, format, args);
    }

    public static void wt(String tag, String msg, Throwable t) {
        if (isEnable(WARN))
            facade.log(tag, LogLevel.WARN, msg, t);
    }

    public static void e(String tag, String msg) {
        if (isEnable(ERROR))
            facade.log(tag, LogLevel.ERROR, msg);
    }

    public static void e(String tag, String format, Object arg1) {
        if (isEnable(ERROR))
            facade.log(tag, LogLevel.ERROR, format, arg1);
    }

    public static void e(String tag, String format, Object arg1, Object arg2) {
        if (isEnable(ERROR))
            facade.log(tag, LogLevel.ERROR, format, arg1, arg2);
    }

    public static void e(String tag, String format, Object... args) {
        if (isEnable(ERROR))
            facade.log(tag, LogLevel.ERROR, format, args);
    }

    public static void e(String tag, String msg, Throwable t) {
        if (isEnable(ERROR))
            facade.log(tag, LogLevel.ERROR, msg, t);
    }

    public static void f(String tag, String msg) {
        if (isEnable(FATAL))
            facade.log(tag, LogLevel.FATAL, msg);
    }

    public static void f(String tag, String format, Object arg1) {
        if (isEnable(FATAL))
            facade.log(tag, LogLevel.FATAL, format, arg1);
    }

    public static void f(String tag, String format, Object arg1, Object arg2) {
        if (isEnable(FATAL))
            facade.log(tag, LogLevel.FATAL, format, arg1, arg2);
    }

    public static void f(String tag, String format, Object... args) {
        if (isEnable(FATAL))
            facade.log(tag, LogLevel.FATAL, format, args);
    }

    public static void f(String tag, String msg, Throwable t) {
        if (isEnable(FATAL))
            facade.log(tag, LogLevel.FATAL, msg, t);
    }
}
