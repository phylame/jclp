package jclp.vdm;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public interface VdmReader extends Closeable {
    String getName();

    String getComment();

    VdmEntry getEntry(String name);

    InputStream getInputStream(VdmEntry entry) throws IOException;

    Iterator<? extends VdmEntry> entries();

    int size();
}
