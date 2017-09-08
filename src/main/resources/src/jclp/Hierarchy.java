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

package src.jclp;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.val;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import static src.jclp.Validate.require;
import static lombok.AccessLevel.PROTECTED;

public class Hierarchy<T extends Hierarchy<T>> implements Hierarchical<T> {
    @Getter
    @Setter(PROTECTED)
    private T parent = null;

    private ArrayList<T> children = new ArrayList<>();

    public void append(@NonNull T item) {
        children.add(ensureSolitary(item));
    }

    public void insert(int index, @NonNull T item) {
        children.add(index, ensureSolitary(item));
    }

    @Override
    public int size() {
        return children.size();
    }

    @Override
    public T get(int index) {
        return children.get(index);
    }

    public int indexOf(T item) {
        return item != null && item.getParent() == this
                ? children.indexOf(item)
                : -1;
    }

    public boolean replace(@NonNull T item, @NonNull T target) {
        val index = indexOf(item);
        if (index == -1) {
            return false;
        }
        replaceAt(index, target);
        return true;
    }

    public T replaceAt(int index, @NonNull T item) {
        val current = children.set(index, ensureSolitary(item));
        current.setParent(null);
        return current;
    }

    public boolean remove(@NonNull T item) {
        if (item.getParent() != this) {
            return false;
        }
        if (children.remove(item)) {
            item.setParent(null);
            return true;
        }
        return false;
    }

    public T removeAt(int index) {
        val current = children.remove(index);
        current.setParent(null);
        return current;
    }

    public void swap(int from, int to) {
        Collections.swap(children, from, to);
    }

    public void clear() {
        for (val item : children) {
            item.setParent(null);
        }
        children.clear();
    }

    @Override
    public final Iterator<T> iterator() {
        return children.iterator();
    }

    @SuppressWarnings("unchecked")
    private T ensureSolitary(T item) {
        require(item != this, "Cannot add self to children list: %s", item);
        require(item != parent, "Cannot add parent to children list: %s", item);
        require(item.getParent() == null, "Item has been in certain parent: %s", item);
        item.setParent((T) this);
        return item;
    }
}
