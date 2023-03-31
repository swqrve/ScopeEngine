package org.scope.util;

import lombok.Getter;

public class EnginePreferences {
    @Getter private final int width;
    @Getter private final int height;

    @Getter private final String title;
    @Getter private final boolean resizable;
    @Getter private final boolean vSync;

    @Getter private final String debugDefaultScope;
    @Getter private final boolean saveDebugLogs;

    @Getter private final Platform platform;

    @Getter private final int maxSpotLights;
    @Getter private final int maxPointLights;

    public EnginePreferences(Platform platform, int width, int height, boolean resizable, String title, boolean vSync, int maxSpotLights, int maxPointLights) {
        this(platform, width, height, resizable, title, vSync, "Engine", false, maxSpotLights, maxPointLights);
    }

    public EnginePreferences(Platform platform, int width, int height, boolean resizable, String title, boolean vSync, String debugDefaultScope, boolean saveDebugLogs, int maxSpotLights, int maxPointLights) {
        this.width = width;
        this.height = height;

        this.title = title;
        this.resizable = resizable;

        this.vSync = vSync;

        this.debugDefaultScope = debugDefaultScope;
        this.saveDebugLogs = saveDebugLogs;

        this.platform = platform;

        this.maxSpotLights = maxSpotLights;
        this.maxPointLights = maxPointLights;
    }
}
