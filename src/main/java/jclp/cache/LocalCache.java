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

import lombok.NonNull;
import lombok.val;
import jclp.util.Validate;

import java.io.IOException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LocalCache implements Cacheable {
    private volatile StringBuilder b = new StringBuilder();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public Object add(@NonNull String text) {
        Validate.checkNotNull(b, "closed");
        if (text.isEmpty()) {
            return RangeTag.EMPTY;
        }
        val writeLock = lock.writeLock();
        writeLock.lock();
        try {
            val tag = new RangeTag(b.length(), text.length());
            b.append(text);
            return tag;
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public String get(Object tag) {
        Validate.checkNotNull(b, "closed");
        if (tag instanceof RangeTag) {
            val rt = (RangeTag) tag;
            if (rt.length == 0) {
                return "";
            }
            val readLock = lock.readLock();
            readLock.lock();
            try {
                return b.substring((int) rt.offset, (int) rt.length);
            } finally {
                readLock.unlock();
            }
        }
        return null;
    }

    @Override
    public void close() throws IOException {
        val writeLock = lock.writeLock();
        writeLock.lock();
        try {
            b = null;
        } finally {
            writeLock.unlock();
        }
    }
}
