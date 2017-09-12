package jclp.i18n;

import jclp.value.Lazy;

import java.util.Collection;
import java.util.MissingResourceException;

import static jclp.Validate.checkNotNull;

public class TranslatorSupport {
    public static String path = null;

    private static final Lazy<Linguist> linguist = new Lazy<>(() -> {
        checkNotNull(path, "path is not specified");
        return new Linguist(path);
    });

    public static String tr(String key) throws MissingResourceException {
        return linguist.get().tr(key);
    }

    public static String optTr(String key, String fallback) {
        return linguist.get().optTr(key, fallback);
    }

    public static String tr(String key, Object... args) throws MissingResourceException {
        return linguist.get().tr(key, args);
    }

    public static String optTr(String key, String fallback, Object... args) {
        return linguist.get().optTr(key, fallback, args);
    }

    public static void attach(Collection<? extends Translator> translators) {
        linguist.get().attach(translators);
    }

    public static void detach(Collection<? extends Translator> translators) {
        linguist.get().detach(translators);
    }
}
