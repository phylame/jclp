package jclp.io;

import lombok.NonNull;
import lombok.val;

import java.io.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static jclp.StringUtils.isNotEmpty;
import static jclp.Validate.require;

public final class IOUtils {
    private IOUtils() {
    }

    /**
     * Default buffer size.
     */
    public static int DEFAULT_BUFFER_SIZE = 8192;

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

    public static InputStream clipped(RandomAccessFile in, long offset) throws IOException {
        return new RAFInputStream(in, offset, -1);
    }

    public static InputStream clipped(RandomAccessFile in, long offset, long length) throws IOException {
        return new RAFInputStream(in, offset, length);
    }

    public static BufferedInputStream buffered(InputStream in) {
        return in instanceof BufferedInputStream
                ? (BufferedInputStream) in
                : new BufferedInputStream(in, DEFAULT_BUFFER_SIZE);
    }

    public static BufferedOutputStream buffered(OutputStream out) {
        return out instanceof BufferedOutputStream
                ? (BufferedOutputStream) out
                : new BufferedOutputStream(out, DEFAULT_BUFFER_SIZE);
    }

    public static RandomAccessFile buffered(RandomAccessFile in) {
        return in;
    }

    public static Reading readingFor(@NonNull InputStream in) {
        return in::read;
    }

    public static Reading readingFor(@NonNull RandomAccessFile in) {
        return in::read;
    }

    public static Writing writingFor(@NonNull OutputStream out) {
        return out::write;
    }

    public static Writing writingFor(@NonNull RandomAccessFile out) {
        return out::write;
    }

    public static long copy(InputStream in, OutputStream out, int size) throws IOException {
        return copy(in, out, size, DEFAULT_BUFFER_SIZE);
    }

    public static long copy(InputStream in, OutputStream out, int size, int bufferSize) throws IOException {
        return copy(readingFor(in), writingFor(out), size, bufferSize);
    }

    public static long copy(InputStream in, RandomAccessFile out, int size) throws IOException {
        return copy(in, out, size, DEFAULT_BUFFER_SIZE);
    }

    public static long copy(InputStream in, RandomAccessFile out, int size, int bufferSize) throws IOException {
        return copy(readingFor(in), writingFor(out), size, bufferSize);
    }

    public static long copy(RandomAccessFile in, OutputStream out, int size) throws IOException {
        return copy(in, out, size, DEFAULT_BUFFER_SIZE);
    }


    public static long copy(RandomAccessFile in, OutputStream out, int size, int bufferSize) throws IOException {
        return copy(readingFor(in), writingFor(out), size, bufferSize);
    }

    public static long copy(RandomAccessFile in, RandomAccessFile out, int size) throws IOException {
        return copy(in, out, size, DEFAULT_BUFFER_SIZE);
    }

    public static long copy(RandomAccessFile in, RandomAccessFile out, int size, int bufferSize) throws IOException {
        return copy(readingFor(in), writingFor(out), size, bufferSize);
    }

    public static long copy(Reading in, Writing out, int size) throws IOException {
        return copy(in, out, size, DEFAULT_BUFFER_SIZE);
    }

    public static long copy(@NonNull Reading in, @NonNull Writing out, long limit, int bufferSize) throws IOException {
        require(bufferSize > 0, "bufferSize <= 0");
        val buf = new byte[bufferSize];
        int n;
        long total = 0L;
        while ((n = in.read(buf, 0, bufferSize)) != -1) {
            total += n;
            if (limit < 0 || total < limit) {
                out.write(buf, 0, n);
            } else {
                out.write(buf, 0, n - (int) (total - limit));
                total = limit;
                break;
            }
        }
        return total;
    }

    public static byte[] toBytes(InputStream in) throws IOException {
        return toBytes(readingFor(buffered(in)));
    }

    public static byte[] toBytes(RandomAccessFile in) throws IOException {
        return toBytes(readingFor(in));
    }

    public static byte[] toBytes(Reading in) throws IOException {
        val out = new ByteBuilder();
        copy(in, writingFor(out), -1, DEFAULT_BUFFER_SIZE);
        return out.toArray();
    }

    public static String toString(InputStream in) throws IOException {
        return toString(in, null);
    }

    public static String toString(InputStream in, String encoding) throws IOException {
        return toString(readingFor(buffered(in)), encoding);
    }

    public static String toString(RandomAccessFile in) throws IOException {
        return toString(in, null);
    }

    public static String toString(RandomAccessFile in, String encoding) throws IOException {
        return toString(readingFor(buffered(in)), encoding);
    }

    public static String toString(Reading in) throws IOException {
        return toString(in, null);
    }

    public static String toString(Reading in, String encoding) throws IOException {
        val out = new ByteArrayOutputStream();
        copy(in, writingFor(out), -1, DEFAULT_BUFFER_SIZE);
        return encoding != null ? out.toString(encoding) : out.toString();
    }

    public static List<String> toLines(InputStream in, boolean skipEmpty) throws IOException {
        return toLines(in, null, skipEmpty);
    }

    public static List<String> toLines(InputStream in, String encoding, boolean skipEmpty) throws IOException {
        return toLines(readerFor(buffered(in), encoding), skipEmpty);
    }

    public static BufferedReader buffered(Reader in) {
        return in instanceof BufferedReader
                ? (BufferedReader) in
                : new BufferedReader(in, DEFAULT_BUFFER_SIZE);
    }

    public static BufferedWriter buffered(Writer out) {
        return out instanceof BufferedWriter
                ? (BufferedWriter) out
                : new BufferedWriter(out, DEFAULT_BUFFER_SIZE);
    }

    public static Reader readerFor(InputStream in) {
        return new InputStreamReader(in);
    }

    public static Reader readerFor(InputStream in, String encoding) throws UnsupportedEncodingException {
        return isNotEmpty(encoding) ? new InputStreamReader(in, encoding) : new InputStreamReader(in);
    }

    public static Writer writerFor(OutputStream out) {
        return new OutputStreamWriter(out);
    }

    public static Writer writerFor(OutputStream out, String encoding) throws UnsupportedEncodingException {
        return isNotEmpty(encoding) ? new OutputStreamWriter(out, encoding) : new OutputStreamWriter(out);
    }

    public static long copy(Reader in, Writer out, int size) throws IOException {
        return copy(in, out, size, DEFAULT_BUFFER_SIZE);
    }

    public static long copy(@NonNull Reader in, @NonNull Writer out, long limit, int bufferSize) throws IOException {
        require(bufferSize > 0, "bufferSize <= 0");
        val buf = new char[bufferSize];
        int n;
        long total = 0L;
        while ((n = in.read(buf, 0, bufferSize)) != -1) {
            total += n;
            if (limit < 0 || total < limit) {
                out.write(buf, 0, n);
            } else {
                out.write(buf, 0, n - (int) (total - limit));
                total = limit;
                break;
            }
        }
        return total;
    }

    public static String toString(Reader reader) throws IOException {
        val out = new CharArrayWriter();
        copy(buffered(reader), out, -1, DEFAULT_BUFFER_SIZE);
        return out.toString();
    }

    public static List<String> toLines(Reader reader, boolean skipEmpty) throws IOException {
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

    public static void writeLines(Writer out, Collection<? extends CharSequence> lines) throws IOException {
        writeLines(out, lines, System.lineSeparator());
    }

    public static void writeLines(Writer out, Collection<? extends CharSequence> lines, String lineSeparator) throws IOException {
        val bw = buffered(out);
        int i = 0, end = lines.size();
        for (val line : lines) {
            bw.write(line.toString());
            if (++i != end) {
                bw.write(lineSeparator);
            }
        }
    }
}
