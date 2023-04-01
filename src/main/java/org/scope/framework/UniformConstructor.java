package org.scope.framework;

import org.scope.render.ShaderProgram;

public interface UniformConstructor {
    void setUniforms(ShaderProgram shader, String uniformName);
}
