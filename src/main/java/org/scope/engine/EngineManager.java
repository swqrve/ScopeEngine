package org.scope.engine;

import lombok.Getter;
import lombok.Setter;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11;
import org.scope.ScopeEngine;
import org.scope.camera.Camera;
import org.scope.input.InputManager;
import org.scope.logger.Debug;
import org.scope.render.model.struct.Texture;
import org.scope.render.shader.ShaderProgram;
import org.scope.render.text.TextManager;
import org.scope.render.text.type.TextSource;
import org.scope.sound.SoundManager;
import org.scope.sound.type.SoundBuffer;
import org.scope.sound.type.SoundSource;
import org.scope.util.ConstManager;
import org.scope.util.EnginePreferences;
import org.scope.util.FileUtil;
import org.scope.window.WindowManager;

public class EngineManager {

    @Getter private WindowManager windowManager;
    @Getter private InputManager inputManager;
    @Getter private SoundManager soundManager;
    @Getter private TextManager textManager;


    @Getter @Setter private boolean running;

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

        textManager = new TextManager();
        textManager.init();

        initDefaultAssets();

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
                if (!running) break;

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
                if (!running) break;

                input(deltaTime);
                update(deltaTime);
                render(deltaTime);
                frames++;
            }
        }
    }

    private void input(double delta) {
        inputManager.input();
        ScopeEngine.getInstance().getCurrentScene().input(inputManager);
        if (Camera.getCurrentCamera() != null) Camera.getCurrentCamera().input(inputManager, delta);
    }

    private void render(double delta) {
        if (!running) return;

        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        ScopeEngine.getInstance().getCurrentScene().render(delta);

        GLFW.glfwSwapBuffers(windowManager.getWindowID());
        GLFW.glfwPollEvents();
    }

    private void update(double deltaTime) {
        ScopeEngine.getInstance().getCurrentScene().update(deltaTime);
    }

    private void initDefaultAssets() {
        Texture.setErrorTexture(new Texture("textures/error.png"));

        new TextSource("default", "fonts/arial.ttf", 0, 48);

        new SoundSource("creak")
                .setBuffer(new SoundBuffer("sounds/creak.ogg").getBufferID())
                .setGain(0.10f)
                .setLoops(false)
                .setIsRelative(false)
                .setPosition(0.0f, 0.0f, 0.0f);

        new ShaderProgram("default", FileUtil.loadResource("shaders/vertex.glsl"), FileUtil.loadResource("shaders/fragment.glsl"));
        new ShaderProgram("skyboxD", FileUtil.loadResource("shaders/skybox/skyboxvertex.glsl"), FileUtil.loadResource("shaders/skybox/skyboxfragment.glsl"));
        new ShaderProgram("uiD", FileUtil.loadResource("shaders/ui/uivertex.glsl"), FileUtil.loadResource("shaders/ui/uifragment.glsl"));
    }

    public void cleanup() {
        // Call the cleanup on the current scene, windows, input, anything that needs cleaning
        soundManager.cleanup();
        errorCallback.free();
        windowManager.cleanup();
        GLFW.glfwTerminate();
    }
}
