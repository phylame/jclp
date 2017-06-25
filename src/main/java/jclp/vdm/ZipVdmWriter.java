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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;

@RequiredArgsConstructor
public class ZipVdmWriter implements VdmWriter {
    @NonNull
    private final ZipOutputStream zip;

    @Override
    public void setComment(@NonNull String comment) {
        zip.setComment(comment);
    }

    @Override
    public OutputStream begin(@NonNull VdmEntry entry) throws IOException {
        zip.putNextEntry(((ZipVdmEntry) entry).entry);
        return zip;
    }

    @Override
    public void end(@NonNull VdmEntry entry) throws IOException {
        zip.flush();
        zip.closeEntry();
    }

    @Override
    public void write(@NonNull VdmEntry entry, @NonNull byte[] data, int off, int len) throws IOException {
        zip.putNextEntry(((ZipVdmEntry) entry).entry);
        zip.write(data, off, len);
        zip.flush();
        zip.closeEntry();
    }

    @Override
    public void write(@NonNull VdmEntry entry, @NonNull byte[] data) throws IOException {
        zip.putNextEntry(((ZipVdmEntry) entry).entry);
        zip.write(data, 0, data.length);
        zip.flush();
        zip.closeEntry();
    }

    @Override
    public void write(@NonNull VdmEntry entry, @NonNull InputStream input) throws IOException {
        zip.putNextEntry(((ZipVdmEntry) entry).entry);
        IOUtils.copy(input, zip, -1);
        zip.flush();
        zip.closeEntry();
    }

    @Override
    public void close() throws IOException {
        zip.close();
    }

    @Override
    public ZipVdmEntry newEntry(@NonNull String name) {
        return new ZipVdmEntry(name);
    }
}
