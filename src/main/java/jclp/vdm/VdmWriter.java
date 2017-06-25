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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface VdmWriter extends Closeable {
    void setComment(String comment);

    VdmEntry newEntry(String name);

    OutputStream begin(VdmEntry entry) throws IOException;

    void end(VdmEntry entry) throws IOException;

    void write(VdmEntry entry, byte[] data) throws IOException;

    void write(VdmEntry entry, byte[] data, int off, int len) throws IOException;

    void write(VdmEntry entry, InputStream input) throws IOException;
}
