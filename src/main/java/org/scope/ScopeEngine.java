package org.scope;

import lombok.Getter;
import org.scope.manager.EngineManager;
import org.scope.logger.Debug;
import org.scope.scene.Scene;
import org.scope.util.ConstManager;
import org.scope.util.EnginePreferences;

public class ScopeEngine {
    @Getter private static ScopeEngine instance;
    @Getter private final EnginePreferences preferences;

    @Getter private final EngineManager engineManager;

    @Getter private Scene currentScene;


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
        engineManager.setRunning(false);
        currentScene.cleanup();
    }

    public void setCurrentScene(Scene nextScene) {
        if (currentScene != null) currentScene.cleanup();
        this.currentScene = nextScene;
    }
}
