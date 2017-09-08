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

package src.jclp.log;

import java.text.MessageFormat;

public class DefaultFacade implements Facade {
    @Override
    public void log(String tag, Level level, String msg) {
        print(level, format(tag, level, msg));
    }

    @Override
    public void log(String tag, Level level, String format, Object arg1) {
        print(level, format(tag, level, format, arg1));
    }

    @Override
    public void log(String tag, Level level, String format, Object arg1, Object arg2) {
        print(level, format(tag, level, format, arg1, arg2));
    }

    @Override
    public void log(String tag, Level level, String format, Object... args) {
        print(level, format(tag, level, format, args));
    }

    @Override
    public void log(String tag, Level level, String msg, Throwable t) {
        print(level, format(tag, level, msg));
        t.printStackTrace();
    }

    private void print(Level level, String msg) {
        if (level.getCode() > Level.WARN.getCode()) {
            System.out.println(msg);
        } else {
            System.err.println(msg);
        }
    }

    private static final String PATTERN = "[%s] %c/%s: %s";

    private String format(String tag, Level level, String msg) {
        return String.format(PATTERN, Thread.currentThread().getName(), level.name().charAt(0) + 32, tag, msg);
    }

    private String format(String tag, Level level, String format, Object arg1) {
        return String.format(PATTERN, Thread.currentThread().getName(), level.name().charAt(0) + 32, tag, MessageFormat.format(format, arg1));
    }

    private String format(String tag, Level level, String format, Object arg1, Object arg2) {
        return String.format(PATTERN, Thread.currentThread().getName(), level.name().charAt(0) + 32, tag, MessageFormat.format(format, arg1, arg2));
    }

    private String format(String tag, Level level, String format, Object... args) {
        return String.format(PATTERN, Thread.currentThread().getName(), level.name().charAt(0) + 32, tag, MessageFormat.format(format, args));
    }
}
