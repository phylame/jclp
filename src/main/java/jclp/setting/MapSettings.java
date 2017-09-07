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

import jclp.value.Pair;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.Iterator;
import java.util.Map;

@RequiredArgsConstructor
public class MapSettings implements Settings {
    @NonNull
    private final Map<String, Object> map;

    @Override
    public boolean isEnable(String key) {
        return true;
    }

    @Override
    public Object get(String key) {
        return map.get(key);
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        val value = map.get(key);
        return type.isInstance(value) ? type.cast(value) : null;
    }

    @Override
    public boolean contains(String key) {
        return map.containsKey(key);
    }

    @Override
    public Object set(String key, Object value) {
        return map.put(key, value);
    }

    @Override
    public void update(Map<String, ?> values) {
        map.putAll(values);
    }

    @Override
    public Object remove(String key) {
        return map.remove(key);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public Iterator<Pair<String, Object>> iterator() {
        return map.entrySet()
                .stream()
                .map(e -> new Pair<>(e.getKey(), e.getValue()))
                .iterator();
    }

    @Override
    public String toString() {
        return map.toString();
    }
}
