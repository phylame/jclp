package jclp;

import java.util.Collection;

public interface Attachable<T> {
    void attach(Collection<? extends T> items);

    void detach(Collection<? extends T> items);
}
