package jclp.vdm.file;

import jclp.function.Consumer;
import jclp.io.IOUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import jclp.vdm.VdmEntry;
import jclp.vdm.VdmReader;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static jclp.util.Validate.require;

@RequiredArgsConstructor
public class FileVdmReader implements VdmReader {
    @NonNull
    private final File dir;

    private final List<InputStream> streams = new LinkedList<>();

    @Override
    public String getName() {
        return dir.getPath();
    }

    @Override
    public String getComment() {
        return null;
    }

    @Override
    public VdmEntry getEntry(@NonNull String name) {
        val file = new File(dir, name);
        return file.exists() ? new FileVdmEntry(file, name, this) : null;
    }

    @Override
    public InputStream getInputStream(@NonNull VdmEntry entry) throws IOException {
        require(entry instanceof FileVdmEntry, "Invalid entry: %s", entry);
        val fve = (FileVdmEntry) entry;
        require(fve.reader == this, "Invalid entry: %s", entry);
        val stream = new FileInputStream(fve.file);
        synchronized (stream) {
            streams.add(stream);
        }
        return stream;
    }

    private int walkDirectory(File dir, Consumer<File> action) {
        val items = dir.list();
        if (items == null || items.length == 0) {
            return 0;
        }
        for (val item : items) {
            val file = new File(dir, item);
            if (file.isDirectory() && walkDirectory(file, action) != 0) {
                continue;
            }
            action.accept(file);
        }
        return items.length;
    }

    @Override
    public Iterator<? extends VdmEntry> entries() {
        val start = dir.getAbsolutePath().length() + 1;
        val items = new LinkedList<VdmEntry>();
        walkDirectory(dir, new Consumer<File>() {
            @Override
            public void accept(File file) {
                items.add(new FileVdmEntry(file, file.getAbsolutePath().substring(start), FileVdmReader.this));
            }
        });
        return items.iterator();
    }

    @Override
    public int size() {
        val counter = new FileCounter();
        walkDirectory(dir, counter);
        return counter.count;
    }

    @Override
    public void close() throws IOException {
        synchronized (streams) {
            for (val stream : streams) {
                IOUtils.closeQuietly(stream);
            }
            streams.clear();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }

    private static class FileCounter implements Consumer<File> {
        private int count = 0;

        @Override
        public void accept(File file) {
            ++count;
        }
    }
}
