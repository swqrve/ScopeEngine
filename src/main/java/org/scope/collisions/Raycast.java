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

    public boolean intersects(AABB aabb) {
        return intersectionCheck(this.origin, getInverseDirection(), aabb);
    }

    public static boolean intersectsAABB(Vector3f position, Vector3f direction, AABB aabb) {
        float invDirX = 1.0f / direction.x;
        float invDirY = 1.0f / direction.y;
        float invDirZ = 1.0f / direction.z;

        return intersectionCheck(position, new Vector3f(invDirX, invDirY, invDirZ), aabb);
    }

    private static boolean intersectionCheck(Vector3f origin, Vector3f inverse, AABB aabb) {
        float tMinX = (aabb.getBottomLeftCorner().x - origin.x) * inverse.x;
        float tMaxX = (aabb.getTopRightCorner().x - origin.x) * inverse.x;
        if (inverse.x < 0) {
            float tmp = tMinX;
            tMinX = tMaxX;
            tMaxX = tmp;
        }

        float tMinY = (aabb.getBottomLeftCorner().y - origin.y) * inverse.y;
        float tMaxY = (aabb.getTopRightCorner().y - origin.y) * inverse.y;
        if (inverse.y < 0) {
            float tmp = tMinY;
            tMinY = tMaxY;
            tMaxY = tmp;
        }

        float tMinZ = (aabb.getBottomLeftCorner().z - origin.z) * inverse.z;
        float tMaxZ = (aabb.getTopRightCorner().z - origin.z) * inverse.z;
        if (inverse.z < 0) {
            float tmp = tMinZ;
            tMinZ = tMaxZ;
            tMaxZ = tmp;
        }

        float tEntry = Math.max(Math.max(tMinX, tMinY), tMinZ);
        float tExit = Math.min(Math.min(tMaxX, tMaxY), tMaxZ);

        return !(tEntry > tExit) && !(tExit < 0);
    }
}
