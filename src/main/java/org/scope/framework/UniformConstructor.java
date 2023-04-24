package org.scope.framework;

import org.scope.render.shader.ShaderProgram;

public interface UniformConstructor {
    void setUniforms(ShaderProgram shader, String uniformName);
}
