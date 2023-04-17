package org.scope;

import lombok.Getter;
import org.scope.engine.EngineManager;
import org.scope.logger.Debug;
import org.scope.scene.Scene;
import org.scope.util.ConstManager;
import org.scope.util.EnginePreferences;

import java.util.Random;

import static org.lwjgl.opengl.GL11C.*;

public class ScopeEngine {
    @Getter private static ScopeEngine instance;
    @Getter private final EnginePreferences preferences;

    @Getter private final EngineManager engineManager;

    @Getter private Scene currentScene;

    @Getter private final Random random = new Random();


    public ScopeEngine(Scene entryScene, EnginePreferences preferences) {
        instance = this;

        this.preferences = preferences;

        // Handle debug settings in preferences
        Debug.setDefaultScopeName(preferences.getDebugDefaultScope());
        Debug.resetScope();
        Debug.setDefaultSaveLogs(preferences.isSaveDebugLogs());
        Debug.setLogVisibility(Debug.LogLevel.ALL);

        currentScene = entryScene;

        ConstManager.createConstant("maxPointLights", preferences.getMaxPointLights());
        ConstManager.createConstant("maxSpotLights", preferences.getMaxSpotLights());

        engineManager = new EngineManager();
        engineManager.init(preferences);
    }

    public void end() {
        engineManager.setRunning(false);
        cleanup();
    }

    public void cleanup() {
        engineManager.cleanup();
        currentScene.cleanup();
    }

    public void setCurrentScene(Scene nextScene) {
        if (currentScene != null) currentScene.cleanup();
        this.currentScene = nextScene;
    }

    public void enableBlending() {
        glEnable(GL_BLEND);
    }

    public void disableBlending() {
        glDisable(GL_BLEND);
    }
}
