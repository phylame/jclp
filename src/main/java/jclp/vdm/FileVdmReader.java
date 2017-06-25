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

public class FileVdmReader implements VdmReader {
    private final File dir;

    public FileVdmReader(@NonNull File dir) throws FileNotFoundException {
        this.dir = dir;
        Validate.require(dir.exists() && dir.isDirectory(), "File must be a directory: %s", dir);
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public String getName() {
        return dir.getName();
    }

    @Override
    public String getComment() {
        try {
            return FileUtils.toString(new File(this.dir, FileVdmEntry.COMMENT_FILE));
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public FileVdmEntry entryFor(@NonNull String name) {
        val file = new File(dir, name);
        return file.exists() ? new FileVdmEntry(file, this) : null;
    }

    @Override
    public InputStream streamOf(@NonNull VdmEntry entry) throws IOException {
        val fve = (FileVdmEntry) entry;
        if (fve.fvr == null || fve.fvr.get() != this) {
            return null;
        }
        return new FileInputStream(fve.file);
    }

    @Override
    public Iterable<FileVdmEntry> entries() {
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
        val counter = new FileCounter();
        FileUtils.walkDir(dir, counter);
        return counter.count;
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

    @Override
    public String toString() {
        return "file://" + dir.getPath();
    }
}
