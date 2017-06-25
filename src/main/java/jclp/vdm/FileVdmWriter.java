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

import jclp.io.IOUtils;
import jclp.log.Log;
import jclp.util.Validate;
import lombok.NonNull;
import lombok.val;

import java.io.*;

public class FileVdmWriter implements VdmWriter {
    private static final String TAG = "FileVdmWriter";

    private final File dir;

    public FileVdmWriter(@NonNull File dir) throws IOException {
        this.dir = dir;
        Validate.require(!dir.exists() || dir.isDirectory(), "File must be directory: %s", dir);
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void setComment(@NonNull String comment) {
        try (val out = IOUtils.writerFor(openOutput(new File(dir, FileVdmEntry.COMMENT_FILE)))) {
            out.write(comment);
        } catch (IOException e) {
            Log.e(TAG, "cannot write comment to {}", this);
        }
    }

    private FileOutputStream openOutput(File file) throws IOException {
        if (!dir.mkdirs()) {
            throw new IOException("Cannot create dir " + dir);
        }
        return new FileOutputStream(file);
    }

    @Override
    public OutputStream begin(@NonNull VdmEntry entry) throws IOException {
        val fve = (FileVdmEntry) entry;
        Validate.check(fve.stream == null, "entry is already begin for output stream");
        fve.stream = openOutput(fve.file);
        return fve.stream;
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
        return new FileVdmEntry(name);
    }
}
