package jclp.vdm.zip;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import jclp.vdm.VdmEntry;
import jclp.vdm.VdmWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static jclp.util.Validate.require;

@RequiredArgsConstructor
public class ZipVdmWriter implements VdmWriter {
    @NonNull
    private final ZipOutputStream zip;

    @Override
    public void setComment(String comment) {
        zip.setComment(comment);
    }

    @Override
    public void setProperty(String name, Object value) {
        switch (name) {
        case "method":
            zip.setMethod(Integer.parseInt(value.toString()));
        break;
        case "level":
            zip.setLevel(Integer.parseInt(value.toString()));
        break;
        }
    }

    @Override
    public VdmEntry newEntry(@NonNull String name) {
        return new ZipVdmEntry(new ZipEntry(name), this);
    }

    @Override
    public OutputStream putEntry(VdmEntry entry) throws IOException {
        require(entry instanceof ZipVdmEntry, "Invalid entry: %s", entry);
        val zve = (ZipVdmEntry) entry;
        require(zve.writer == this, "Invalid entry: %s", entry);
        zip.putNextEntry(zve.entry);
        return zip;
    }

    @Override
    public void closeEntry(VdmEntry entry) throws IOException {
        require(entry instanceof ZipVdmEntry, "Invalid entry: %s", entry);
        val zve = (ZipVdmEntry) entry;
        require(zve.writer == this, "Invalid entry: %s", entry);
        zip.closeEntry();
    }

    @Override
    public void write(VdmEntry entry, byte[] b) throws IOException {
        write(entry, b, 0, b.length);
    }

    @Override
    public void write(VdmEntry entry, byte[] b, int off, int len) throws IOException {
        putEntry(entry).write(b, off, len);
        closeEntry(entry);
    }

    @Override
    public void close() throws IOException {
        zip.close();
    }
}
