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

import lombok.val;

import java.util.logging.Logger;

public class JULFacade implements Facade {
    @Override
    public void log(String tag, Level level, String msg) {
        val logger = Logger.getLogger(tag);
        logger.setLevel(mapLevel(Log.getLevel()));
        logger.log(mapLevel(level), msg);
    }

    @Override
    public void log(String tag, Level level, String format, Object arg1) {
        val logger = Logger.getLogger(tag);
        logger.setLevel(mapLevel(Log.getLevel()));
        logger.log(mapLevel(level), format, arg1);
    }

    @Override
    public void log(String tag, Level level, String format, Object arg1, Object arg2) {
        val logger = Logger.getLogger(tag);
        logger.setLevel(mapLevel(Log.getLevel()));
        logger.log(mapLevel(level), format, new Object[]{arg1, arg2});
    }

    @Override
    public void log(String tag, Level level, String format, Object... args) {
        val logger = Logger.getLogger(tag);
        logger.setLevel(mapLevel(Log.getLevel()));
        logger.log(mapLevel(level), format, args);
    }

    @Override
    public void log(String tag, Level level, String msg, Throwable t) {
        val logger = Logger.getLogger(tag);
        logger.setLevel(mapLevel(Log.getLevel()));
        logger.log(mapLevel(level), msg, t);
    }

    private java.util.logging.Level mapLevel(Level level) {
        switch (level) {
            case ALL:
                return java.util.logging.Level.ALL;
            case TRACE:
                return java.util.logging.Level.FINER;
            case DEBUG:
                return java.util.logging.Level.FINE;
            case INFO:
                return java.util.logging.Level.INFO;
            case WARN:
                return java.util.logging.Level.WARNING;
            case ERROR:
                return java.util.logging.Level.SEVERE;
            case FATAL:
                return java.util.logging.Level.SEVERE;
            case OFF:
                return java.util.logging.Level.OFF;
            default:
                return null;
        }
    }
}
