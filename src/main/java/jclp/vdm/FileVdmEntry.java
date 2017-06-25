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

import jclp.util.Validate;
import lombok.NonNull;

import java.io.File;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

public class FileVdmEntry implements VdmEntry {
    static final String COMMENT_FILE = "__FVECOMM__";

    final WeakReference<? extends FileVdmReader> fvr;

    OutputStream stream;

    final File file;

    FileVdmEntry(@NonNull String name) {
        this(new File(name), null);
        Validate.require(!file.isAbsolute(), "Relative path is required: %s", file.getPath());
    }

    FileVdmEntry(@NonNull File file, FileVdmReader fvr) {
        this.file = file;
        this.fvr = fvr != null ? new WeakReference<>(fvr) : null;
    }

    @Override
    public String getName() {
        return file.getName();
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
        return file.getPath();
    }
}
