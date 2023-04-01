package org.scope.particle;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.scope.render.struct.Material;

@Accessors(chain = true)
public class ParticleSetting {

    @Getter private final Vector3f basePosition = new Vector3f();
    @Getter private final Vector3f baseVelocity = new Vector3f();

    @Getter @Setter private float lifeTime;

    @Getter @Setter private boolean shrinking = true;
    @Getter @Setter private boolean emitsLight = false;
    @Getter @Setter private boolean affectedByLight = false;
    @Getter @Setter private boolean spins = true;

    @Getter @Setter private Material material;

    @Getter private final Vector4f startingColor = new Vector4f();
    @Getter private final Vector4f finalColor = new Vector4f();

    @Getter @Setter private float startSize = 1.0f;
    @Getter @Setter private float endSize = 0.0f;

    @Getter @Setter private float rotation = 0.0f;
    @Getter @Setter private float rotationSpeed = 0.1f;
    @Getter @Setter private float velocityDisplacement = 1.0f;

    public ParticleSetting setBasePosition(float x, float y, float z) {
        basePosition.set(x, y, z);
        return this;
    }


    public ParticleSetting setBaseVelocity(float x, float y, float z) {
        baseVelocity.set(x, y, z);
        return this;
    }

    public ParticleSetting setStartingColor(float x, float y, float z, float w) {
        startingColor.set(x, y, z, w);
        return this;
    }

    public ParticleSetting setFinalColor(float x, float y, float z, float w) {
        finalColor.set(x, y, z, w);
        return this;
    }
}
