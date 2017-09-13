package jclp.i18n;

import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.Map;
import java.util.MissingResourceException;

@RequiredArgsConstructor
public class MapTranslator extends AbstractTranslator {
    private final Map<String, String> map;

    @Override
    protected String handleGet(String key) throws MissingResourceException {
        val text = map.get(key);
        if (text == null) {
            throw new MissingResourceException(null, getClass().getName(), key);
        }
        return text;
    }
}
