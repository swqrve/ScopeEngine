package org.scope.input;

import lombok.Getter;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.scope.ScopeEngine;

public class InputManager {
    @Getter private static InputManager instance;
     public enum Input {
        RELEASED(0), PRESSED(1), SCROLL_UP(3), SCROLL_DOWN(4);

        private final int value;

        Input(int value) {
            this.value = value;
        }

        public static Input getFromValue(int value) {
            for (Input e : values()) if (e.value == value) return e;
            return null;
        }
    }
    private final int[] keyManager = new int[350];
    private final int[] mouseManager = new int[350];

    private final Vector2d previousMousePosition;
    @Getter private final Vector2d mousePosition;
    @Getter private final Vector2f displacementVector;
    @Getter private boolean mouseInWindow;


    public InputManager() {
        instance = this;

        previousMousePosition = new Vector2d(-1.0D, -1.0D);
        mousePosition = new Vector2d(0.0D, 0.0D);
        displacementVector = new Vector2f();
    }

    public void init() {
        GLFW.glfwSetCursorPosCallback(ScopeEngine.getInstance().getEngineManager().getWindowManager().getWindowID(), (window, xPos, yPos) -> {
            mousePosition.x = xPos;
            mousePosition.y = yPos;
        });

        GLFW.glfwSetCursorEnterCallback(ScopeEngine.getInstance().getEngineManager().getWindowManager().getWindowID(), (window, entered) -> mouseInWindow = entered);

        GLFW.glfwSetMouseButtonCallback(ScopeEngine.getInstance().getEngineManager().getWindowManager().getWindowID(), (window, button, action, mods) -> {
            mouseManager[button] = action;
        });

        GLFW.glfwSetKeyCallback(ScopeEngine.getInstance().getEngineManager().getWindowManager().getWindowID(), (window, key, scancode, action, mods) -> {
            if (action == 2) action = 1;
            keyManager[key] = action;
        });
    }

    public void input() {
        if (previousMousePosition.x > 0 && previousMousePosition.y > 0 && mouseInWindow) {
            double x = mousePosition.x - previousMousePosition.x;
            double y = mousePosition.y - previousMousePosition.y;

            boolean rotateX = x != 0;
            boolean rotateY = y != 0;

            if (rotateX) displacementVector.y = (float) x;
            if (rotateY) displacementVector.x = (float) y;
        }

        previousMousePosition.x = mousePosition.x;
        previousMousePosition.y = mousePosition.y;
    }

    public boolean isKeyPressed(int keyCode) {
        return Input.getFromValue(keyManager[keyCode]) == Input.PRESSED;
    }

    public Input getKey(int keyCode) {
        return Input.getFromValue(keyManager[keyCode]);
    }

    public boolean isMousePressed(int mouseCode) {
        return Input.getFromValue(mouseManager[mouseCode]) == Input.PRESSED;
    }

    public Input getMouseButton(int keyCode) {
        return Input.getFromValue(mouseManager[keyCode]);
    }
}
