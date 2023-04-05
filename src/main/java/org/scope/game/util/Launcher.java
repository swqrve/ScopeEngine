package org.scope.game.util;

import org.scope.logger.Debug;
import org.scope.util.Platform;
import org.scope.game.TestGame;
import org.scope.util.EnginePreferences;
import org.scope.util.ScopeLauncher;

public class Launcher {
    public static void main(String[] args) {
        EnginePreferences preferences = new EnginePreferences(Platform.WINDOWS, 1280, 720, false, "ScopeEngine", false, "Engine", true, 5, 5);
        ScopeLauncher.launch(new TestGame(), args, preferences);

        System.out.println("------------------------------------------------------------------------------");
        Debug.log(Debug.LogLevel.INFO, "Now printing all saved logs...", false);
        Debug.printDebugLog(Debug.LogLevel.ALL);
    }
}
