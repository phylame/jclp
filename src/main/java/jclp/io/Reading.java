package jclp.io;

import java.io.IOException;

public interface Reading {
    int read(byte[] b, int off, int len) throws IOException;
}
