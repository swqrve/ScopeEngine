package org.scope.light.types;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.scope.light.Light;
import org.scope.render.shader.ShaderProgram;


/*
Dawn:    (-1, 0, 0)
Mid day: (0, 1, 0)
Dusk:    (1, 0, 0)
 */
public class DirectionalLight extends Light {
    @Getter @Setter private Vector3f direction;

    public DirectionalLight(Vector3f color, Vector3f direction, float intensity) {
        super(color, null, intensity);
        this.direction = direction;
    }

    public DirectionalLight(DirectionalLight light) {
        this(light.getColor(), light.getDirection(), light.getIntensity());
    }

    @Override
    public void setUniforms(ShaderProgram shader, String uniformName) {
        setDefaultUniforms(shader, uniformName);
        shader.setVec3(uniformName + ".direction", getDirection());
    }
}
