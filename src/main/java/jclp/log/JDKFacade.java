package jclp.log;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JDKFacade implements LogFacade {
    @Override
    public void log(String tag, LogLevel level, String msg) {
        Logger.getLogger(tag).log(mapLevel(level), msg);
    }

    @Override
    public void log(String tag, LogLevel level, String msg, Throwable t) {
        Logger.getLogger(tag).log(mapLevel(level), msg, t);
    }

    private Level mapLevel(LogLevel level) {
        switch (level) {
        case ALL:
            return Level.ALL;
        case TRACE:
            return Level.FINER;
        case DEBUG:
            return Level.FINE;
        case INFO:
            return Level.INFO;
        case WARN:
            return Level.WARNING;
        case ERROR:
            return Level.SEVERE;
        case OFF:
            return Level.OFF;
        default:
            return null;
        }
    }
}
