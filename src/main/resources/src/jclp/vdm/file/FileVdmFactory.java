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

import src.jclp.CollectionUtils;
import lombok.NonNull;
import src.jclp.vdm.VdmFactory;
import src.jclp.vdm.VdmReader;
import src.jclp.vdm.VdmWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

public class FileVdmFactory implements VdmFactory {
    @Override
    public String getName() {
        return "FileVDM";
    }

    @Override
    public Set<String> getNames() {
        return CollectionUtils.setOf("dir");
    }

    private File getDirectory(Object input, boolean reading) throws IOException {
        File dir;
        if (input instanceof String) {
            dir = new File((String) input);
        } else if (input instanceof File) {
            dir = (File) input;
        } else {
            throw new IllegalArgumentException(input.toString());
        }
        if (!dir.exists()) {
            if (reading) {
                throw new FileNotFoundException(dir.getAbsolutePath());
            } else if (!dir.mkdirs()) {
                throw new IOException("Cannot create directory: " + dir);
            }
        }
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("Not a directory: " + dir);
        }
        return dir;
    }

    @Override
    public VdmReader getReader(@NonNull Object input) throws IOException {
        return new FileVdmReader(getDirectory(input, true));
    }

    @Override
    public VdmWriter getWriter(@NonNull Object output) throws IOException {
        return new FileVdmWriter(getDirectory(output, false));
    }
}
