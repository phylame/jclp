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

package jclp.vdm.file;

import jclp.vdm.VdmEntry;

import java.io.File;
import java.io.OutputStream;

class FileVdmEntry implements VdmEntry {
    File file;
    String name;
    FileVdmReader reader;
    FileVdmWriter writer;
    OutputStream stream;

    FileVdmEntry(File file, String name, FileVdmReader reader) {
        this.file = file;
        this.name = name;
        this.reader = reader;
    }

    FileVdmEntry(File file, String name, FileVdmWriter writer) {
        this.file = file;
        this.name = name;
        this.writer = writer;
    }

    @Override
    public String getName() {
        return name + (isDirectory() ? "/" : "");
    }

    @Override
    public String getComment() {
        return null;
    }

    @Override
    public long lastModified() {
        return file.lastModified();
    }

    @Override
    public boolean isDirectory() {
        return file.isDirectory();
    }

    @Override
    public String toString() {
        return "file:/" + file.getAbsolutePath().replace('\\', '/');
    }
}
