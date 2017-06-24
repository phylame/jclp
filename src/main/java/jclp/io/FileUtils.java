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

import jclp.function.Consumer;
import lombok.val;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public final class FileUtils {
    private FileUtils() {
    }

    public static void walkDir(File dir, Consumer<File> consumer) {
        val names = dir.list();
        if (names == null) {
            return;
        }
        for (String name : names) {
            val file = new File(dir, name);
            if (file.isDirectory()) {
                walkDir(file, consumer);
            } else {
                consumer.accept(file);
            }
        }
    }

    public static void copyFile(File source, File target) throws IOException {
        copyFile(source, target, 0x10000); // 64k to be fastest
    }

    /**
     * Copies source file to target file.
     *
     * @param source     the source file
     * @param target     the target file
     * @param bufferSize size of buffer area
     * @throws IOException if occur I/O errors
     */
    public static void copyFile(File source, File target, int bufferSize) throws IOException {
        try (val in = new FileInputStream(source); FileOutputStream out = new FileOutputStream(target)) {
            IOUtils.copy(in, out, -1, bufferSize);
        }
    }

    public static void write(File file, CharSequence cs) throws IOException {
        write(file, cs, null);
    }

    /**
     * Writes specified char sequence to file.
     *
     * @param file     the output file
     * @param cs       the char sequence
     * @param encoding the encoding, if {@code null} use default encoding
     * @throws IOException if occur I/O errors
     */
    public static void write(File file, CharSequence cs, String encoding) throws IOException {
        try (val writer = IOUtils.writerFor(file, encoding)) {
            writer.write(cs.toString());
        }
    }

    public static void writeLines(File file, Collection<?> items) throws IOException {
        writeLines(file, items, System.lineSeparator());
    }

    public static void writeLines(File file, Collection<?> items, String lineSeparator) throws IOException {
        writeLines(file, items, lineSeparator, null);
    }

    /**
     * Writes specified char sequence to file.
     *
     * @param file     the output file
     * @param cs       the char sequence
     * @param encoding the encoding, if {@code null} use default encoding
     * @throws IOException if occur I/O errors
     */
    public static void writeLines(File file, Collection<?> items, String lineSeparator, String encoding) throws IOException {
        try (val writer = IOUtils.writerFor(file, encoding)) {
            IOUtils.dumpLines(writer, items, lineSeparator);
        }
    }

    public static String toString(File file) throws IOException {
        return toString(file, null);
    }

    public static String toString(File file, String encoding) throws IOException {
        return IOUtils.toString(IOUtils.readerFor(file, encoding));
    }

    public static List<String> toLines(File file, boolean skipEmpty) throws IOException {
        return toLines(file, null, skipEmpty);
    }

    public static List<String> toLines(File file, String encoding, boolean skipEmpty) throws IOException {
        return IOUtils.toLines(IOUtils.readerFor(file, encoding), skipEmpty);
    }

    public static Iterator<String> linesOf(File file, boolean skipEmpty) throws IOException {
        return linesOf(file, null, skipEmpty);
    }

    public static Iterator<String> linesOf(File file, String encoding, boolean skipEmpty) throws IOException {
        return IOUtils.linesOf(IOUtils.readerFor(file, encoding), skipEmpty);
    }
}
