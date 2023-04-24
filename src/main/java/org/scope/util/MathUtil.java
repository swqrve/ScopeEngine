package org.scope.util;

import org.joml.Vector2f;

public class MathUtil {

    public static float lerp(float start, float end, float interp) {
        return (start * (1.0f - interp)) + (end * interp);
    }

    public static void screenSpaceToRange(Vector2f output, float xResolution, float yResolution, float x, float y) {
        output.set(((x / xResolution) * 2) - 1, ((y / yResolution) * 2) - 1);
    }
}
