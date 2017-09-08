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

package src.jclp.vdm.file;

import src.jclp.io.IOUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import src.jclp.vdm.VdmEntry;
import src.jclp.vdm.VdmWriter;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

import static src.jclp.Validate.check;
import static src.jclp.Validate.checkNotNull;
import static src.jclp.Validate.require;

@RequiredArgsConstructor
public class FileVdmWriter implements VdmWriter {
    @NonNull
    private final File dir;

    private final List<OutputStream> streams = new LinkedList<>();

    @Override
    public void setComment(String comment) {
    }

    @Override
    public void setProperty(String name, Object value) {
    }

    @Override
    public VdmEntry newEntry(@NonNull String name) {
        return new FileVdmEntry(new File(dir, name), name, this);
    }

    private FileOutputStream openOutput(File file) throws IOException {
        val parent = file.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IOException("Cannot create dir: " + parent);
        }
        val output = new FileOutputStream(file);
        synchronized (streams) {
            streams.add(output);
        }
        return output;
    }

    @Override
    public OutputStream putEntry(VdmEntry entry) throws IOException {
        require(entry instanceof FileVdmEntry, "Invalid entry: %s", entry);
        val fve = (FileVdmEntry) entry;
        require(fve.writer == this, "Invalid entry: %s", entry);
        check(fve.stream == null, "entry is already put");
        fve.stream = openOutput(fve.file);
        return fve.stream;
    }

    @Override
    public void closeEntry(VdmEntry entry) throws IOException {
        require(entry instanceof FileVdmEntry, "Invalid entry: %s", entry);
        val fve = (FileVdmEntry) entry;
        require(fve.writer == this, "Invalid entry: %s", entry);
        val stream = fve.stream;
        checkNotNull(stream, "entry is not put");
        stream.flush();
        stream.close();
        streams.remove(stream);
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
        synchronized (streams) {
            for (val stream : streams) {
                IOUtils.closeQuietly(stream);
            }
            streams.clear();
        }
    }
}
