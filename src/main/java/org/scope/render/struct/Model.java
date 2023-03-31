package org.scope.render.struct;

import lombok.Getter;
import lombok.Setter;
import org.lwjgl.opengl.*;
import org.scope.camera.Camera;
import org.scope.framework.Cleanable;
import org.scope.manager.ModelManager;
import org.scope.render.ShaderProgram;
import org.scope.render.Texture;

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

        shader.setMatrix4f("view", camera.getViewMatrix());

        bindVAO();
        GL30.glDrawArrays(GL11.GL_TRIANGLES, 0, getVerticesSize());
    }

    public void render(Camera camera, ShaderProgram shader, Texture texture) {
        shader.bind();
        texture.bind();

        shader.setMatrix4f("view", camera.getViewMatrix());

        bindVAO();
        GL30.glDrawArrays(GL11.GL_TRIANGLES, 0, getVerticesSize());
    }
}
