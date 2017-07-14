package jclp.test;

import static jclp.util.StringUtils.isEmpty;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jclp.io.FileUtils;
import jclp.log.Log;
import lombok.val;


public final class DefinitionFactory {
    private static final String TAG = "DefinitionFactory";

    private DefinitionFactory() {
    }

    public static List<Definition> forJSON(File file) {
        if (file == null) {
            return null;
        }
        try {
            return forJSON(FileUtils.toString(file));
        } catch (IOException e) {
            Log.d(TAG, "cannot read string from file", e);
            return null;
        }
    }

    public static List<Definition> forJSON(String source) {
        if (isEmpty(source)) {
            return null;
        }
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(source);
        } catch (JSONException e) {
            Log.d(TAG, "bad JSON data", e);
            return null;
        }
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
            return null;
        }
        Definition definition;
        try {
            definition = new Definition(key, getType(jsonObject.optString("type")));
        } catch (ClassNotFoundException e) {
            return null;
        }
        definition.setDefaults(jsonObject.optString("default"));
        definition.setDescription(jsonObject.optString("description"));

        Object value = jsonObject.opt("dependency");
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
        System.out.println(definition);
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
