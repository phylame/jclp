package jclp.util;

import jclp.function.BiFunction;
import jclp.function.Function;
import jclp.function.Predicate;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

@RequiredArgsConstructor
public class Sequence<E> {
    @Getter
    @NonNull
    private final Iterator<? extends E> iterator;

    public Sequence(Collection<? extends E> c) {
        this(c.iterator());
    }

    public Sequence(Enumeration<? extends E> e) {
        this(CollectionUtils.iterator(e));
    }

    public <T> Sequence<T> map(@NonNull Function<? super E, ? extends T> transform) {
        return new Sequence<>(CollectionUtils.map(iterator, transform));
    }

    public <T> Sequence<T> mapIndexed(@NonNull BiFunction<? super E, Integer, ? extends T> transform) {
        return new Sequence<>(CollectionUtils.mapIndexed(iterator, transform));
    }

    public Sequence<E> filter(@NonNull Predicate<? super E> filter) {
        return new Sequence<>(CollectionUtils.filter(iterator, filter));
    }

    public void attach(@NonNull Collection<? super E> target) {
        while (iterator.hasNext()) {
            target.add(iterator.next());
        }
    }

    public String join(String separator) {
        return StringUtils.join(separator, iterator);
    }
}
