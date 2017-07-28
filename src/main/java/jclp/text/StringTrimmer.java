package jclp.text;

import jclp.function.Function;
import jclp.util.StringUtils;

public class StringTrimmer implements Render<String>, Function<String, String> {
    @Override
    public String render(String str) {
        return StringUtils.trimmed(str);
    }

    @Override
    public String apply(String str) {
        return render(str);
    }
}
