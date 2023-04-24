package org.scope.render.model.type;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.scope.camera.Camera;
import org.scope.render.model.struct.Model;
import org.scope.render.model.struct.Texture;
import org.scope.render.shader.ShaderProgram;
import org.scope.util.BufferUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class SDQuad extends Model {
    private final float[] vertices = {
            // pos      // tex
            0.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 0.0f,

            0.0f, 1.0f, 0.0f, 1.0f,
            1.0f, 1.0f, 1.0f, 1.0f,
            1.0f, 0.0f, 1.0f, 0.0f
    };

    public SDQuad() {
        super("sdquad");
        setVaoID(GL30.glGenVertexArrays());
        setVboID(GL15.glGenBuffers());

        FloatBuffer verticesBuffer = BufferUtil.storeDataInFloatBuffer(vertices);

        GL30.glBindVertexArray(getVaoID());

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, getVboID());
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);

        GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, 4 * Float.BYTES, 0);
        GL20.glEnableVertexAttribArray(0);

        BufferUtil.freeMemory(verticesBuffer);
    }

    @Override
    public int getVerticesSize() {
        return vertices.length;
    }

    @Override
    public void cleanup() {
        GL30.glDeleteVertexArrays(getVaoID());
        GL15.glDeleteBuffers(getVboID());
    }
}
