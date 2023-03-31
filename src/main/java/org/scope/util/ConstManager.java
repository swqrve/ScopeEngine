package org.scope.util;

import org.scope.ScopeEngine;
import org.scope.logger.Debug;

import java.util.HashMap;
import java.util.Map;

public class ConstManager {
    private static final Map<String, Object> constantsMap = new HashMap<>();

    public static void createConstant(String name, Object value) {
        constantsMap.put(name, value);
    }

    public static Object getConstant(String name) {
        if (!constantsMap.containsKey(name)) {
            Debug.log("Const Manager: ", Debug.LogLevel.FATAL, "Failed to find the requested const... Name: " + name);
            ScopeEngine.getInstance().end();

            return 0;
        }

        return constantsMap.get(name);
    }

    public void editConstant(String name, Object value) {
        if (!constantsMap.containsKey(name)) {
            Debug.log("Const Manager: ", Debug.LogLevel.WARN, " Edit constant called on a constant that doesn't exist! Creating a new constant instead... Name: " + name);
            createConstant(name, value);

            return;
        }

        constantsMap.replace(name, value);
    }
}
