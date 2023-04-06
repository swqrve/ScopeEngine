package org.scope.sound.type;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.scope.camera.Camera;

import static org.lwjgl.openal.AL10.*;

public class SoundListener {

    private final float[] orientation = new float[6];

    private final Vector3f at = new Vector3f();
    private final Vector3f up = new Vector3f();


    public SoundListener(float x, float y, float z) {
        setPosition(x, y, z);
        setVelocity(0.0f, 0.0f,0.0f);
    }

    public SoundListener(Vector3f pos) {
        this(pos.x, pos.y, pos.z);
    }

    public void setOrientation(Vector3f at, Vector3f up) {
        orientation[0] = at.x;
        orientation[1] = at.y;
        orientation[2] = at.z;

        orientation[3] = up.x;
        orientation[4] = up.y;
        orientation[5] = up.z;

        alListenerfv(AL_ORIENTATION, orientation);
    }

    public void setPosition(Vector3f pos) {
        alListener3f(AL_POSITION, pos.x, pos.y, pos.z);
    }

    public void setPosition(float x, float y, float z) {
        alListener3f(AL_POSITION, x, y, z);
    }

    public void setVelocity(Vector3f vel) {
        alListener3f(AL_VELOCITY, vel.x, vel.y, vel.z);
    }

    public void setVelocity(float x, float y, float z) {
        alListener3f(AL_VELOCITY, x, y, z);
    }

    public void updateListener(Camera camera) {
        Matrix4f viewMatrix = camera.getViewMatrix();
        setPosition(camera.getCameraPosition());
        at.set(0.0f, 0.0f, 0.0f);
        viewMatrix.positiveZ(at).negate();
        up.set(0.0f, 0.0f, 0.0f);
        viewMatrix.positiveY(up);
        setOrientation(at, up);
    }
}
