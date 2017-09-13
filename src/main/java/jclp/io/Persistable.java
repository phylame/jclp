package jclp.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Persistable {
    void load(InputStream input) throws IOException;

    void sync(OutputStream output) throws IOException;
}
