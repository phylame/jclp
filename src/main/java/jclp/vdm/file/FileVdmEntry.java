package jclp.vdm.file;

import jclp.vdm.VdmEntry;

import java.io.File;
import java.io.OutputStream;

class FileVdmEntry implements VdmEntry {
    File file;
    String name;
    FileVdmReader reader;
    FileVdmWriter writer;
    OutputStream stream;

    FileVdmEntry(File file, String name, FileVdmReader reader) {
        this.file = file;
        this.name = name;
        this.reader = reader;
    }

    FileVdmEntry(File file, String name, FileVdmWriter writer) {
        this.file = file;
        this.name = name;
        this.writer = writer;
    }

    @Override
    public String getName() {
        return name + (isDirectory() ? "/" : "");
    }

    @Override
    public String getComment() {
        return null;
    }

    @Override
    public long lastModified() {
        return file.lastModified();
    }

    @Override
    public boolean isDirectory() {
        return file.isDirectory();
    }

    @Override
    public String toString() {
        return "file:/" + file.getAbsolutePath().replace('\\', '/');
    }
}
