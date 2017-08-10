package jclp.vdm;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

public interface VdmWriter extends Closeable {
    void setComment(String comment);

    void setProperty(String name, Object value);

    VdmEntry newEntry(String name);

    OutputStream putEntry(VdmEntry entry) throws IOException;

    void closeEntry(VdmEntry entry) throws IOException;

    void write(VdmEntry entry, byte[] b) throws IOException;

    void write(VdmEntry entry, byte[] b, int off, int len) throws IOException;
}
