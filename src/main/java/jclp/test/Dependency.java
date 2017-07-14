package jclp.test;

import jclp.function.Predicate;
import jclp.util.Keyed;
import lombok.Data;
import lombok.NonNull;


@Data
public class Dependency implements Keyed<String> {
    @NonNull
    private final String key;

    @NonNull
    private final Predicate<Object> condition;
}
