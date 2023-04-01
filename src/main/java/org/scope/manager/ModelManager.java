package org.scope.manager;

import lombok.Getter;
import org.scope.ScopeEngine;
import org.scope.logger.Debug;
import org.scope.render.struct.Model;

import java.util.HashMap;
import java.util.Map;

public class ModelManager {
    @Getter private static final Map<String, Model> models = new HashMap<>();

    public static Model getModel(String modelName) {
        modelName = modelName.toLowerCase();
        Model model = models.get(modelName);

        if (model == null) Debug.log(Debug.LogLevel.ERROR, "Could not find the requested model of name: " +  modelName + ". Returning null.");
        return model;
    }

    public static void addModel(String modelName, Model model) {
        modelName = modelName.toLowerCase();

        if (models.containsKey(modelName)) {
            Debug.log(Debug.LogLevel.ERROR, "Model with the name " +  modelName + " already exists! This model will not be added to the list..");
            return;
        }

        if (models.containsValue(model)) {
            Debug.log(Debug.LogLevel.ERROR, "The model you're attempting to upload already exists with the name " + modelName + " already exists with another name! Nothing will be changed.");
            return;
        }

        Debug.log(Debug.LogLevel.INFO, "Adding model of name " + modelName);
        models.put(modelName, model);
    }
}
