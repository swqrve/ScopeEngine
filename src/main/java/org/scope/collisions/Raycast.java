package org.scope.collisions;

import lombok.Getter;
import org.joml.Vector3f;
import org.scope.collisions.coliders.AABB;

public class Raycast {
    @Getter private final Vector3f origin;
    @Getter private final Vector3f direction;

    private final Vector3f inverseDirection = new Vector3f();
    public Raycast(Vector3f origin, Vector3f direction) {
        this.origin = new Vector3f(origin);
        this.direction = new Vector3f(direction);

        getInverseDirection();
    }

    public Raycast(float x, float y, float z, float dirX, float dirY, float dirZ) {
        this.origin = new Vector3f(x, y, z);
        this.direction = new Vector3f(dirX, dirY, dirZ);

        getInverseDirection();
    }

    public Vector3f getInverseDirection() {
        inverseDirection.set(1.0f / direction.x, 1.0f / direction.y, 1.0f / direction.z);
        return inverseDirection;
    }

    public boolean intersectsABB(AABB aabb) {
        Vector3f inverseDirection = getInverseDirection();

        float tMinX = (aabb.getBottomLeftCorner().x - origin.x) * inverseDirection.x;
        float tMaxX = (aabb.getTopRightCorner().x - origin.x) * inverseDirection.x;
        if (inverseDirection.x < 0) {
            float tmp = tMinX;
            tMinX = tMaxX;
            tMaxX = tmp;
        }

        float tMinY = (aabb.getBottomLeftCorner().y - origin.y) * inverseDirection.y;
        float tMaxY = (aabb.getTopRightCorner().y - origin.y) * inverseDirection.y;
        if (inverseDirection.y < 0) {
            float tmp = tMinY;
            tMinY = tMaxY;
            tMaxY = tmp;
        }

        float tMinZ = (aabb.getBottomLeftCorner().z - origin.z) * inverseDirection.z;
        float tMaxZ = (aabb.getTopRightCorner().z - origin.z) * inverseDirection.z;
        if (inverseDirection.z < 0) {
            float tmp = tMinZ;
            tMinZ = tMaxZ;
            tMaxZ = tmp;
        }

        float tEntry = Math.max(Math.max(tMinX, tMinY), tMinZ);
        float tExit = Math.min(Math.min(tMaxX, tMaxY), tMaxZ);

        if (tEntry > tExit || tExit < 0) return false;

        return true;
    }

    public static boolean intersectsAABB(Vector3f position, Vector3f direction, AABB aabb) {
        float invDirX = 1.0f / direction.x;
        float invDirY = 1.0f / direction.y;
        float invDirZ = 1.0f / direction.z;

        float tMinX = (aabb.getBottomLeftCorner().x - position.x) * invDirX;
        float tMaxX = (aabb.getTopRightCorner().x - position.x) * invDirX;
        if (invDirX < 0) {
            float tmp = tMinX;
            tMinX = tMaxX;
            tMaxX = tmp;
        }

        float tMinY = (aabb.getBottomLeftCorner().y - position.y) * invDirY;
        float tMaxY = (aabb.getTopRightCorner().y - position.y) * invDirY;
        if (invDirY < 0) {
            float tmp = tMinY;
            tMinY = tMaxY;
            tMaxY = tmp;
        }

        float tMinZ = (aabb.getBottomLeftCorner().z - position.z) * invDirZ;
        float tMaxZ = (aabb.getTopRightCorner().z - position.z) * invDirZ;
        if (invDirZ < 0) {
            float tmp = tMinZ;
            tMinZ = tMaxZ;
            tMaxZ = tmp;
        }

        float tEntry = Math.max(Math.max(tMinX, tMinY), tMinZ);
        float tExit = Math.min(Math.min(tMaxX, tMaxY), tMaxZ);

        if (tEntry > tExit || tExit < 0) return false;

        return true;
    }
}
