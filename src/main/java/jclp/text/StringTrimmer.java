package jclp.text;

import jclp.util.StringUtils;

public class StringTrimmer implements Render<String> {
    @Override
    public String render(String str) {
        return StringUtils.trimmed(str);
    }
}
