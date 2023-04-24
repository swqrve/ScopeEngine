package org.scope.window;

import lombok.Getter;
import lombok.Setter;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.system.MemoryUtil;
import org.scope.ScopeEngine;
import org.scope.camera.Camera;
import org.scope.framework.Cleanable;
import org.scope.logger.Debug;

import java.nio.IntBuffer;

import static java.sql.Types.NULL;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13C.GL_MULTISAMPLE;

public class WindowManager implements Cleanable {

    @Getter private long windowID;

    @Getter private int width;
    @Getter private int height;

    @Getter private String title;
    @Getter @Setter private boolean resizable;

    public WindowManager(int width, int height, String title, boolean resizable) {
        this.width = width;
        this.height = height;
        this.title = title;
        this.resizable = resizable;
    }

    public void init() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!org.lwjgl.glfw.GLFW.glfwInit()) {
            Debug.log(Debug.LogLevel.FATAL, "Unable to initialize GLFW, closing window...");
            return;
        }

        setWindowHints();

        windowID = glfwCreateWindow(width, height, title, NULL, NULL);

        if (windowID == 0) {
            Debug.log(Debug.LogLevel.FATAL, "Failed to create GLFW window");
            glfwTerminate();
            return;
        }

        IntBuffer pWidth = MemoryUtil.memAllocInt(1);
        IntBuffer pHeight = MemoryUtil.memAllocInt(1);

        glfwGetWindowSize(windowID, pWidth, pHeight);

        GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        if (videoMode == null) return;
        glfwSetWindowPos(windowID, (videoMode.width() - pWidth.get(0)) / 2, (videoMode.height() - pHeight.get(0)) / 2);

        MemoryUtil.memFree(pWidth);
        MemoryUtil.memFree(pHeight);

        glfwMakeContextCurrent(windowID);
        GL.createCapabilities();

        GLFW.glfwSwapInterval(ScopeEngine.getInstance().getPreferences().isVSync() ? 1 : 0);

        glfwSetFramebufferSizeCallback(windowID, (w, width, height) -> {
            this.width = width;
            this.height = height;
            glViewport(0, 0, width, height);

            if (Camera.getCurrentCamera() != null) Camera.getCurrentCamera().updateCameraProjection();
        });

        glfwSetKeyCallback(windowID, (w, key, scancode, action, mod) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) glfwSetWindowShouldClose(windowID, true);
            if (key == GLFW_KEY_1 && action == GLFW_RELEASE) glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
            if (key == GLFW_KEY_2 && action == GLFW_RELEASE) glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        });

        GLFW.glfwShowWindow(windowID);

        GL11.glClearColor(0.3f, 0.3f, 0.3f, 1.0f);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glEnable(GL_MULTISAMPLE);

        // TODO: Enable face culling later after sorting out vertices in the standard models
/*        GL11.glFrontFace(GL_CCW);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);*/
    }

    public void setWindowHints() {
        GLFW.glfwDefaultWindowHints();

        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL11.GL_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);
        GLFW.glfwWindowHint(GLFW_SAMPLES, 4); // MSAA
    }

    public void setTitle(String title) {
        GLFW.glfwSetWindowTitle(windowID, title);
        this.title = title;
    }


    @Override
    public void cleanup() {
        GLFW.glfwDestroyWindow(windowID);
    }
}
