package jclp.test;

import static jclp.util.StringUtils.isEmpty;

import jclp.cond.Conditions;
import lombok.val;


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
