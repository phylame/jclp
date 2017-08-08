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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import jclp.io.IOUtils;
import jclp.util.Validate;
import lombok.NonNull;
import lombok.val;

public class FileVdmWriter implements VdmWriter {
    private final File dir;
    private final List<FileOutputStream> outputs = new LinkedList<>();

    public FileVdmWriter(@NonNull File dir) throws IOException {
        this.dir = dir;
        Validate.require(!dir.exists() || dir.isDirectory(), "File must be directory: %s", dir);
    }

    @Override
    public void close() throws IOException {
        for (val output : outputs) {
            output.close();
        }
        outputs.clear();
    }

    @Override
    public void setComment(String comment) {
    }

    private FileOutputStream openOutput(File file) throws IOException {
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Cannot create dir " + dir);
        }
        val parent = file.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IOException("Cannot create dir " + parent);
        }
        val output = new FileOutputStream(file);
        outputs.add(output);
        return output;
    }

    @Override
    public OutputStream begin(@NonNull VdmEntry entry) throws IOException {
        val fileEntry = (FileVdmEntry) entry;
        Validate.check(fileEntry.stream == null, "entry is already begin for output stream");
        fileEntry.stream = openOutput(fileEntry.file);
        return fileEntry.stream;
    }

    @Override
    public void end(@NonNull VdmEntry entry) throws IOException {
        val stream = ((FileVdmEntry) entry).stream;
        Validate.checkNotNull(stream, "entry is not ready for output stream");
        stream.flush();
        stream.close();
    }

    @Override
    public void write(@NonNull VdmEntry entry, @NonNull byte[] data) throws IOException {
        write(entry, data, 0, data.length);
    }

    @Override
    public void write(@NonNull VdmEntry entry, @NonNull byte[] data, int off, int len) throws IOException {
        try (val out = openOutput(((FileVdmEntry) entry).file)) {
            out.write(data, off, len);
        }
    }

    @Override
    public void write(VdmEntry entry, InputStream input) throws IOException {
        try (val out = openOutput(((FileVdmEntry) entry).file)) {
            IOUtils.copy(input, out, -1);
        }
    }

    @Override
    public FileVdmEntry newEntry(@NonNull String name) {
        return new FileVdmEntry(new File(dir, name));
    }
}
