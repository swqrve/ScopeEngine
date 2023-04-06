package org.scope.render.type;

import lombok.Getter;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryUtil;
import org.scope.ScopeEngine;
import org.scope.camera.Camera;
import org.scope.logger.Debug;
import org.scope.render.ShaderProgram;
import org.scope.render.Texture;
import org.scope.render.struct.Model;
import org.scope.util.BufferUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11C.GL_RED;
import static org.lwjgl.opengl.GL11C.GL_RGB;
import static org.lwjgl.opengl.GL11C.GL_RGBA;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11C.glBindTexture;
import static org.lwjgl.opengl.GL11C.glGenTextures;
import static org.lwjgl.opengl.GL11C.glTexImage2D;
import static org.lwjgl.opengl.GL11C.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL15C.glDeleteBuffers;
import static org.lwjgl.opengl.GL30C.glDeleteVertexArrays;
import static org.lwjgl.stb.STBImage.stbi_load;

public class SkyBox extends Model {
    private final String[] faces = { "right", "left", "top", "bottom", "front", "back" };
    @Getter private final ShaderProgram skyboxShader;
    private final float[] vertices = {
            // Back face
            -0.5f, -0.5f, -0.5f,
             0.5f, -0.5f, -0.5f,
             0.5f,  0.5f, -0.5f,
             0.5f,  0.5f, -0.5f,
            -0.5f,  0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
            // Front face
            -0.5f, -0.5f,  0.5f,
             0.5f,  0.5f,  0.5f,
             0.5f, -0.5f,  0.5f,
             0.5f,  0.5f,  0.5f,
            -0.5f, -0.5f,  0.5f,
            -0.5f,  0.5f,  0.5f,
            // Left face
            -0.5f,  0.5f,  0.5f,
            -0.5f, -0.5f, -0.5f,
            -0.5f,  0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
            -0.5f,  0.5f,  0.5f,
            -0.5f, -0.5f,  0.5f,
            // Right face
             0.5f,  0.5f,  0.5f,
             0.5f,  0.5f, -0.5f,
             0.5f, -0.5f, -0.5f,
             0.5f, -0.5f, -0.5f,
             0.5f, -0.5f,  0.5f,
             0.5f,  0.5f,  0.5f,
            // Bottom face
            -0.5f, -0.5f, -0.5f,
             0.5f, -0.5f,  0.5f,
             0.5f, -0.5f, -0.5f,
             0.5f, -0.5f,  0.5f,
            -0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f,  0.5f,
            // Top face
            -0.5f,  0.5f, -0.5f,
             0.5f,  0.5f, -0.5f,
             0.5f,  0.5f,  0.5f,
             0.5f,  0.5f,  0.5f,
            -0.5f,  0.5f,  0.5f,
            -0.5f,  0.5f, -0.5f,
    };

    private int textureID;

    private Matrix4f positionMatrix = new Matrix4f();

    public SkyBox(ShaderProgram skyboxShader, String directory) {
        super("skybox");
        this.skyboxShader = skyboxShader;

        setVaoID(GL30.glGenVertexArrays());
        setVboID(GL15.glGenBuffers());

        FloatBuffer verticesBuffer = BufferUtil.storeDataInFloatBuffer(vertices);

        GL30.glBindVertexArray(getVaoID());

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, getVboID());
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);

        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 3 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);

        BufferUtil.freeMemory(verticesBuffer);

        loadCubeMap(directory);

        skyboxShader.bind();
        skyboxShader.setInt("skybox", 0);
    }

    public void render(Camera camera) {
        glDepthFunc(GL_LEQUAL);
        glDisable(GL_CULL_FACE);
        skyboxShader.bind();

        positionMatrix.set(camera.getViewMatrix());
        positionMatrix.setTranslation(0, 0, 0);

        skyboxShader.setMatrix4f("view", positionMatrix);
        skyboxShader.setMatrix4f("projection", camera.getCameraProjection());

        bindVAO();

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureID);

        GL30.glDrawArrays(GL11.GL_TRIANGLES, 0, getVerticesSize());

        glDepthFunc(GL_LESS);
    }

    public void render(Camera camera, ShaderProgram shader) {
        Debug.log(Debug.LogLevel.INFO, "You attempted to pass a shader in the render call for the skybox! The skybox will always use the shader it was created with by default. There is currently no override functionality", true);
        render(camera);
    }

    private void loadCubeMap(String directory) { // TODO: Should have texture class handle all the skybox texture loading for cube maps (cause it can be used for other stuff too I think) but for now this implementation is fine
        textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_CUBE_MAP, textureID);

        for (int i = 0; i < faces.length; i++) {
            IntBuffer width = MemoryUtil.memAllocInt(1);
            IntBuffer height = MemoryUtil.memAllocInt(1);
            IntBuffer nrChannels = MemoryUtil.memAllocInt(1);

            String fileLocation = directory + "/" + faces[i] + ".png";
            STBImage.stbi_set_flip_vertically_on_load(false);

            ByteBuffer data = null;
            if (Texture.class.getClassLoader().getResource(fileLocation) != null) data = stbi_load(Texture.class.getClassLoader().getResource(fileLocation).getPath().substring(1), width, height, nrChannels, 0);

            if (data == null) {
                Debug.log(Debug.LogLevel.FATAL, "Failed to load texture " + fileLocation + ". There is no fall back skybox! Crashing!", true);
                ScopeEngine.getInstance().end();
                return;
            }

            int format = GL_RGBA;

            int value = nrChannels.get();
            if (value == 1) format = GL_RED;
            if (value == 3) format = GL_RGB;

            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, format, width.get(), height.get(), 0, format, GL_UNSIGNED_BYTE, data);
            STBImage.stbi_image_free(data);

            BufferUtil.freeMemory(width);
            BufferUtil.freeMemory(height);
            BufferUtil.freeMemory(nrChannels);
        }

        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);

        Debug.log(Debug.LogLevel.INFO, "Skybox Texture in directory " + directory  + " successfully.");
    }


    @Override
    public int getVerticesSize() {
        return vertices.length;
    }

    @Override
    public void cleanup() {
        glDeleteVertexArrays(getVaoID());
        glDeleteBuffers(getVboID());
        glDeleteTextures(textureID);
    }

}
