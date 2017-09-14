package jclp.io;

import jclp.value.Pair;
import lombok.NonNull;
import lombok.val;

/**
 * Utilities for file name.
 */
public final class PathUtils {
    private PathUtils() {
    }

    public static Pair<Integer, Integer> splitPath(@NonNull String path) {
        int extpos = path.length(), seppos;
        boolean extFound = false;
        char ch;
        for (seppos = extpos - 1; seppos >= 0; --seppos) {
            ch = path.charAt(seppos);
            if (ch == '.' && !extFound) {
                extpos = seppos;
                extFound = true;
            } else if (ch == '/' || ch == '\\') {
                break;
            }
        }
        return new Pair<>(seppos, extpos);
    }

    public static String dirName(@NonNull String path) {
        val index = splitPath(path).getFirst();
        return index != -1 ? path.substring(0, index) : "";
    }

    public static String fullName(@NonNull String path) {
        int index = splitPath(path).getFirst();
        return path.substring(index != 0 ? index + 1 : index);
    }

    public static String baseName(@NonNull String path) {
        val pair = splitPath(path);
        return path.substring(pair.getFirst() + 1, pair.getSecond());
    }

    public static String extName(@NonNull String path) {
        int index = splitPath(path).getSecond();
        return index != path.length() ? path.substring(index + 1) : "";
    }
}