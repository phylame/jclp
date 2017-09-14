package jclp.io;

import jclp.StringUtils;
import jclp.value.Lazy;
import lombok.NonNull;
import lombok.val;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Utilities for mime type.
 */
public final class MimeUtils {
    private MimeUtils() {
    }

    /**
     * MIME type for unknown file.
     */
    public static final String UNKNOWN_MIME = "application/octet-stream";

    private static final Lazy<Properties> mimeMap = new Lazy<>(() -> {
        try {
            return ResourceUtils.getProperties("!jclp/io/mime.properties", PathUtils.class.getClassLoader());
        } catch (IOException e) {
            return new Properties();
        }
    });

    public static void mapMime(@NonNull String extension, @NonNull String mime) {
        mimeMap.get().put(extension, mime);
    }

    public static void mapMimes(@NonNull Map<?, ?> m) {
        mimeMap.get().putAll(m);
    }

    public static String getMime(@NonNull String name) {
        if (name.isEmpty()) {
            return "";
        }
        val ext = PathUtils.extName(name);
        return ext.isEmpty() ? UNKNOWN_MIME : mimeMap.get().getProperty(ext, UNKNOWN_MIME);
    }

    public static String detectMime(@NonNull String path, String mime) {
        return StringUtils.isEmpty(mime) ? getMime(path) : mime;
    }
}
