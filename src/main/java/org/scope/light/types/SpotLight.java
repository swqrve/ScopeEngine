package org.scope.light.types;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.scope.light.Light;
import org.scope.render.ShaderProgram;

public class SpotLight extends Light {

    @Getter @Setter private float constant;
    @Getter @Setter private float linear;
    @Getter @Setter private float exponent;

    @Getter @Setter private Vector3f coneDirection;
    @Getter @Setter private float cutOff;

    public SpotLight(Vector3f color, Vector3f position, float intensity, float constant, float linear, float exponent, Vector3f coneDirection, float cutOff) {
        super(color, position, intensity);

        this.constant = constant;
        this.linear = linear;
        this.exponent = exponent;

        this.coneDirection = coneDirection;
        this.cutOff = cutOff;
    }

    public SpotLight(Vector3f color, Vector3f position, float intensity, Vector3f coneDirection, float cutOff) {
        this(color, position, intensity, 1.0f, 0.0f, 0.0f, coneDirection, cutOff);
    }

    public SpotLight(SpotLight spotLight) {
        this(spotLight.getColor(), spotLight.getPosition(), spotLight.getIntensity(), spotLight.getConstant(), spotLight.getLinear(), spotLight.getExponent(), spotLight.getConeDirection(), spotLight.getCutOff());
    }

    @Override
    public void setUniforms(ShaderProgram shader, String uniformName) {
        setDefaultUniforms(shader, uniformName);
        shader.setFloat(uniformName + ".att.constant", getConstant());
        shader.setFloat(uniformName + ".att.linear", getLinear());
        shader.setFloat(uniformName + ".att.exponent", getExponent());
        shader.setVec3(uniformName + ".coneDir", getConeDirection());
        shader.setFloat(uniformName + ".cutOff", getCutOff());
    }
}
