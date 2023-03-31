package org.scope.render;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;
import org.scope.ScopeEngine;
import org.scope.logger.Debug;
import org.scope.util.BufferUtil;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {

    private final Map<String, Integer> uniforms;
    public final int programID;

    private final int vertexShaderID;
    private final int fragmentShaderID;
    
    public ShaderProgram(String vertexCode, String fragmentCode) {
        programID = glCreateProgram();

        if (programID == 0) {
            Debug.log(Debug.LogLevel.FATAL, "Failed to create shader program! OpenGL capabilities may not have been initialized yet!", true);
            ScopeEngine.getInstance().end();
        }

        uniforms = new HashMap<>();

        vertexShaderID = createShader(vertexCode, GL_VERTEX_SHADER);
        fragmentShaderID = createShader(fragmentCode, GL_FRAGMENT_SHADER);
        
        link();
    }
    
    protected int createShader(String shaderCode, int shaderType)  {
        int shaderID = glCreateShader(shaderType);
        if (shaderID == 0) Debug.log(Debug.LogLevel.FATAL,"Error creating shader. Type: " + shaderType);


        glShaderSource(shaderID, shaderCode);
        glCompileShader(shaderID);

        if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == 0) Debug.log(Debug.LogLevel.FATAL,shaderType + " Error compiling Shader code: " + glGetShaderInfoLog(shaderID, 1024));

        return shaderID;
    }

    public void link() {
        glAttachShader(programID, vertexShaderID);
        glAttachShader(programID, fragmentShaderID);

        glLinkProgram(programID);

        glValidateProgram(programID);
        if (glGetProgrami(programID, GL_VALIDATE_STATUS) == 0) Debug.log(Debug.LogLevel.FATAL,"Warning validating Shader code: " + glGetProgramInfoLog(programID, 1024));

        glDeleteShader(vertexShaderID);
        glDeleteShader(fragmentShaderID);
    }

    public void bind() {
        glUseProgram(programID);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void createUniform(String uniformName) {
        if (uniforms.containsKey(uniformName)) { // TODO: Could impact performance? How costly is a containsKey call?
            Debug.log(Debug.LogLevel.WARN, "You're trying to create a uniform you've already created! Uniform Name: " + uniformName);
            return;
        }

        int uniformLocation = GL20.glGetUniformLocation(programID, uniformName);
        if (uniformLocation < 0) {
            Debug.log(Debug.LogLevel.WARN, "Failed to create uniform in shader " + programID + " with uniform name " + uniformName, true);
            return;
        }

        uniforms.put(uniformName, uniformLocation);
    }

    public void cleanup() {
        unbind();
        if (programID != 0) glDeleteProgram(programID);
    }


    public void setBool(String name, boolean value) {
        bind();
        glUniform1i(uniforms.get(name), (value) ? 1 : 0);
    }

    public void setInt(String name, int value) {
        bind();
        glUniform1i(uniforms.get(name), value);
    }

    public void setFloat(String name, float value) {
        bind();
        glUniform1f(uniforms.get(name), value);
    }

    public void setMatrix4f(String name, FloatBuffer buffer) {
        bind();
        glUniformMatrix4fv(uniforms.get(name), false, buffer);
    }

    public void setMatrix4f(String name, Matrix4f matrix) {
        FloatBuffer buffer = BufferUtil.storeDataInFloatBuffer(matrix);

        bind();
        glUniformMatrix4fv(uniforms.get(name), false, buffer);

        BufferUtil.freeMemory(buffer);
    }

    public void setVec3(String name, float v, float v1, float v2) {
        bind();
        glUniform3f(uniforms.get(name), v, v1, v2);
    }

    public void setVec3(String name, Vector3f v) {
        bind();
        glUniform3f(uniforms.get(name), v.x, v.y, v.z);
    }

    public void setVec4(String name, Vector4f v) {
        bind();
        glUniform4f(uniforms.get(name), v.x, v.y, v.z, v.w);
    }
    public void setVec4(String name, float v, float v1, float v2, float v3) {
        bind();
        glUniform4f(uniforms.get(name), v, v1, v2, v3);
    }
}
