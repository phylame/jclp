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

package jclp.io;

import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import static jclp.Validate.requireNotEmpty;

public final class ResourceUtils {
    private ResourceUtils() {
    }

    public static final String CLASSPATH_PREFIX = "!";

    public static URL getResource(String uri) {
        return getResource(uri, null);
    }

    @SneakyThrows(MalformedURLException.class)
    public static URL getResource(String uri, ClassLoader loader) {
        requireNotEmpty(uri, "path cannot be null or empty");
        if (uri.startsWith(CLASSPATH_PREFIX)) {
            return (loader != null ? loader : getContextLoader()).getResource(uri.substring(CLASSPATH_PREFIX.length()));
        }
        val path = Paths.get(uri);
        if (Files.exists(path)) {
            return path.toUri().toURL();
        }
        try {
            return new URL(uri);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public static InputStream openResource(String uri) throws IOException {
        return openResource(uri, null, false);
    }

    public static InputStream openResource(String uri, ClassLoader loader) throws IOException {
        return openResource(uri, loader, false);
    }

    public static InputStream openResource(String uri, ClassLoader loader, boolean reload) throws IOException {
        val url = getResource(uri, loader);
        if (url != null) {
            val conn = url.openConnection();
            if (reload) {
                conn.setUseCaches(false);
            }
            return conn.getInputStream();
        }
        return null;
    }

    public static Properties getProperties(String uri) throws IOException {
        return getProperties(uri, null, false);
    }

    public static Properties getProperties(String uri, ClassLoader loader) throws IOException {
        return getProperties(uri, loader, false);
    }

    public static Properties getProperties(String uri, ClassLoader loader, boolean reload) throws IOException {
        @Cleanup
        val in = openResource(uri, loader, reload);
        if (in != null) {
            val prop = new Properties();
            prop.load(in);
            return prop;
        }
        return null;
    }

    public static ClassLoader getContextLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
}
