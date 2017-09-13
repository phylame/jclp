package jclp.i18n;

import jclp.Attachable;
import jclp.CollectionUtils;
import lombok.val;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.MissingResourceException;

public abstract class AbstractTranslator implements Translator, Attachable<Translator> {
    private final LinkedHashSet<Translator> attachments = new LinkedHashSet<>();

    protected abstract String handleGet(String key) throws MissingResourceException;

    @Override
    public String tr(String key) throws MissingResourceException {
        try {
            return handleGet(key);
        } catch (MissingResourceException e) {
            if (CollectionUtils.isNotEmpty(attachments)) {
                for (val translator : attachments) {
                    try {
                        return translator.tr(key);
                    } catch (MissingResourceException ignored) {
                    }
                }
            }
            throw e;
        }
    }

    @Override
    public void attach(Collection<? extends Translator> translators) {
        attachments.addAll(translators);
    }

    @Override
    public void detach(Collection<? extends Translator> translators) {
        attachments.removeAll(translators);
    }
}
