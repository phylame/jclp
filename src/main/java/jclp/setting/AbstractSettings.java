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

import jclp.value.Values;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.val;

import java.util.Map;

import static jclp.CollectionUtils.isEmpty;
import static jclp.CollectionUtils.isNotEmpty;
import static jclp.Validate.require;

@ToString
public abstract class AbstractSettings implements Settings {
    @Setter
    protected Map<String, Definition> definitions;

    protected AbstractSettings(Map<String, Definition> definitions) {
        this.definitions = definitions;
    }

    protected abstract <T> T convertValue(Object value, Class<T> type);

    protected abstract Object handleSet(String key, Object value);

    protected abstract Object handleGet(String key);

    @Override
    public final boolean isEnable(String key) {
        if (isEmpty(definitions)) {
            return true;
        }
        val definition = definitions.get(key);
        if (definition == null) {
            return true;
        }
        val dependencies = definition.getDependencies();
        if (isEmpty(dependencies)) {
            return true;
        }
        for (val dependency : dependencies) {
            if (!isEnable(dependency.getKey()) || !dependency.getCondition().test(get(dependency.getKey()))) {
                return false;
            }
        }
        return true;
    }

    public final Definition getDefinition(String key) {
        return isNotEmpty(definitions) ? definitions.get(key) : null;
    }

    protected final Object getDefault(String key) {
        val definition = getDefinition(key);
        return definition != null ? Values.get(definition.getDefaults()) : null;
    }

    protected final Class<?> getType(String key) {
        val definition = getDefinition(key);
        return definition != null ? definition.getType() : null;
    }

    @Override
    public Object get(String key) {
        val value = handleGet(key);
        return value == null ? getDefault(key) : value;
    }

    @Override
    public <T> T get(String key, @NonNull Class<T> type) {
        val value = handleGet(key);
        if (value == null) {
            return type.cast(getDefault(key));
        } else if (type.isInstance(value)) {
            return type.cast(value);
        } else {
            return convertValue(value, type);
        }
    }

    @Override
    public Object set(String key, @NonNull Object value) {
        val type = getType(key);
        require(type == null || type.isInstance(value), "instance of %s expected", type);
        return handleSet(key, value);
    }

    @Override
    public boolean contains(String key) {
        return handleGet(key) != null;
    }
}
