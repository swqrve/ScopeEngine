package org.scope.particle.type;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import org.joml.Vector4f;


public class Particle {
    @Getter private final Vector3f position = new Vector3f(0.0f, 0.0f, 0.0f);
    @Getter private final Vector3f velocity = new Vector3f(0.0f, 0.0f, 0.0f);

    @Getter @Setter private float lifeTime = 1.0f;
    @Getter @Setter private float lifeRemaining = 1.0f;

    @Getter @Setter private boolean shrinking = true; // TODO: add more customized with shrinking, maybe include sin, cos, etc. functions for interesting size changes
    @Getter @Setter private boolean emitsLight = false; // TODO: Currently with how lights are setup the particles would not be able to emit light. This will be changed tho
    @Getter @Setter private boolean affectedByLight = false;
    @Getter @Setter private boolean spinningParticle = true;
    @Getter @Setter private boolean active = false;
    @Getter @Setter private boolean billboard = true;
    @Getter @Setter private boolean fades = false;

    @Getter private final Vector4f startingColor = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);
    @Getter private final Vector4f finalColor = new Vector4f(0.0f, 0.0f, 0.0f, 1.0f);

    @Getter @Setter private float startSize = 1.0f;
    @Getter @Setter private float endSize = 0.0f;

    @Getter @Setter private float rotation = 0.0f;
    @Getter @Setter private float rotationSpeed = 0.10f;

    @Getter @Setter private float alpha = 1.0f;
    @Getter @Setter private float fadeSpeed = 1.0f;

    public float getLifePercentageLived() {
        return lifeRemaining / lifeTime;
    }

}
