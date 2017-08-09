package jclp.util;

import java.util.Collection;
import java.util.Iterator;

import jclp.function.BiFunction;
import jclp.function.Function;
import jclp.function.Predicate;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Sequence<E> {
    @NonNull
    private final Iterator<? extends E> iterator;

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
