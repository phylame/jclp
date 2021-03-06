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

import jclp.log.Log;
import jclp.CollectionUtils;
import jclp.MiscUtils;
import jclp.Validate;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;

/**
 * Utilities for file and I/O operations.
 */
public final class IOUtils {
    private IOUtils() {
    }

    private static final String TAG = "IOUtils";

    /**
     * Prefix for path name in class path.
     */
    public static final String CLASS_PATH_PREFIX = "!";

    /**
     * Default buffer size.
     */
    public static int DEFAULT_BUFFER_SIZE = 8192;

    /**
     * End of file.
     */
    public static final int EOF = -1;

    /**
     * Close specified {@code Closeable} quietly.
     *
     * @param c the {@code Closeable} instance
     */
    public static void closeQuietly(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException ignored) {
            }
        }
    }

    public static byte[] ensureLength(byte[] b, int length) {
        return b.length >= length ? b : Arrays.copyOf(b, length);
    }

    public static BufferedInputStream buffered(@NonNull InputStream in) {
        return (in instanceof BufferedInputStream) ? (BufferedInputStream) in : new BufferedInputStream(in);
    }

    public static BufferedOutputStream buffered(@NonNull OutputStream out) {
        return (out instanceof BufferedOutputStream) ? (BufferedOutputStream) out : new BufferedOutputStream((out));
    }

    public static BufferedReader buffered(@NonNull Reader reader) {
        return (reader instanceof BufferedReader) ? (BufferedReader) reader : new BufferedReader(reader);
    }

    public static BufferedWriter buffered(@NonNull Writer writer) {
        return (writer instanceof BufferedWriter) ? (BufferedWriter) writer : new BufferedWriter(writer);
    }

    public interface Reading {
        int read(byte[] b, int off, int len) throws IOException;
    }

    public interface Writing {
        void write(byte[] b, int off, int len) throws IOException;

        void flush() throws IOException;
    }

    public static Reading readingFor(@NonNull InputStream in) {
        return new ISReading(in);
    }

    public static Reading readingFor(@NonNull RandomAccessFile raf) {
        return new RAFWriting(raf);
    }

    public static Writing writingFor(@NonNull OutputStream out) {
        return new OSWriting(out);
    }

    public static Writing writingFor(@NonNull RandomAccessFile raf) {
        return new RAFWriting(raf);
    }

    public static long copy(InputStream in, OutputStream out, int size) throws IOException {
        return copy(in, out, size, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Copies bytes from {@code InputStream} to {@code OutputStream}.
     *
     * @param in         source stream
     * @param out        destination stream
     * @param size       size of bytes to copy, <code>-1</code> to copy all
     * @param bufferSize size of buffer area
     * @return number of copied bytes
     * @throws IOException if occur I/O error.
     */
    public static long copy(InputStream in, OutputStream out, int size, int bufferSize) throws IOException {
        return copy(readingFor(in), writingFor(out), size, bufferSize);
    }

    public static long copy(InputStream in, RandomAccessFile out, int size) throws IOException {
        return copy(in, out, size, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Copies bytes from {@code InputSteam} to {@code RandomAccessFile}.
     *
     * @param in         source stream
     * @param out        destination file
     * @param size       size of bytes to copy, <code>-1</code> to copy all
     * @param bufferSize size of buffer area
     * @return number of copied bytes
     * @throws IOException if occur I/O error.
     */
    public static long copy(InputStream in, RandomAccessFile out, int size, int bufferSize) throws IOException {
        return copy(readingFor(in), writingFor(out), size, bufferSize);
    }

    public static long copy(RandomAccessFile in, OutputStream out, int size) throws IOException {
        return copy(in, out, size, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Copies bytes from {@code RandomAccessFile} to {@code OutputStream}.
     *
     * @param in         source file
     * @param out        destination stream
     * @param size       size of bytes to copy, <code>-1</code> to copy all
     * @param bufferSize size of buffer area
     * @return number of copied bytes
     * @throws IOException if occur I/O error.
     */
    public static long copy(RandomAccessFile in, OutputStream out, int size, int bufferSize) throws IOException {
        return copy(readingFor(in), writingFor(out), size, bufferSize);
    }

    public static long copy(RandomAccessFile in, RandomAccessFile out, int size) throws IOException {
        return copy(in, out, size, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Copies bytes from {@code RandomAccessFile} to {@code RandomAccessFile}.
     *
     * @param in         source file
     * @param out        destination file
     * @param size       size of bytes to copy, <code>-1</code> to copy all
     * @param bufferSize size of buffer area
     * @return number of copied bytes
     * @throws IOException if occur I/O error.
     */
    public static long copy(RandomAccessFile in, RandomAccessFile out, int size, int bufferSize) throws IOException {
        return copy(readingFor(in), writingFor(out), size, bufferSize);
    }

    public static long copy(Reading input, Writing output, int size) throws IOException {
        return copy(input, output, size, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Copies bytes from {@code Reading} to {@code Writing}.
     *
     * @param in         input source
     * @param out        destination output
     * @param size       size of bytes to copy, <code>-1</code> to copy all
     * @param bufferSize size of buffer area
     * @return number of copied bytes
     * @throws IOException if occur I/O error.
     */
    public static long copy(@NonNull Reading in, @NonNull Writing out, long size, int bufferSize) throws IOException {
        Validate.require(bufferSize > 0, "bufferSize <= 0");
        val buf = new byte[bufferSize];
        int n;
        long total = 0L;
        while ((n = in.read(buf, 0, bufferSize)) != EOF) {
            total += n;
            if (size < 0 || total < size) {
                out.write(buf, 0, n);
            } else {
                out.write(buf, 0, n - (int) (total - size));
                total = size;
                break;
            }
        }
        out.flush();
        return total;
    }

    /**
     * Gets all bytes of specified input.
     *
     * @param in the input
     * @return the bytes
     * @throws IOException if occur I/O error
     */
    public static byte[] toBytes(InputStream in) throws IOException {
        return toBytes(readingFor(buffered(in)));
    }

    /**
     * Gets all bytes of specified input.
     *
     * @param raf the input
     * @return the bytes
     * @throws IOException if occur I/O error
     */
    public static byte[] toBytes(RandomAccessFile raf) throws IOException {
        return toBytes(readingFor(raf));
    }

    /**
     * Gets all bytes of specified input.
     *
     * @param in the input
     * @return the bytes
     * @throws IOException if occur I/O error
     */
    public static byte[] toBytes(Reading in) throws IOException {
        val out = new ByteBuilder();
        copy(in, writingFor(out), -1, DEFAULT_BUFFER_SIZE);
        return out.array();
    }

    public static Reader readerFor(File file) throws IOException {
        return readerFor(file, null);
    }

    /**
     * Opens a reader for specified file with encoding.
     *
     * @param file     the input file
     * @param encoding the encoding, if {@code null} use default encoding
     * @return the reader
     * @throws IOException if occur I/O errors
     */
    public static Reader readerFor(File file, String encoding) throws IOException {
        return readerFor(new FileInputStream(file), encoding);
    }

    public static Reader readerFor(URL url) throws IOException {
        return readerFor(url, null);
    }

    public static Reader readerFor(URL url, String encoding) throws IOException {
        return readerFor(url.openStream(), encoding);
    }

    @SneakyThrows(UnsupportedEncodingException.class)
    public static Reader readerFor(InputStream in) {
        return readerFor(in, null);
    }

    /**
     * Open a reader for specified input stream with specified encoding.
     *
     * @param in       the input stream
     * @param encoding the encoding, if {@code null} use default encoding
     * @return the reader
     * @throws UnsupportedEncodingException if specified encoding is unsupported
     */
    public static Reader readerFor(InputStream in, String encoding) throws UnsupportedEncodingException {
        return encoding != null ? new InputStreamReader(in, encoding) : new InputStreamReader(in);
    }

    public static Writer writerFor(File file) throws IOException {
        return writerFor(file, null);
    }

    /**
     * Opens a writer for specified file with encoding.
     *
     * @param file     the output file
     * @param encoding the encoding, if {@code null} use default encoding
     * @return the writer
     * @throws IOException if occur I/O errors
     */
    public static Writer writerFor(File file, String encoding) throws IOException {
        return writerFor(new FileOutputStream(file), encoding);
    }

    @SneakyThrows(UnsupportedEncodingException.class)
    public static Writer writerFor(OutputStream out) {
        return writerFor(out, null);
    }

    /**
     * Open a writer for specified output stream with specified encoding.
     *
     * @param out      the out stream
     * @param encoding the encoding, if {@code null} use default encoding
     * @return the writer
     * @throws UnsupportedEncodingException if specified encoding is unsupported
     */
    public static Writer writerFor(OutputStream out, String encoding) throws UnsupportedEncodingException {
        return encoding != null ? new OutputStreamWriter(out, encoding) : new OutputStreamWriter(out);
    }

    public static long copy(Reader reader, Writer writer, int size) throws IOException {
        return copy(reader, writer, size, DEFAULT_BUFFER_SIZE);
    }

    /**
     * Copies characters from {@code Reader} to {@code Reader}.
     *
     * @param in         source reader
     * @param out        destination writer
     * @param size       size of characters to copy, <code>-1</code> to copy all
     * @param bufferSize size of buffer area
     * @return number of copied characters
     * @throws IOException if occur I/O error.
     */
    public static long copy(@NonNull Reader in, @NonNull Writer out, long size, int bufferSize) throws IOException {
        Validate.require(bufferSize > 0, "bufferSize <= 0");
        val buf = new char[bufferSize];
        int n;
        long total = 0L;
        while ((n = in.read(buf, 0, bufferSize)) != EOF) {
            total += n;
            if (size < 0 || total < size) {
                out.write(buf, 0, n);
            } else {
                out.write(buf, 0, n - (int) (total - size));
                total = size;
                break;
            }
        }
        out.flush();
        return total;
    }

    /**
     * Gets all characters of specified reader.
     *
     * @param reader the input reader
     * @return the string
     * @throws IOException if occur I/O error.
     */
    public static String toString(Reader reader) throws IOException {
        val out = new StringWriter();
        copy(buffered(reader), out, -1, DEFAULT_BUFFER_SIZE);
        return out.toString();
    }

    public static String toString(InputStream in) throws IOException {
        return toString(in, null);
    }

    /**
     * Gets string from specified input with specified encoding.
     *
     * @param in       the input
     * @param encoding the encoding, if {@code null} use default encoding
     * @return the string
     * @throws IOException if occur I/O error
     */
    public static String toString(InputStream in, String encoding) throws IOException {
        return toString(readingFor(buffered(in)), encoding);
    }

    public static String toString(Reading in) throws IOException {
        return toString(in, null);
    }

    /**
     * Gets string from specified input with specified encoding.
     *
     * @param in       the input
     * @param encoding the encoding, if {@code null} use default encoding
     * @return the string
     * @throws IOException if occur I/O error
     */
    public static String toString(Reading in, String encoding) throws IOException {
        val out = new ByteArrayOutputStream();
        copy(in, writingFor(out), -1, DEFAULT_BUFFER_SIZE);
        return encoding != null ? out.toString(encoding) : out.toString();
    }

    public static List<String> toLines(InputStream in, boolean skipEmpty) throws IOException {
        return toLines(in, null, skipEmpty);
    }

    /**
     * Gets all lines of specified input stream.
     *
     * @param in        the input stream
     * @param encoding  encoding of the bytes or {@code null} to use default encoding
     * @param skipEmpty {@literal true} to skip empty lines
     * @return list of lines
     * @throws IOException if occur I/O error
     */
    public static List<String> toLines(InputStream in, String encoding, boolean skipEmpty) throws IOException {
        return toLines(readerFor(buffered(in), encoding), skipEmpty);
    }

    /**
     * Gets all lines of specified reader.
     *
     * @param reader    the input reader
     * @param skipEmpty {@literal true} to skip empty lines
     * @return list of lines
     * @throws IOException if occur I/O error
     */
    public static List<String> toLines(@NonNull Reader reader, boolean skipEmpty) throws IOException {
        val br = buffered(reader);
        val lines = new LinkedList<String>();
        String line;
        while ((line = br.readLine()) != null) {
            if (!line.isEmpty() || !skipEmpty) {
                lines.add(line);
            }
        }
        return lines;
    }

    public static Iterator<String> linesOf(InputStream in, boolean skipEmpty) throws IOException {
        return linesOf(in, null, skipEmpty);
    }

    public static Iterator<String> linesOf(InputStream in, String encoding, boolean skipEmpty) throws IOException {
        return linesOf(readerFor(buffered(in), encoding), skipEmpty);
    }

    public static Iterator<String> linesOf(@NonNull Reader reader, boolean skipEmpty) {
        return new LineIterator(skipEmpty, buffered(reader));
    }

    public static void dumpLines(@NonNull Writer writer, Collection<?> items) throws IOException {
        dumpLines(writer, items, System.lineSeparator());
    }

    public static void dumpLines(@NonNull Writer writer, Collection<?> items, String lineSeparator) throws IOException {
        val bw = buffered(writer);
        for (val item : items) {
            bw.append(item.toString()).append(lineSeparator);
        }
        bw.flush();
    }

    public static URL resourceFor(@NonNull String path) throws MalformedURLException {
        return resourceFor(path, null);
    }

    public static URL resourceFor(@NonNull String path, ClassLoader loader) throws MalformedURLException {
        if (path.startsWith(CLASS_PATH_PREFIX)) {
            val name = path.substring(CLASS_PATH_PREFIX.length());
            return loader != null ? loader.getResource(name) : MiscUtils.getContextClassLoader().getResource(name);
        } else if (path.matches("^[a-z]{2,}://.*")) {
            return new URL(path);
        } else {
            val file = new File(path);
            return file.exists() ? new URL("file:///" + file.getAbsolutePath()) : null;
        }
    }

    public static InputStream openResource(@NonNull String path) throws IOException {
        return openResource(path, null);
    }

    public static InputStream openResource(@NonNull String path, ClassLoader loader) throws IOException {
        val url = resourceFor(path, loader);
        return url != null ? url.openStream() : null;
    }

    public static Iterable<URL> resourcesFor(@NonNull String name) {
        return resourcesFor(name, null);
    }

    public static Iterable<URL> resourcesFor(@NonNull String name, ClassLoader loader) {
        return AccessController.doPrivileged(new FindResourcesAction(name, loader));
    }

    @RequiredArgsConstructor
    private static class ISReading implements Reading {
        @NonNull
        private final InputStream stream;

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return stream.read(b, off, len);
        }
    }

    @RequiredArgsConstructor
    private static class OSWriting implements Writing {
        @NonNull
        private final OutputStream stream;

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            stream.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {
            stream.flush();
        }
    }

    @RequiredArgsConstructor
    private static class RAFWriting implements Reading, Writing {
        @NonNull
        private final RandomAccessFile raf;

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return raf.read(b, off, len);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            raf.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {

        }
    }

    @RequiredArgsConstructor
    private static class FindResourcesAction implements PrivilegedAction<Iterable<URL>> {
        @NonNull
        private final String name;
        private final ClassLoader loader;

        @Override
        public Iterable<URL> run() {
            Enumeration<URL> urls = null;
            try {
                urls = loader != null ? loader.getResources(name) : ClassLoader.getSystemResources(name);
            } catch (IOException | NoSuchMethodError e) {
                Log.e(TAG, "cannot find resources", e);
            }
            return CollectionUtils.iterable(CollectionUtils.iterator(urls));
        }
    }

    @RequiredArgsConstructor
    private static class LineIterator implements Iterator<String> {
        private final boolean skipEmpty;

        private final BufferedReader reader;

        private String nextLine = null;
        private boolean done = false;

        @Override
        public boolean hasNext() {
            if (nextLine == null && !done) {
                while (true) {
                    try {
                        nextLine = reader.readLine();
                        if (nextLine == null || !skipEmpty || !nextLine.isEmpty()) {
                            break;
                        }
                    } catch (IOException e) {
                        nextLine = null;
                        done = true;
                        break;
                    }
                }
            }
            return nextLine != null;
        }

        @Override
        public String next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            val answer = nextLine;
            nextLine = null;
            return answer;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

}
