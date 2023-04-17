package org.scope.util;

import org.lwjgl.Version;
import org.scope.ScopeEngine;
import org.scope.logger.Debug;
import org.scope.scene.Scene;

public class ScopeLauncher {
    public static void launch(Scene entryScene, String[] args, EnginePreferences preferences) {
        if (preferences.getPlatform() != Platform.WINDOWS) {
            Debug.resetScope();
            Debug.log(Debug.LogLevel.FATAL, "Detected a non-implemented platform... ScopeEngine only supports Windows. Linux and Mac support will be implemented in the future.", true);
            return;
        }

        Debug.log(Debug.LogLevel.INFO, "LWJGL Version: " + Version.getVersion(), true);
        Debug.log(Debug.LogLevel.INFO,"JOML Version: 1.10.5", true); // Defined in Pom
        if (args.length == 0)  Debug.log(Debug.LogLevel.INFO,"No Args Detected", true);
        Debug.log(Debug.LogLevel.INFO,"Now Launching..", true);

        new ScopeEngine(entryScene, preferences);

        Debug.log(Debug.LogLevel.INFO,"Shutting down...", true); // Do any last second necessary cleanup
    }
}
