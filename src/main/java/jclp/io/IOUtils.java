package jclp.io;

import java.io.*;

import static jclp.StringUtils.isNotEmpty;

public final class IOUtils {
    private IOUtils() {
    }

    public static Reader readerFor(InputStream input) {
        return new InputStreamReader(input);
    }

    public static Reader readerFor(InputStream input, String encoding) throws UnsupportedEncodingException {
        return isNotEmpty(encoding) ? new InputStreamReader(input, encoding) : new InputStreamReader(input);
    }

    public static Writer writerFor(OutputStream output) {
        return new OutputStreamWriter(output);
    }

    public static Writer writerFor(OutputStream output, String encoding) throws UnsupportedEncodingException {
        return isNotEmpty(encoding) ? new OutputStreamWriter(output, encoding) : new OutputStreamWriter(output);
    }

    public static BufferedReader buffered(Reader reader) {
        return reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader);
    }

    public static BufferedWriter buffered(Writer writer) {
        return writer instanceof BufferedWriter ? (BufferedWriter) writer : new BufferedWriter(writer);
    }
}
