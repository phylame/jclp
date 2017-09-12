package jclp.log;

public interface LogFacade {
    void log(String tag, LogLevel level, String msg);

    void log(String tag, LogLevel level, String msg, Throwable t);
}
