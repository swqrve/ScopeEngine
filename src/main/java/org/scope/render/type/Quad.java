package org.scope.render.type;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.scope.camera.Camera;
import org.scope.render.struct.Model;
import org.scope.render.ShaderProgram;
import org.scope.render.Texture;
import org.scope.util.BufferUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;


// Todo: add a model manager where you can grab models by name? That way you don't have to manage model locations and no risk of them being lost
public class Quad extends Model {
    private final float[] vertices = {
             // positions       normals           texture coords
             0.5f,  0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f,   // top right
             0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f,   // bottom right
            -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f,   // bottom left
            -0.5f,  0.5f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f    // top left
    };

    private final int[] indices = {
            3, 2, 1,
            3, 1, 0
    };

    public Quad() {
        super("quad");
        setVaoID(GL30.glGenVertexArrays());
        setVboID(GL15.glGenBuffers());
        setEboID(GL15.glGenBuffers());

        FloatBuffer verticesBuffer = BufferUtil.storeDataInFloatBuffer(vertices);
        IntBuffer indicesBuffer = BufferUtil.storeDataInIntBuffer(indices);

        GL30.glBindVertexArray(getVaoID());

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, getVboID());
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, getEboID());
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);

        GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 8 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);

        GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 8 * Float.BYTES, 3L * Float.BYTES);
        GL20.glEnableVertexAttribArray(1);

        GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 8 * Float.BYTES, 6L * Float.BYTES);
        GL20.glEnableVertexAttribArray(2);

        BufferUtil.freeMemory(verticesBuffer);
        BufferUtil.freeMemory(indicesBuffer);
    }

    @Override
    public void render(Camera camera, ShaderProgram shader) {
        shader.bind();

        shader.setMatrix4f("view", camera.getViewMatrix());

        bindVAO();
        GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0);
    }

    @Override
    public void render(Camera camera, ShaderProgram shader, Texture texture) {
        shader.bind();
        texture.bind();

        shader.setMatrix4f("view", camera.getViewMatrix());

        bindVAO();
        GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0);
    }

    @Override
    public int getVerticesSize() {
        return vertices.length;
    }

    @Override
    public void cleanup() {
        GL30.glDeleteVertexArrays(getVaoID());
        GL15.glDeleteBuffers(getVboID());
        GL15.glDeleteBuffers(getEboID());
    }
}
