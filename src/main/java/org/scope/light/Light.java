package org.scope.light;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.scope.framework.UniformConstructor;
import org.scope.render.ShaderProgram;

public abstract class Light implements UniformConstructor {
    @Getter @Setter private Vector3f color;
    @Getter @Setter private Vector3f position;
    @Getter @Setter private float intensity;

    public Light(Vector3f color, Vector3f position, float intensity) {
        this.color = color;
        this.position = position;

        this.intensity = intensity;
    }

    // if (getPosition() != null) This exists because directional lights don't have a position, could act like it's the direction if I want a cleaner code base but eh

    public void setDefaultUniforms(ShaderProgram shader, String uniformName) {
        shader.setVec3(uniformName + ".color", getColor());
        if (getPosition() != null) shader.setVec3(uniformName + ".position", getPosition());
        shader.setFloat(uniformName + ".intensity", getIntensity());
    }
}
