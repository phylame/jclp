package jclp.setting;

import jclp.Keyed;
import lombok.Data;
import lombok.NonNull;

import java.util.function.Predicate;

@Data
public class Dependency implements Keyed<String> {
    @NonNull
    private final String key;

    @NonNull
    private final Predicate<Object> condition;
}
