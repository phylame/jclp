package jclp;

public interface Hierarchical<T extends Hierarchical<T>> extends Iterable<T> {
    int size();

    T getParent();

    T get(int index);
}
