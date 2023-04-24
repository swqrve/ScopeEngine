package org.scope.render.model.struct;

import lombok.Getter;
import lombok.Setter;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.scope.camera.Camera;
import org.scope.framework.Cleanable;
import org.scope.render.model.ModelManager;
import org.scope.render.shader.ShaderProgram;

public abstract class Model implements Cleanable {
    @Getter @Setter private int vaoID;
    @Getter @Setter private int vboID;
    @Getter @Setter private int eboID;

    public Model(String modelName) {
        ModelManager.addModel(modelName, this);
    }

    public abstract int getVerticesSize();
    public void bindVAO() {
        GL30.glBindVertexArray(getVaoID());
    }

    public void render(Camera camera, ShaderProgram shader) {
        shader.bind();

        bindVAO();
        GL30.glDrawArrays(GL11.GL_TRIANGLES, 0, getVerticesSize());
    }

    public void render(Camera camera, ShaderProgram shader, Texture texture) {
        shader.bind();
        texture.bind();

        bindVAO();
        GL30.glDrawArrays(GL11.GL_TRIANGLES, 0, getVerticesSize());
    }
}
