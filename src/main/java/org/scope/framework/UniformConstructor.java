package org.scope.framework;

import org.scope.render.ShaderProgram;

public interface UniformConstructor {
    void createUniforms(ShaderProgram shader, String uniformName);
    void setUniforms(ShaderProgram shader, String uniformName);
}
