package jclp.setting;

import jclp.Keyed;
import lombok.*;

import java.util.List;
import java.util.function.Predicate;

@Getter
@Builder
@ToString
public class Definition implements Keyed<String> {
    @NonNull
    private String key;

    @NonNull
    private Class<?> type;

    private Object defaults;

    private String description;

    private Predicate<Object> constraint;

    @Singular
    private List<Dependency> dependencies;
}
