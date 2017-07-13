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

package jclp.setting;

import jclp.cond.Conditions;
import jclp.io.IOUtils;
import jclp.log.Log;
import jclp.util.CollectionMap;
import lombok.NonNull;
import lombok.ToString;
import lombok.val;

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@ToString
public final class Dependencies {
    private static final String TAG = "Dependencies";

    private final Map<String, Dependency> dependencies = new HashMap<>();

    private final CollectionMap<String, String> relations = new CollectionMap<>();

    boolean isEnable(String key, Map<String, String> values) {
        if (dependencies.isEmpty()) {
            return true;
        }
        val dependency = dependencies.get(key);
        return dependency == null || isEnable(dependency.key, values) && dependency.isEnable(values);
    }

    public void add(String key, @NonNull Dependency dep) {
        dependencies.put(key, dep);
        relations.addOne(dep.key, key);
    }

    public void remove(String key) {
        val dependency = dependencies.remove(key);
        if (dependency != null) {
            val keys = relations.get(dependency.key);
            if (keys != null) {
                keys.remove(key);
            }
        }
    }

    public void clear() {
        dependencies.clear();
        relations.clear();
    }

    public Collection<String> relationsOf(String key) {
        return relations.get(key);
    }

    public void load(Reader reader) throws IOException {
        val br = IOUtils.buffered(reader);
        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            int index = line.indexOf("->");
            if (index == -1) {
                Log.t(TAG, "not found '->' in line");
                continue;
            }
            val key = line.substring(0, index);
            line = line.substring(index + 2);
            index = line.indexOf("@");
            if (index == -1) {
                Log.t(TAG, "not found '@' in line");
                continue;
            }
            val condition = Conditions.forPattern(line.substring(index + 1));
            if (condition != null) {
                add(key, new Dependency(line.substring(0, index), condition));
            }
        }
    }
}
