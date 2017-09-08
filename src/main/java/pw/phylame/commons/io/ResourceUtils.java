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

package pw.phylame.commons.io;

import lombok.Cleanup;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import static pw.phylame.commons.Validate.requireNotEmpty;

public final class ResourceUtils {
    private ResourceUtils() {
    }

    public static final String CLASSPATH_PREFIX = "!";

    public static String trimPrefix(@NonNull String path) {
        return path.startsWith(CLASSPATH_PREFIX) ? path.substring(CLASSPATH_PREFIX.length()) : path;
    }

    public static URL getResource(String path) {
        return getResource(path, null);
    }

    @SneakyThrows(MalformedURLException.class)
    public static URL getResource(String path, ClassLoader loader) {
        requireNotEmpty(path, "path cannot be null or empty");
        if (path.startsWith(CLASSPATH_PREFIX)) {
            path = path.substring(CLASSPATH_PREFIX.length());
            return loader != null ? loader.getResource(path) : Thread.currentThread().getContextClassLoader().getResource(path);
        } else if (path.matches("^[\\w]+://.*")) {
            try {
                return new URL(path);
            } catch (MalformedURLException e) {
                return null;
            }
        } else {
            val file = new File(path);
            return file.exists() ? file.toURI().toURL() : null;
        }
    }

    public static InputStream openResource(String path) throws IOException {
        return openResource(path, null, false);
    }

    public static InputStream openResource(String path, ClassLoader loader, boolean reload) throws IOException {
        val url = getResource(path, loader);
        if (url != null) {
            URLConnection conn = url.openConnection();
            if (reload) {
                conn.setUseCaches(false);
            }
            return conn.getInputStream();
        }
        return null;
    }

    public static Properties getProperties(@NonNull String path) throws IOException {
        return getProperties(path, null, false);
    }

    public static Properties getProperties(@NonNull String path, ClassLoader loader, boolean reload) throws IOException {
        @Cleanup val in = openResource(path, loader, reload);
        if (in != null) {
            val prop = new Properties();
            prop.load(in);
            return prop;
        }
        return null;
    }
}
