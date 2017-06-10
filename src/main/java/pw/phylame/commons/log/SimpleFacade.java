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

import java.text.MessageFormat;

public class SimpleFacade implements LogFacade {
    @Override
    public void log(String tag, LogLevel level, String msg) {
        print(level, format(tag, level, msg));
    }

    @Override
    public void log(String tag, LogLevel level, String format, Object arg1) {
        print(level, format(tag, level, format, arg1));
    }

    @Override
    public void log(String tag, LogLevel level, String format, Object arg1, Object arg2) {
        print(level, format(tag, level, format, arg1, arg2));
    }

    @Override
    public void log(String tag, LogLevel level, String format, Object... args) {
        print(level, format(tag, level, format, args));
    }

    @Override
    public void log(String tag, LogLevel level, String msg, Throwable t) {
        print(level, format(tag, level, msg + t));
    }

    private void print(LogLevel level, String msg) {
        if (level.getCode() < LogLevel.WARN.getCode()) {
            System.out.println(msg);
        } else {
            System.err.println(msg);
        }
    }

    private String format(String tag, LogLevel level, String msg) {
        return String.format("[%s] %c/%s: %s", Thread.currentThread().getName(), level.name().charAt(0) + 32, tag, msg);
    }

    private String format(String tag, LogLevel level, String format, Object arg1) {
        return String.format("[%s] %c/%s: %s", Thread.currentThread().getName(), level.name().charAt(0) + 32, tag, MessageFormat.format(format, arg1));
    }

    private String format(String tag, LogLevel level, String format, Object arg1, Object arg2) {
        return String.format("[%s] %c/%s: %s", Thread.currentThread().getName(), level.name().charAt(0) + 32, tag, MessageFormat.format(format, arg1, arg2));
    }

    private String format(String tag, LogLevel level, String format, Object... args) {
        return String.format("[%s] %c/%s: %s", Thread.currentThread().getName(), level.name().charAt(0) + 32, tag, MessageFormat.format(format, args));
    }
}
