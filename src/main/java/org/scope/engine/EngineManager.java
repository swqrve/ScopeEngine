package org.scope.engine;

import lombok.Getter;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11;
import org.scope.ScopeEngine;
import org.scope.camera.Camera;
import org.scope.input.InputManager;
import org.scope.logger.Debug;
import org.scope.render.Texture;
import org.scope.render.text.TextManager;
import org.scope.sound.SoundManager;
import org.scope.util.ConstManager;
import org.scope.util.EnginePreferences;
import org.scope.window.WindowManager;

import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.opengl.GL30C.glGenVertexArrays;

public class EngineManager {

    @Getter private WindowManager windowManager;
    @Getter private InputManager inputManager;
    @Getter private SoundManager soundManager;

    @Getter private boolean running;

    @Getter private int fps;
    private final float frameTime;

    private GLFWErrorCallback errorCallback;

    public EngineManager() {
        ConstManager.createConstant("zNear", 0.1f);
        ConstManager.createConstant("zFar", 100.0f);
        ConstManager.createConstant("Nanosecond", 1000000000D);
        ConstManager.createConstant("FrameRate", 5000.0f);

        frameTime = 1.0f / (float) ConstManager.getConstant("FrameRate");
    }

    public void init(EnginePreferences preferences) {
        GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

        // Initialize all Engine Utilities.... Input manager, Rendering manager, Sound manager, etc.
        windowManager = new WindowManager(preferences.getWidth(), preferences.getHeight(), preferences.getTitle(), preferences.isResizable());
        windowManager.init(); // The window is already visible once this runs!

        inputManager = new InputManager();
        inputManager.init();

        soundManager = new SoundManager();
        soundManager.init(null); // TODO: Grab default sound device options from preferences

        new TextManager();

        // Initialize our "null" texture or "error" texture
        Texture.setErrorTexture(new Texture("textures/error.png"));

        ScopeEngine.getInstance().getCurrentScene().init();

        run();
    }

    public void run() {
        if (running) {
            Debug.setScopeName("ScopeEngine");
            Debug.log(Debug.LogLevel.FATAL, "Something attempted to run the main loop when the engine is already running, what are you doing?", true);
            Debug.resetScope();
            return;
        }

        running = true;

        int frames = 0;
        long frameCounter = 0;

        long lastTime = System.nanoTime();
        double unprocessedTime = 0;

        double nano = (double) ConstManager.getConstant("Nanosecond");

        while (running) {
            boolean render = false;

            long startTime = System.nanoTime();
            long passedTime = startTime - lastTime;
            lastTime = startTime;

            unprocessedTime += passedTime / nano;
            frameCounter += passedTime;

            double deltaTime = passedTime / nano;

            while (unprocessedTime > frameTime) {
                render = true;
                unprocessedTime -= frameTime;

                if (GLFW.glfwWindowShouldClose(windowManager.getWindowID())) setRunning(false);

                if (frameCounter >= nano) {
                    fps = frames;
                    windowManager.setTitle("ScopeEngine FPS: " + getFps());
                    frames = 0;
                    frameCounter = 0;
                }
            }

            if (render) {
                input(deltaTime);
                update(deltaTime);
                render();
                frames++;
            }
        }

        cleanup();
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    private void input(double delta) {
        inputManager.input();
        ScopeEngine.getInstance().getCurrentScene().input(inputManager);
        if (Camera.getCurrentCamera() != null) Camera.getCurrentCamera().input(inputManager, delta);
    }

    private void render() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        ScopeEngine.getInstance().getCurrentScene().render();

        // Handle buffer and poll events
        GLFW.glfwSwapBuffers(windowManager.getWindowID());
        GLFW.glfwPollEvents();
    }

    private void update(double deltaTime) {
        ScopeEngine.getInstance().getCurrentScene().update(deltaTime);
    }

    public void cleanup() {
        // Call the cleanup on the current scene, windows, input, anything that needs cleaning
        soundManager.cleanup();
        errorCallback.free();
        windowManager.cleanup();
        GLFW.glfwTerminate();
    }
}
