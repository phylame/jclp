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

import lombok.NonNull;

import java.io.File;
import java.io.OutputStream;

public class FileVdmEntry implements VdmEntry {
    static final String COMMENT_FILE = ".__FVECOMM__";

    final File file;
    final FileVdmReader reader;
    OutputStream stream;

    FileVdmEntry(File file) {
        this(file, null);
    }

    FileVdmEntry(@NonNull File file, FileVdmReader reader) {
        this.file = file;
        this.reader = reader;
    }

    @Override
    public String getName() {
        return file.getPath();
    }

    @Override
    public String getComment() {
        return null;
    }

    @Override
    public boolean isDirectory() {
        return file.isDirectory();
    }

    @Override
    public String toString() {
        return "file:///" + file.getPath();
    }
}
