package org.scope.render.shader;

import lombok.Getter;
import org.scope.logger.Debug;

import java.util.HashMap;
import java.util.Map;

public class ShaderManager {
    @Getter private static final Map<String, ShaderProgram> shaders = new HashMap<>();

    public static ShaderProgram getShader(String shaderName) {
        shaderName = shaderName.toLowerCase();
        ShaderProgram shader = shaders.get(shaderName);

        if (shader == null) Debug.log(Debug.LogLevel.ERROR, "Could not find the requested shader of name: " +  shaderName + ". Returning null.");
        return shader;
    }

    public static void addShader(String shaderName, ShaderProgram shader) {
        shaderName = shaderName.toLowerCase();

        if (shaders.containsKey(shaderName)) {
            Debug.log(Debug.LogLevel.ERROR, "Shader with the name " +  shaderName + " already exists! This shader will not be added to the list..");
            return;
        }

        if (shaders.containsValue(shader)) {
            Debug.log(Debug.LogLevel.ERROR, "The shader you're attempting to upload already exists with the name " + shaderName + " already exists with another name! Nothing will be changed.");
            return;
        }

        Debug.log(Debug.LogLevel.INFO, "Adding shader of name " + shaderName);
        shaders.put(shaderName, shader);
    }
}
