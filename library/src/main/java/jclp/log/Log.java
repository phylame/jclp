package jclp.log;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.text.MessageFormat;

public final class Log {
    private Log() {
    }

    @Getter
    @Setter
    @NonNull
    private static LogLevel level = LogLevel.INFO;

    @Getter
    @Setter
    @NonNull
    private static LogFacade facade = new SimpleFacade();

    public static boolean isEnable(LogLevel level) {
        return level.getCode() <= Log.level.getCode();
    }

    public static void t(String tag, String msg) {
        log(tag, LogLevel.TRACE, msg);
    }

    public static void t(String tag, String format, Object... args) {
        log(tag, LogLevel.TRACE, format, args);
    }

    public static void t(String tag, String msg, Throwable t) {
        log(tag, LogLevel.TRACE, msg, t);
    }

    public static void d(String tag, String msg) {
        log(tag, LogLevel.DEBUG, msg);
    }

    public static void d(String tag, String format, Object... args) {
        log(tag, LogLevel.DEBUG, format, args);
    }

    public static void d(String tag, String msg, Throwable t) {
        log(tag, LogLevel.DEBUG, msg, t);
    }

    public static void i(String tag, String msg) {
        log(tag, LogLevel.INFO, msg);
    }

    public static void i(String tag, String format, Object... args) {
        log(tag, LogLevel.INFO, format, args);
    }

    public static void i(String tag, String msg, Throwable t) {
        log(tag, LogLevel.INFO, msg, t);
    }

    public static void w(String tag, String msg) {
        log(tag, LogLevel.WARN, msg);
    }

    public static void w(String tag, String format, Object... args) {
        log(tag, LogLevel.WARN, format, args);
    }

    public static void w(String tag, String msg, Throwable t) {
        log(tag, LogLevel.WARN, msg, t);
    }

    public static void e(String tag, String msg) {
        log(tag, LogLevel.ERROR, msg);
    }

    public static void e(String tag, String format, Object... args) {
        log(tag, LogLevel.ERROR, format, args);
    }

    public static void e(String tag, String msg, Throwable t) {
        log(tag, LogLevel.ERROR, msg, t);
    }

    private static void log(String tag, LogLevel level, String msg) {
        if (isEnable(level)) {
            facade.log(tag, level, msg);
        }
    }

    private static void log(String tag, LogLevel level, String format, Object... args) {
        if (isEnable(level)) {
            facade.log(tag, level, MessageFormat.format(format, args));
        }
    }

    private static void log(String tag, LogLevel level, String msg, Throwable t) {
        if (isEnable(level)) {
            facade.log(tag, level, msg, t);
        }
    }
}
