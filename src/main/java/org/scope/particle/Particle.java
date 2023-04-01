package org.scope.particle;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.scope.render.struct.Material;


public class Particle {
    @Getter private final Vector3f position = new Vector3f();
    @Getter private final Vector3f velocity = new Vector3f();

    @Getter @Setter private float lifeTime;
    @Getter @Setter private float lifeRemaining;

    @Getter @Setter private boolean shrinking = true; // TODO: add more customized with shrinking, maybe include sin, cos, etc. functions for interesting size changes
    @Getter @Setter private boolean emitsLight = false; // TODO: Currently with how lights are setup the particles would not be able to emit light. This will be changed tho
    @Getter @Setter private boolean affectedByLight = false;
    @Getter @Setter private boolean spinningParticle = true;
    @Getter @Setter private boolean active = false;

    @Getter @Setter private Material material;

    @Getter private final Vector4f startingColor = new Vector4f();
    @Getter private final Vector4f finalColor = new Vector4f();

    @Getter @Setter private float startSize = 1.0f;
    @Getter @Setter private float endSize = 0.0f;

    @Getter @Setter private float rotation = 0.0f;
    @Getter @Setter private float rotationSpeed = 0.10f;

    public float getLifePercentageLived() {
        return lifeRemaining / lifeTime;
    }

}
