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

package jclp.vdm.zip;

import jclp.function.Function;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import jclp.vdm.VdmEntry;
import jclp.vdm.VdmReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static jclp.Validate.require;

@RequiredArgsConstructor
public class ZipVdmReader implements VdmReader {
    @NonNull
    private final ZipFile zip;

    @Override
    public String getName() {
        return zip.getName();
    }

    @Override
    public String getComment() {
        return zip.getComment();
    }

    @Override
    public VdmEntry getEntry(@NonNull String name) {
        val entry = zip.getEntry(name);
        return entry != null ? new ZipVdmEntry(entry, this) : null;
    }

    @Override
    public InputStream getInputStream(@NonNull VdmEntry entry) throws IOException {
        require(entry instanceof ZipVdmEntry, "Invalid entry: %s", entry);
        val zve = (ZipVdmEntry) entry;
        require(zve.reader == this, "Invalid entry: %s", entry);
        return zip.getInputStream(zve.entry);
    }

    @Override
    public Iterator<? extends VdmEntry> entries() {
        return new Sequence<>(zip.entries()).map(new Function<ZipEntry, ZipVdmEntry>() {
            @Override
            public ZipVdmEntry apply(ZipEntry entry) {
                return new ZipVdmEntry(entry, ZipVdmReader.this);
            }
        }).getIterator();
    }

    @Override
    public int size() {
        return zip.size();
    }

    @Override
    public void close() throws IOException {
        zip.close();
    }
}
