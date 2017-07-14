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

package jclp.log;

import jclp.function.Function;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static jclp.util.CollectionUtils.getOrPut;

public class Slf4jFacade implements Facade {
    @Override
    public void log(String tag, Level level, String msg) {
        val logger = getLogger(tag);
        switch (level) {
            case TRACE:
                logger.trace(msg);
                break;
            case DEBUG:
                logger.debug(msg);
                break;
            case INFO:
                logger.info(msg);
                break;
            case WARN:
                logger.warn(msg);
                break;
            case ERROR:
                logger.error(msg);
                break;
            case FATAL:
                logger.error(msg);
                break;
        }
    }

    @Override
    public void log(String tag, Level level, String format, Object arg1) {
        val logger = getLogger(tag);
        format = convertFormat(format);
        switch (level) {
            case TRACE:
                logger.trace(format, arg1);
                break;
            case DEBUG:
                logger.debug(format, arg1);
                break;
            case INFO:
                logger.info(format, arg1);
                break;
            case WARN:
                logger.warn(format, arg1);
                break;
            case ERROR:
                logger.error(format, arg1);
                break;
            case FATAL:
                logger.error(format, arg1);
                break;
        }
    }

    @Override
    public void log(String tag, Level level, String format, Object arg1, Object arg2) {
        val logger = getLogger(tag);
        format = convertFormat(format);
        switch (level) {
            case TRACE:
                logger.trace(format, arg1, arg2);
                break;
            case DEBUG:
                logger.debug(format, arg1, arg2);
                break;
            case INFO:
                logger.info(format, arg1, arg2);
                break;
            case WARN:
                logger.warn(format, arg1, arg2);
                break;
            case ERROR:
                logger.error(format, arg1, arg2);
                break;
            case FATAL:
                logger.error(format, arg1, arg2);
                break;
        }
    }

    @Override
    public void log(String tag, Level level, String format, Object... args) {
        val logger = getLogger(tag);
        format = convertFormat(format);
        switch (level) {
            case TRACE:
                logger.trace(format, args);
                break;
            case DEBUG:
                logger.debug(format, args);
                break;
            case INFO:
                logger.info(format, args);
                break;
            case WARN:
                logger.warn(format, args);
                break;
            case ERROR:
                logger.error(format, args);
                break;
            case FATAL:
                logger.error(format, args);
                break;
        }
    }

    @Override
    public void log(String tag, Level level, String msg, Throwable t) {
        val logger = getLogger(tag);
        switch (level) {
            case TRACE:
                logger.trace(msg, t);
                break;
            case DEBUG:
                logger.debug(msg, t);
                break;
            case INFO:
                logger.info(msg, t);
                break;
            case WARN:
                logger.warn(msg, t);
                break;
            case ERROR:
                logger.error(msg, t);
                break;
            case FATAL:
                logger.error(msg, t);
                break;
        }
    }

    private String convertFormat(String format) {
        return format.replaceAll("\\{\\d}", "{}");
    }

    private Logger getLogger(final String tag) {
        return getOrPut(loggers, tag, new Function<String, Logger>() {
            @Override
            public Logger apply(String arg) {
                return LoggerFactory.getLogger(tag);
            }
        });
    }

    private final Map<String, Logger> loggers = new HashMap<>();

}
