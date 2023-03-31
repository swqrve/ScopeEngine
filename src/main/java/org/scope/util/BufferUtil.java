package org.scope.util;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class BufferUtil {
    public static FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        ((Buffer) buffer.put(data)).flip();
        return buffer;
    }

    public static FloatBuffer storeDataInFloatBuffer(Matrix4f data) {
        return data.get(MemoryUtil.memAllocFloat(16));
    }

    public static IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
        ((Buffer) buffer.put(data)).flip();
        return buffer;
    }

    public static void freeMemory(FloatBuffer buffer) {
        MemoryUtil.memFree(buffer);
    }

    public static void freeMemory(IntBuffer buffer) {
        MemoryUtil.memFree(buffer);
    }
}
