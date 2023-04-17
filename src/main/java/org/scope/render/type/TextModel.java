package org.scope.render.type;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.scope.render.struct.Model;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15C.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.glDeleteVertexArrays;

public class TextModel extends Model {
    public TextModel(String name) {
        super(name);

        setVaoID(GL30.glGenVertexArrays());
        setVboID(GL15.glGenBuffers());

        glBindVertexArray(getVaoID());

        glBindBuffer(GL_ARRAY_BUFFER, getVboID());
        glBufferData(GL_ARRAY_BUFFER, 6 * 4 * Float.BYTES, GL_DYNAMIC_DRAW);

        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 4, GL_FLOAT, false, 4 * Float.BYTES, 0);
    }

    @Override
    public void cleanup() {
        glDeleteVertexArrays(getVaoID());
        glDeleteBuffers(getVboID());
    }

    @Override
    public int getVerticesSize() {
        return 6 * 4 * Float.BYTES;
    }
}
