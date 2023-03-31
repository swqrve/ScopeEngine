package org.scope.light.types;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.scope.light.Light;
import org.scope.render.ShaderProgram;

public class PointLight extends Light {
    @Getter @Setter private float constant;
    @Getter @Setter private float linear;
    @Getter @Setter private float exponent;

    public PointLight(Vector3f color, Vector3f position, float intensity, float constant, float linear, float exponent) {
        super(color, position, intensity);

        this.constant = constant;
        this.linear = linear;
        this.exponent = exponent;
    }

    public PointLight(Vector3f color, Vector3f position, float intensity) {
        this(color, position, intensity, 1.0f, 0.0f, 0.0f);
    }

    public PointLight(PointLight light) {
        this(light.getColor(), light.getPosition(), light.getIntensity(), light.getConstant(), light.getLinear(), light.getExponent());
    }

    @Override
    public void createUniforms(ShaderProgram shader, String uniformName) {
        createDefaultUniforms(shader, uniformName);
        shader.createUniform(uniformName + ".att.constant");
        shader.createUniform(uniformName + ".att.linear");
        shader.createUniform(uniformName + ".att.exponent");
    }

    @Override
    public void setUniforms(ShaderProgram shader, String uniformName) {
        setDefaultUniforms(shader, uniformName);
        shader.setFloat(uniformName + ".att.constant", getConstant());
        shader.setFloat(uniformName + ".att.linear", getLinear());
        shader.setFloat(uniformName + ".att.exponent", getExponent());
    }
}
