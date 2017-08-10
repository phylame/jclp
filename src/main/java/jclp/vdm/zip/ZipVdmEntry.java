package jclp.vdm.zip;

import jclp.vdm.VdmEntry;

import java.util.zip.ZipEntry;

class ZipVdmEntry implements VdmEntry {
    ZipEntry entry;
    ZipVdmReader reader;
    ZipVdmWriter writer;

    ZipVdmEntry(ZipEntry entry, ZipVdmReader reader) {
        this.entry = entry;
        this.reader = reader;
    }

    ZipVdmEntry(ZipEntry entry, ZipVdmWriter writer) {
        this.entry = entry;
        this.writer = writer;
    }

    @Override
    public String getName() {
        return entry.getName();
    }

    @Override
    public String getComment() {
        return entry.getComment();
    }

    @Override
    public long lastModified() {
        return entry.getTime();
    }

    @Override
    public boolean isDirectory() {
        return entry.isDirectory();
    }

    @Override
    public String toString() {
        return reader != null
                ? "zip:file:/" + reader.getName().replace('\\', '/') + "!" + entry
                : entry.toString();
    }

    @Override
    public int hashCode() {
        return entry.hashCode();
    }
}
