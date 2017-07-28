package jclp.util;

import java.util.Iterator;

import jclp.function.Function;
import jclp.function.Predicate;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Sequence<E> {
    @Getter
    @NonNull
    private final Iterator<? extends E> iterator;

    public <T> Sequence<T> map(@NonNull Function<? super E, ? extends T> transform) {
        return new Sequence<>(CollectionUtils.map(iterator, transform));
    }

    public Sequence<E> filter(@NonNull Predicate<? super E> filter) {
        return new Sequence<>(CollectionUtils.filter(iterator, filter));
    }

    public String join(String separator) {
        return StringUtils.join(separator, iterator);
    }
}
