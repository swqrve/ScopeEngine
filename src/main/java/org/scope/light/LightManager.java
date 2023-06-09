package org.scope.light;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.scope.light.types.DirectionalLight;
import org.scope.light.types.PointLight;
import org.scope.light.types.SpotLight;
import org.scope.logger.Debug;
import org.scope.render.shader.ShaderProgram;
import org.scope.util.ConstManager;

public class LightManager {
    @Getter private final Vector3f ambientLightColor;
    @Getter @Setter private float specularPower;

    // Only support for 1 directional light at a time...
    @Getter @Setter private DirectionalLight currentDirectionalLight;

    @Getter private final int maxPointLights;
    @Getter private final int maxSpotLights;

    @Getter private final PointLight[] pointLights;
    @Getter private final SpotLight[] spotLights;

    @Getter @Setter private ShaderProgram shader;

    public LightManager(ShaderProgram shader, Vector3f ambientLightColor, float specularPower) {
        this.shader = shader;

        this.ambientLightColor = ambientLightColor;
        this.specularPower = specularPower;

        this.maxPointLights = (int) ConstManager.getConstant("maxPointLights"); // TODO: These don't actually affect the max point or spot lights inside of the shader, they're defaulted to max 5
        this.maxSpotLights = (int) ConstManager.getConstant("maxSpotLights");

        pointLights = new PointLight[maxPointLights];
        spotLights = new SpotLight[maxSpotLights];
    }

    public LightManager(ShaderProgram shader) {
        this(shader, new Vector3f(1.0f, 1.0f, 1.0f), 32.0f);
    }

    public void addLight(Light light) {
        if (light instanceof PointLight) {
            int nextFreeIndex = getNextFreeIndex(pointLights);
            if (nextFreeIndex == pointLights.length) {
                Debug.log(Debug.LogLevel.WARN, "You've tried to add another point light when you're already at the max! It won't be added.");
                return;
            }

            pointLights[nextFreeIndex] = (PointLight) light;
            return;
        }

        if (light instanceof SpotLight) {
            int nextFreeIndex = getNextFreeIndex(spotLights);
            if (nextFreeIndex == spotLights.length) {
                Debug.log(Debug.LogLevel.WARN, "You've tried to add another spot light when you're already at the max! It won't be added.");
                return;
            }

            spotLights[nextFreeIndex] = (SpotLight) light;
            return;
        }

        if (light instanceof DirectionalLight) { // Could probably just use the directionalLight setter but this may look cleaner to just be adding lights via the same function
            currentDirectionalLight = (DirectionalLight) light;
        }
    }

    public int getNextFreeIndex(Light[] lights) {
        for (int i = 0; i < lights.length; i++) if (lights[i] == null) return i;
        return lights.length;
    }

    public void setUniforms() {
        if (currentDirectionalLight != null) currentDirectionalLight.setUniforms(shader, "directionalLight");
        for (int i = 0; i < getNextFreeIndex(pointLights); i++) pointLights[i].setUniforms(shader,  "pointLight[" + i + "]");
        for (int i = 0; i < getNextFreeIndex(spotLights); i++) spotLights[i].setUniforms(shader, "spotLight[" + i + "]");

        shader.setFloat("specularPower", specularPower);
        shader.setVec3("ambientLight", ambientLightColor);
    }
}
