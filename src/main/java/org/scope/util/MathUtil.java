package org.scope.util;

public class MathUtil {

    public static float lerp(float start, float end, float interp) {
        return (start * (1.0f - interp)) + (end * interp);
    }
}
