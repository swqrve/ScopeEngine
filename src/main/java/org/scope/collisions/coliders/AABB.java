package org.scope.collisions.coliders;

import lombok.Getter;
import org.joml.Vector3f;

public class AABB {
    @Getter private int physicsLayer; // TODO: Properly implement physics layers for collisions, for now this is fine since it's not a necessity and can be handled easily by the game maker

    @Getter private final Vector3f topRightCorner;
    @Getter private final Vector3f bottomLeftCorner;

    private final Vector3f center = new Vector3f();

    public AABB() {
        topRightCorner = new Vector3f(0, 0, 0);
        bottomLeftCorner = new Vector3f(0, 0, 0);
    }

    public AABB(Vector3f bottomLeft, Vector3f topRight) {
        this.bottomLeftCorner = bottomLeft;
        this.topRightCorner = topRight;
    }

    public AABB(Vector3f center, Vector3f dimensions, boolean x) {
        float halfScaleX = dimensions.x / 2.0f;
        float halfScaleY = dimensions.y / 2.0f;
        float halfScaleZ = dimensions.z / 2.0f;

        bottomLeftCorner = new Vector3f(center.x - halfScaleX, center.y - halfScaleY, center.z - halfScaleZ);
        topRightCorner = new Vector3f(center.x + halfScaleX, center.y + halfScaleY, center.z + halfScaleZ);

        this.center.set(center);
    }

    public AABB(AABB toCopy) {
        this.physicsLayer = toCopy.getPhysicsLayer();

        bottomLeftCorner = new Vector3f(toCopy.getBottomLeftCorner());
        topRightCorner = new Vector3f(toCopy.getTopRightCorner());
    }

    public boolean contains(Vector3f point) {
        return point.x >= bottomLeftCorner.x && point.x <= topRightCorner.x
                && point.y >= bottomLeftCorner.y && point.y <= topRightCorner.y
                && point.z >= bottomLeftCorner.z && point.z <= topRightCorner.z;
    }

    public boolean intersects(AABB toCheck) {
        return (topRightCorner.x >= toCheck.getBottomLeftCorner().x && bottomLeftCorner.x <= toCheck.getTopRightCorner().x)
                && (topRightCorner.y >= toCheck.getBottomLeftCorner().y && bottomLeftCorner.y <= toCheck.getTopRightCorner().y)
                && (topRightCorner.z >= toCheck.getBottomLeftCorner().z && bottomLeftCorner.z <= toCheck.getTopRightCorner().z);
    }

    public Vector3f getCenter() {
        center.set(bottomLeftCorner).add(topRightCorner).div(2.0f);
        return center;
    }

    public AABB merge(AABB toMerge) {
        float bottomLeftX = Math.min(bottomLeftCorner.x, toMerge.getBottomLeftCorner().x);
        float bottomLeftY = Math.min(bottomLeftCorner.y, toMerge.getBottomLeftCorner().y);
        float bottomLeftZ = Math.min(bottomLeftCorner.z, toMerge.getBottomLeftCorner().z);

        bottomLeftCorner.set(bottomLeftX, bottomLeftY, bottomLeftZ);

        float topRightX = Math.max(topRightCorner.x, toMerge.getTopRightCorner().x);
        float topRightY = Math.max(topRightCorner.y, toMerge.getTopRightCorner().y);
        float topRightZ = Math.max(topRightCorner.z, toMerge.getTopRightCorner().z);

        topRightCorner.set(topRightX, topRightY, topRightZ);

        return this;
    }

    public AABB mergeIntoNew(AABB toMerge) {
        float bottomLeftX = Math.min(bottomLeftCorner.x, toMerge.getBottomLeftCorner().x);
        float bottomLeftY = Math.min(bottomLeftCorner.y, toMerge.getBottomLeftCorner().y);
        float bottomLeftZ = Math.min(bottomLeftCorner.z, toMerge.getBottomLeftCorner().z);

        float topRightX = Math.max(topRightCorner.x, toMerge.getTopRightCorner().x);
        float topRightY = Math.max(topRightCorner.y, toMerge.getTopRightCorner().y);
        float topRightZ = Math.max(topRightCorner.z, toMerge.getTopRightCorner().z);

        return new AABB(new Vector3f(bottomLeftX, bottomLeftY, bottomLeftZ), new Vector3f(topRightX, topRightY, topRightZ));
    }

    @Override
    public String toString() {
        return "AABB: Bottom Left X:" + getBottomLeftCorner().x + " Y:" + getBottomLeftCorner().y + " Z:" + getBottomLeftCorner().z +
                " Top Right X: " + getTopRightCorner().x + " Y:" + getTopRightCorner().y + " Z:" + getTopRightCorner().z;
    }
}
