package jclp.setting;

import jclp.io.FileUtils;
import jclp.log.Log;
import lombok.val;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static jclp.util.StringUtils.isEmpty;

public final class DefinitionFactory {
    private static final String TAG = "DefinitionFactory";

    private DefinitionFactory() {
    }

    public static List<Definition> forJSON(File file) throws IOException {
        return file != null ? forJSON(FileUtils.toString(file)) : null;
    }

    public static List<Definition> forJSON(String source) {
        if (isEmpty(source)) {
            return null;
        }
        val jsonArray = new JSONArray(source);
        val definitions = new ArrayList<Definition>();
        for (val item : jsonArray) {
            if (!(item instanceof JSONObject)) {
                continue;
            }
            val definition = parseDefinition((JSONObject) item);
            if (definition != null) {
                definitions.add(definition);
            }
        }
        return !definitions.isEmpty() ? definitions : null;
    }

    private static Definition parseDefinition(JSONObject jsonObject) {
        val key = jsonObject.optString("key");
        if (isEmpty(key)) {
            Log.d(TAG, "key is empty");
            return null;
        }
        Definition definition;
        try {
            definition = new Definition(key, getType(jsonObject.optString("type")));
        } catch (ClassNotFoundException e) {
            Log.d(TAG, "not found class for type", e);
            return null;
        }
        definition.setDefaults(jsonObject.optString("default"));
        definition.setDescription(jsonObject.optString("description"));

        val value = jsonObject.opt("dependency");
        Iterable<?> items;
        if (value instanceof String) {
            items = Collections.singleton(value.toString());
        } else if (value instanceof JSONArray) {
            items = (JSONArray) value;
        } else {
            items = Collections.emptyList();
        }
        val dependencies = new ArrayList<Dependency>();
        for (val item : items) {
            if (item == null) {
                continue;
            }
            val dependency = DependencyFactory.forPattern(item.toString());
            if (dependency != null) {
                dependencies.add(dependency);
            }
        }
        definition.setDependencies(!dependencies.isEmpty() ? dependencies : null);
        return definition;
    }

    private static Class<?> getType(String str) throws ClassNotFoundException {
        if (isEmpty(str)) {
            return String.class;
        }
        switch (str) {
            case "str":
                return String.class;
            case "int":
                return Integer.class;
            case "bool":
                return Boolean.class;
            case "real":
                return Number.class;
            case "date":
                return Date.class;
            default:
                return Class.forName(str);
        }
    }
}
