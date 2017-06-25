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

package jclp.cache;

import jclp.function.Provider;
import lombok.NonNull;
import lombok.val;
import jclp.log.Log;
import jclp.util.Validate;
import jclp.value.Lazy;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FileCache implements Cacheable {
    private static final String TAG = FileCache.class.getSimpleName();
    private static final Charset ENCODING = Charset.forName("UTF-16BE");

    private File cache;
    private volatile boolean closed = false;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lazy<RandomAccessFile> raf = new Lazy<>(new Provider<RandomAccessFile>() {
        @Override
        public RandomAccessFile provide() throws Exception {
            if (cache != null) {
                cache = File.createTempFile("_text_", ".tmp");
            }
            return new RandomAccessFile(cache, "rw");
        }
    });

    public FileCache() {
    }

    public FileCache(File cache) {
        this.cache = cache;
    }

    @Override
    public Object add(@NonNull String text) throws IOException {
        Validate.require(!closed, "closed");
        if (text.isEmpty()) {
            return RangeTag.EMPTY;
        }
        val writeLock = lock.writeLock();
        writeLock.lock();
        try {
            val raf = this.raf.get();
            Validate.checkNotNull(raf, "failed to create cache file");
            val tag = new RangeTag(raf.getFilePointer(), text.length() * 2);
            raf.write(text.getBytes(ENCODING));
            return tag;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public String get(Object tag) throws IOException {
        Validate.require(!closed, "closed");
        if (tag instanceof RangeTag) {
            val rt = (RangeTag) tag;
            if (rt.length == 0) {
                return "";
            }
            val readLock = lock.readLock();
            readLock.lock();
            try {
                val raf = this.raf.get();
                Validate.checkNotNull(raf, "failed to create cache file");
                raf.seek(rt.offset);
                byte[] b = new byte[(int) rt.length];
                raf.readFully(b);
                val str = new String(b, ENCODING);
                b = null;
                return str;
            } finally {
                readLock.unlock();
            }
        }
        return null;
    }

    @Override
    public void close() throws IOException {
        if (raf.isInitialized()) {
            val writeLock = lock.writeLock();
            writeLock.lock();
            try {
                raf.get().close();
                if (!cache.delete()) {
                    Log.e(TAG, "cannot delete cache file: %s", cache);
                }
                closed = true;
            } finally {
                writeLock.unlock();
            }
        }
    }
}
