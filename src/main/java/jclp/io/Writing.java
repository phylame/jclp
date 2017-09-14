package jclp.io;

import java.io.IOException;

public interface Writing {
    void write(byte[] b, int off, int len) throws IOException;
}
