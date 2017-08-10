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

import jclp.vdm.VdmEntry;

import java.util.zip.ZipEntry;

class ZipVdmEntry implements VdmEntry {
    ZipEntry entry;
    ZipVdmReader reader;
    ZipVdmWriter writer;

    ZipVdmEntry(ZipEntry entry, ZipVdmReader reader) {
        this.entry = entry;
        this.reader = reader;
    }

    ZipVdmEntry(ZipEntry entry, ZipVdmWriter writer) {
        this.entry = entry;
        this.writer = writer;
    }

    @Override
    public String getName() {
        return entry.getName();
    }

    @Override
    public String getComment() {
        return entry.getComment();
    }

    @Override
    public long lastModified() {
        return entry.getTime();
    }

    @Override
    public boolean isDirectory() {
        return entry.isDirectory();
    }

    @Override
    public String toString() {
        return reader != null
                ? "zip:file:/" + reader.getName().replace('\\', '/') + "!" + entry
                : entry.toString();
    }

    @Override
    public int hashCode() {
        return entry.hashCode();
    }
}
