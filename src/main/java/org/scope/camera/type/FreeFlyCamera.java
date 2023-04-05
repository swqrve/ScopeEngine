package org.scope.camera.type;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.scope.ScopeEngine;
import org.scope.camera.Camera;
import org.scope.manager.InputManager;

import static org.lwjgl.glfw.GLFW.*;

public class FreeFlyCamera extends Camera {

    @Getter private boolean firstMouse;

    @Getter @Setter private float lastX;
    @Getter @Setter private float lastY;

    @Getter @Setter private float sensitivity = 0.10f; // TODO: Add to constructor

    public FreeFlyCamera(float x, float y, float z, float fov) {
        super(x, y, z, fov);

        init();
    }

    public void init() {
        glfwSetInputMode(ScopeEngine.getInstance().getEngineManager().getWindowManager().getWindowID(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        glfwSetCursorPosCallback(ScopeEngine.getInstance().getEngineManager().getWindowManager().getWindowID(), (w, x, y) -> {
            if (firstMouse) {
                setLastX((float) x);
                setLastY((float) y);
                firstMouse = false;
            }

            float xOffSet = (float) (x - getLastX());
            float yOffSet = (float) (getLastY() - y);

            setLastX((float) x);
            setLastY((float) y);

            xOffSet *= getSensitivity();
            yOffSet *= getSensitivity();

            setYaw(getYaw() + xOffSet);
            setPitch(getPitch() + yOffSet);

            if (getPitch() > 89.0f) setPitch(89.0f);
            if (getPitch() < -89.0f) setPitch(-89.0f);
        });
    }

    public void input(InputManager input, double delta) {
        float cameraSpeed = (float) (1.5f * delta);

        if (input.isKeyPressed(GLFW_KEY_W)) getCameraPosition().add(new Vector3f(getCameraFront()).mul(cameraSpeed));
        if (input.isKeyPressed(GLFW_KEY_S)) getCameraPosition().sub(new Vector3f(getCameraFront()).mul(cameraSpeed));
        if (input.isKeyPressed(GLFW_KEY_A)) getCameraPosition().sub(new Vector3f(getCameraFront()).cross(new Vector3f(getCameraUp())).normalize().mul(cameraSpeed));
        if (input.isKeyPressed(GLFW_KEY_D)) getCameraPosition().add(new Vector3f(getCameraFront()).cross(new Vector3f(getCameraUp())).normalize().mul(cameraSpeed));
    }
}
