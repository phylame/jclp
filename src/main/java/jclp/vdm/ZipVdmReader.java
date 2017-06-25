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

import jclp.function.Function;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static jclp.util.CollectionUtils.*;

@RequiredArgsConstructor
public class ZipVdmReader implements VdmReader {
    @NonNull
    private final ZipFile zip;

    @Override
    public void close() throws IOException {
        zip.close();
    }

    @Override
    public String getName() {
        return zip.getName();
    }

    @Override
    public String getComment() {
        return zip.getComment();
    }

    @Override
    public int size() {
        return zip.size();
    }

    @Override
    public ZipVdmEntry entryFor(@NonNull String name) {
        val ze = zip.getEntry(name);
        return ze != null ? new ZipVdmEntry(ze, zip) : null;
    }

    @Override
    public InputStream streamOf(@NonNull VdmEntry entry) throws IOException {
        return zip.getInputStream(((ZipVdmEntry) entry).entry);
    }

    @Override
    public Iterable<ZipVdmEntry> entries() {
        return iterable(map(iterator(zip.entries()), new Function<ZipEntry, ZipVdmEntry>() {
            @Override
            public ZipVdmEntry apply(ZipEntry i) {
                return new ZipVdmEntry(i, zip);
            }
        }));
    }

    @Override
    public String toString() {
        return "zip://" + zip.getName();
    }
}
