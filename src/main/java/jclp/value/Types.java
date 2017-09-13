package jclp.value;

import jclp.CollectionUtils;
import jclp.io.ResourceUtils;
import jclp.log.Log;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;

import static jclp.M.M;
import static jclp.StringUtils.isEmpty;
import static jclp.Validate.requireNotEmpty;
import static jclp.Validate.requireNotNull;

public final class Types {
    private Types() {
    }

    public static Set<String> getTypes() {
        return types.keySet();
    }

    public static void setAlias(String id, String... aliases) {
        requireNotEmpty(id, "unknown type %s", id);
        val type = lookup(id);
        requireNotNull(type, "unknown type %s", id);
        Collections.addAll(type.aliases, aliases);
    }

    public static void mapClass(String id, @NonNull Class<?> clazz) {
        requireNotEmpty(id, "type id cannot be null or empty");
        getOrPut(id).clazz = clazz;
        classes.put(clazz, id);
        cache.remove(id);
    }

    public static Class<?> getClass(String id) {
        if (isEmpty(id)) {
            return null;
        }
        val type = lookup(id);
        return type != null ? type.clazz : null;
    }

    public static String getType(@NonNull Object obj) {
        return getType(obj.getClass());
    }

    public static String getType(@NonNull Class<?> clazz) {
        return CollectionUtils.getOrPut(classes, clazz, false, it -> {
            for (val entry : types.entrySet()) {
                if (entry.getValue().clazz == it) {
                    return entry.getKey();
                }
            }
            for (val entry : types.entrySet()) {
                if (entry.getValue().clazz.isAssignableFrom(it)) {
                    return entry.getKey();
                }
            }
            return null;
        });
    }

    public static String getName(String id) {
        if (isEmpty(id)) {
            return null;
        }
        return M.optTr("jclp.type." + id, null);
    }

    public static void setDefault(String id, Object value) {
        requireNotEmpty(id, "unknown type %s", id);
        val type = lookup(id);
        requireNotNull(type, "unknown type %s", id);
        type.value = value;
    }

    public static Object getDefault(String id) {
        if (isEmpty(id)) {
            return null;
        }
        val type = lookup(id);
        if (type == null) {
            return null;
        }
        val value = type.value;
        if (value instanceof Value) {
            return ((Value<?>) value).get();
        } else if (value instanceof Supplier) {
            return ((Supplier<?>) value).get();
        }
        return value;
    }

    private static Type getOrPut(String id) {
        Type type = lookup(id);
        if (type == null) {
            types.put(id, type = new Type(id));
        }
        return type;
    }

    private static Type lookup(String id) {
        Type type = cache.get(id);
        if (type == null) {
            type = types.get(id);
        }
        if (type == null) {
            for (val entry : types.entrySet()) {
                if (entry.getValue().aliases.contains(id)) {
                    cache.put(id, type = entry.getValue());
                    break;
                }
            }
        }
        return type;
    }

    private static void initBuiltins() {
        Properties props = null;
        try {
            props = ResourceUtils.getProperties("!jclp/value/types.properties");
        } catch (IOException e) {
            Log.e("Types", "cannot load types mapping", e);
        }
        if (CollectionUtils.isEmpty(props)) {
            return;
        }
        for (val entry : props.entrySet()) {
            try {
                mapClass(entry.getValue().toString(), Class.forName(entry.getKey().toString()));
            } catch (ClassNotFoundException e) {
                Log.e("Types", "cannot load type class", e);
            }
        }
    }

    private static final HashMap<String, Type> types = new HashMap<>();

    private static final HashMap<String, Type> cache = new HashMap<>();

    private static final IdentityHashMap<Class<?>, String> classes = new IdentityHashMap<>();

    static {
        initBuiltins();
    }

    @RequiredArgsConstructor
    private static class Type {
        private final String id;

        private Class<?> clazz;

        private Object value = null;

        private Set<String> aliases = new HashSet<>();
    }
}
