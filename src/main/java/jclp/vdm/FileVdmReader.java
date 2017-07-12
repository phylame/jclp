/*
 * Copyright 2017 Peng Wan <phylame@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jclp.vdm;

import jclp.function.Consumer;
import jclp.io.FileUtils;
import jclp.util.Validate;
import lombok.NonNull;
import lombok.val;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class FileVdmReader implements VdmReader {
    private final File dir;
    private volatile boolean closed = false;
    private final List<FileInputStream> inputs = new LinkedList<>();

    public FileVdmReader(@NonNull File dir) throws FileNotFoundException {
        this.dir = dir;
        Validate.require(dir.exists() && dir.isDirectory(), "File must be a directory: %s", dir);
    }

    @Override
    public void close() throws IOException {
        if (closed) {
            return;
        }
        synchronized (this) {
            if (closed) {
                return;
            }
            closed = true;
            for (val input : inputs) {
                input.close();
            }
            inputs.clear();
        }
    }

    @Override
    public String getName() {
        return dir.getName();
    }

    @Override
    public String getComment() {
        ensureOpen();
        try {
            return FileUtils.toString(new File(this.dir, FileVdmEntry.COMMENT_FILE));
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public FileVdmEntry entryFor(@NonNull String name) {
        ensureOpen();
        val file = new File(dir, name);
        return file.exists()
                ? new FileVdmEntry(file, this)
                : null;
    }

    @Override
    public InputStream streamFor(@NonNull VdmEntry entry) throws IOException {
        ensureOpen();
        val fileEntry = (FileVdmEntry) entry;
        val input = fileEntry.vdmReader != null && fileEntry.vdmReader.get() == this
                ? new FileInputStream(fileEntry.file)
                : null;
        inputs.add(input);
        return input;
    }

    @Override
    public Iterable<FileVdmEntry> entries() {
        ensureOpen();
        val items = new LinkedList<FileVdmEntry>();
        FileUtils.walkDir(dir, new Consumer<File>() {
            @Override
            public void accept(File file) {
                if (!file.getName().equals(FileVdmEntry.COMMENT_FILE)) {
                    items.add(new FileVdmEntry(file, FileVdmReader.this));
                }
            }
        });
        return items;
    }

    @Override
    public int size() {
        ensureOpen();
        val counter = new FileCounter();
        FileUtils.walkDir(dir, counter);
        return counter.count;
    }

    @Override
    public String toString() {
        return "file://" + dir.getPath();
    }

    private void ensureOpen() {
        Validate.check(!closed, "closed");
    }

    private static class FileCounter implements Consumer<File> {
        private int count = 0;

        @Override
        public void accept(File file) {
            if (!file.getName().equals(FileVdmEntry.COMMENT_FILE)) {
                ++count;
            }
        }
    }
}
