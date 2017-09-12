package jclp.log;

public class SimpleFacade implements LogFacade {
    @Override
    public void log(String tag, LogLevel level, String msg) {
        print(tag, level, msg);
    }

    @Override
    public void log(String tag, LogLevel level, String msg, Throwable t) {
        print(tag, level, msg);
        t.printStackTrace();
    }

    private void print(String tag, LogLevel level, String msg) {
        msg = String.format("[%s] %c/%s: %s", Thread.currentThread().getName(), level.name().charAt(0) + 32, tag, msg);
        if (level.getCode() > LogLevel.WARN.getCode()) {
            System.out.println(msg);
        } else {
            System.err.println(msg);
        }
    }
}
