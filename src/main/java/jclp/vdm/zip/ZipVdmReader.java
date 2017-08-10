package jclp.vdm.zip;

import jclp.function.Function;
import jclp.util.Sequence;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import jclp.vdm.VdmEntry;
import jclp.vdm.VdmReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static jclp.util.Validate.require;

@RequiredArgsConstructor
public class ZipVdmReader implements VdmReader {
    @NonNull
    private final ZipFile zip;

    @Override
    public String getName() {
        return zip.getName();
    }

    @Override
    public String getComment() {
        return zip.getComment();
    }

    @Override
    public VdmEntry getEntry(@NonNull String name) {
        val entry = zip.getEntry(name);
        return entry != null ? new ZipVdmEntry(entry, this) : null;
    }

    @Override
    public InputStream getInputStream(@NonNull VdmEntry entry) throws IOException {
        require(entry instanceof ZipVdmEntry, "Invalid entry: %s", entry);
        val zve = (ZipVdmEntry) entry;
        require(zve.reader == this, "Invalid entry: %s", entry);
        return zip.getInputStream(zve.entry);
    }

    @Override
    public Iterator<? extends VdmEntry> entries() {
        return new Sequence<>(zip.entries()).map(new Function<ZipEntry, ZipVdmEntry>() {
            @Override
            public ZipVdmEntry apply(ZipEntry entry) {
                return new ZipVdmEntry(entry, ZipVdmReader.this);
            }
        }).getIterator();
    }

    @Override
    public int size() {
        return zip.size();
    }

    @Override
    public void close() throws IOException {
        zip.close();
    }
}
