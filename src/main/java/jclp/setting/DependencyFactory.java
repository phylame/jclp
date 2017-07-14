package jclp.setting;

import jclp.condition.Conditions;
import lombok.val;

import static jclp.util.StringUtils.isEmpty;

public final class DependencyFactory {
    public static Dependency forPattern(String str) {
        if (isEmpty(str)) {
            return null;
        }
        int index = str.indexOf("@");
        if (index == -1) {
            return null;
        }
        val condition = Conditions.forPattern(str.substring(index + 1));
        if (condition == null) {
            return null;
        }
        return null;
        // return new Dependency(str.substring(0, index), condition);
    }
}
