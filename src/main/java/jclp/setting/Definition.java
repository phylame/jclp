package jclp.setting;

import jclp.function.Predicate;
import jclp.util.Keyed;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
public class Definition implements Keyed<String> {
    @NonNull
    private final String key;

    @NonNull
    private final Class<?> type;

    private Object defaults;

    private String description;

    private Predicate<Object> constraint;

    private List<Dependency> dependencies;
}
