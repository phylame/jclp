/*
 * Copyright 2017 Peng Wan <phylame@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jclp;

import jclp.text.Render;
import jclp.value.Pair;
import lombok.NonNull;
import lombok.val;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public final class StringUtils {
    private StringUtils() {
    }

    public static final char CHINESE_INDENT = '\u3000';

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return cs != null && cs.length() != 0;
    }

    public static boolean isBlank(CharSequence cs) {
        if (isEmpty(cs)) {
            return true;
        }
        char ch;
        for (int i = 0, end = cs.length(); i != end; ++i) {
            if ((ch = cs.charAt(i)) != CHINESE_INDENT && !Character.isWhitespace(ch)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

    /**
     * Tests if all characters of specified string are upper case.
     *
     * @param cs a {@code CharSequence} represent string
     * @return {@code true} if all characters are upper case or {@code false} if contains lower case character(s)
     */
    public static boolean isLowerCase(CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        }
        for (int i = 0, end = cs.length(); i != end; ++i) {
            if (Character.isUpperCase(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Tests if all characters of specified string are lower case.
     *
     * @param cs a {@code CharSequence} represent string
     * @return {@code true} if all characters are lower case or {@code false} if contains upper case character(s)
     */
    public static boolean isUpperCase(CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        }
        for (int i = 0, end = cs.length(); i != end; ++i) {
            if (Character.isLowerCase(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static String coalesce(CharSequence source, CharSequence fallback) {
        return isNotEmpty(source) ? source.toString() : Objects.toString(fallback, null);
    }

    public static String coalesce(CharSequence cs, @NonNull String format, Object... args) {
        return isNotEmpty(cs) ? cs.toString() : String.format(format, args);
    }

    /**
     * Returns a copy of {@code cs} that first letter was converted to upper case.
     *
     * @param cs the input string
     * @return string which first character is upper
     */
    public static String capitalized(CharSequence cs) {
        return isEmpty(cs)
                ? Objects.toString(cs, null)
                : String.valueOf(Character.toTitleCase(cs.charAt(0))) + cs.subSequence(1, cs.length());
    }

    /**
     * Returns a copy of {@code cs} that each word was converted to capital.
     *
     * @param cs the string
     * @return string which each word is capital
     */
    public static String titled(CharSequence cs) {
        if (isEmpty(cs)) {
            return Objects.toString(cs, null);
        }
        val b = new StringBuilder(cs.length());
        boolean isFirst = true;
        char ch;
        for (int i = 0, end = cs.length(); i != end; ++i) {
            if ((ch = cs.charAt(i)) != '\'' && ch != '"' && !Character.isLetter(ch)) {
                isFirst = true;
            } else if (isFirst) {
                ch = Character.toTitleCase(ch);
                isFirst = false;
            }
            b.append(ch);
        }
        return b.toString();
    }

    /**
     * Like {@link String#trim()} but removes Chinese paragraph indent (u3000).
     *
     * @param cs the input string
     * @return the string removed space
     */
    public static String trimmed(CharSequence cs) {
        if (isEmpty(cs)) {
            return Objects.toString(cs, null);
        }
        int len = cs.length();
        int st = 0;
        char ch;
        while (st < len && (((ch = cs.charAt(st)) <= ' ') || (ch == CHINESE_INDENT))) {
            st++;
        }
        while (st < len && (((ch = cs.charAt(len - 1)) <= ' ') || (ch == CHINESE_INDENT))) {
            len--;
        }
        return st > 0 || len < cs.length() ? cs.subSequence(st, len).toString() : Objects.toString(cs, null);
    }

    public static String stripped(CharSequence cs, String pattern) {
        val str = Objects.toString(cs, null);
        if (isEmpty(str) || isEmpty(pattern)) {
            return str;
        }
        int st = 0;
        int len = str.length();
        while (st < len && pattern.indexOf(str.charAt(st)) != -1) {
            st++;
        }
        while (st < len && pattern.indexOf(str.charAt(len - 1)) != -1) {
            len--;
        }
        return st > 0 || len < str.length() ? str.substring(st, len) : str;
    }

    public static <T> String repeated(T obj, int count) {
        return repeated(obj, count, null);
    }

    public static <T> String repeated(T obj, int count, Render<? super T> transform) {
        if (count <= 0) {
            return "";
        } else if (count == 1) {
            return obj.toString();
        }
        val b = new StringBuilder(8 * count);
        for (int i = 0; i < count; ++i) {
            b.append(transform != null ? transform.render(obj) : obj);
        }
        return b.toString();
    }

    /**
     * Returns list of lines split from specified string.
     *
     * @param cs        the input string
     * @param skipEmpty {@literal true} to skip empty line
     * @return list of lines, never {@code null}
     * @throws NullPointerException if the {@code cs} is {@code null}
     */
    public static List<String> splitLines(@NonNull CharSequence cs, boolean skipEmpty) {
        val lines = new LinkedList<String>();
        splitLines(cs, lines, skipEmpty);
        return lines;
    }

    public static void splitLines(@NonNull CharSequence cs, List<String> lines, boolean skipEmpty) {
        int i, begin = 0;
        CharSequence subseq;
        val end = cs.length();
        for (i = 0; i < end; ) {
            val ch = cs.charAt(i);
            if ('\n' == ch) { // \n
                subseq = cs.subSequence(begin, i);
                if (subseq.length() > 0 || !skipEmpty) {
                    lines.add(subseq.toString());
                }
                begin = ++i;
            } else if ('\r' == ch) {
                subseq = cs.subSequence(begin, i);
                if (subseq.length() > 0 || !skipEmpty) {
                    lines.add(subseq.toString());
                }
                if (i + 1 < end && '\n' == cs.charAt(i + 1)) { // \r\n
                    begin = i += 2;
                } else { // \r
                    begin = ++i;
                }
            } else {
                ++i;
            }
        }
        if (i >= begin) {
            subseq = cs.subSequence(begin, cs.length());
            if (subseq.length() > 0 || !skipEmpty) {
                lines.add(subseq.toString());
            }
        }
    }

    public static String getFirst(String str, String separator) {
        return partition(str, separator).getFirst();
    }

    public static String getSecond(String str, String separator) {
        return partition(str, separator).getSecond();
    }

    public static Pair<String, String> partition(@NonNull String str, @NonNull String separator) {
        val index = str.indexOf(separator);
        return index < 0
                ? new Pair<>(str, "")
                : new Pair<>(str.substring(0, index), str.substring(index + separator.length()));
    }

    public static List<Pair<String, String>> getPairs(String str, String partSeparator) {
        return getPairs(str, partSeparator, "=");
    }

    public static List<Pair<String, String>> getPairs(@NonNull String str,
                                                      @NonNull String partSeparator,
                                                      @NonNull String valueSeparator) {
        val pairs = new ArrayList<Pair<String, String>>();
        for (val part : str.split(partSeparator)) {
            pairs.add(partition(trimmed(part), valueSeparator));
        }
        return pairs;
    }

    public static String getValue(String str, String name, String partSeparator) {
        return getValue(str, name, partSeparator, "=", true);
    }

    public static String getValue(String str, String name, String partSeparator, boolean ignoreCase) {
        return getValue(str, name, partSeparator, "=", ignoreCase);
    }

    public static String getValue(@NonNull String str,
                                  @NonNull String name,
                                  @NonNull String partSeparator,
                                  @NonNull String valueSeparator,
                                  boolean ignoreCase) {
        for (String part : str.split(partSeparator)) {
            part = part.trim();
            val index = part.indexOf(valueSeparator);
            if (index != -1) {
                val tag = part.substring(0, index);
                if (ignoreCase && tag.equalsIgnoreCase(name) || tag.equals(name)) {
                    return part.substring(index + 1);
                }
            }
        }
        return null;
    }

    public static String[] getValues(String str, String name, String partSeparator) {
        return getValues(str, name, partSeparator, "=", true);
    }

    public static String[] getValues(String str, String name, String partSeparator, boolean ignoreCase) {
        return getValues(str, name, partSeparator, "=", ignoreCase);
    }

    public static String[] getValues(@NonNull String str,
                                     @NonNull String name,
                                     @NonNull String partSeparator,
                                     @NonNull String valueSeparator,
                                     boolean ignoreCase) {
        val results = new ArrayList<String>();
        for (val part : str.split(partSeparator)) {
            val index = part.trim().indexOf(valueSeparator);
            if (index != -1) {
                val tag = part.substring(0, index);
                if (ignoreCase && tag.equalsIgnoreCase(name) || tag.equals(name)) {
                    results.add(part.substring(index + 1));
                }
            }
        }
        return results.toArray(new String[results.size()]);
    }
}
