package org.scope.camera;

import lombok.Getter;
import lombok.Setter;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.scope.ScopeEngine;
import org.scope.input.InputManager;
import org.scope.util.ConstManager;

public abstract class Camera {

    @Getter @Setter private static Camera currentCamera;
    @Getter private static final Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
    @Getter private final Vector3f cameraPosition;
    @Getter private Matrix4f cameraProjection;

    @Getter private Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
    @Getter private final Vector3f direction = new Vector3f();

    @Getter private float fov; // Same with this, (Comment on sensitivity)

    @Getter @Setter private float yaw = -90.0f;
    @Getter @Setter private float pitch = 0.0f;

    private final Matrix4f cameraMatrix = new Matrix4f();
    private final Vector3f incrementVector = new Vector3f();

    public Camera(Vector3f cameraPosition, float fov) {
        this.cameraPosition = cameraPosition;
        this.fov = fov;
        cameraProjection = new Matrix4f().identity().perspective(Math.toRadians(fov), (float) ScopeEngine.getInstance().getEngineManager().getWindowManager().getWidth() / (float) ScopeEngine.getInstance().getEngineManager().getWindowManager().getHeight(), (Float) ConstManager.getConstant("zNear"), (Float) ConstManager.getConstant("zFar"));
    }

    public Camera(float x, float y, float z, float fov) {
        this(new Vector3f(x, y, z), fov);
    }

    public abstract void input(InputManager input, double delta);

    public void updateCameraProjection() {
        cameraProjection.identity().perspective(Math.toRadians(fov), (float) ScopeEngine.getInstance().getEngineManager().getWindowManager().getWidth() / (float) ScopeEngine.getInstance().getEngineManager().getWindowManager().getHeight(), (Float) ConstManager.getConstant("zNear"), (Float) ConstManager.getConstant("zFar"));
    }

    public void setFov(float fov) {
        this.fov = fov;
        updateCameraProjection();
    }

    public void setCameraPosition(float x, float y, float z) {
        this.cameraPosition.x = x;
        this.cameraPosition.y = y;
        this.cameraPosition.z = z;
    }

    public Matrix4f getViewMatrix() {
        direction.x = Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
        direction.y = Math.sin(Math.toRadians(pitch));
        direction.z = Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
        cameraFront = direction.normalize();

        return cameraMatrix.identity().lookAt(cameraPosition, incrementVector.set(cameraPosition).add(cameraFront), cameraUp);
    }

}
