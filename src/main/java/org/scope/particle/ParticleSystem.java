package org.scope.particle;

import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL31;
import org.lwjgl.system.MemoryUtil;
import org.scope.ScopeEngine;
import org.scope.camera.Camera;
import org.scope.logger.Debug;
import org.scope.manager.ModelManager;
import org.scope.render.ShaderProgram;
import org.scope.render.type.Quad;
import org.scope.util.MathUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30C.glBindBufferBase;
import static org.lwjgl.opengl.GL43C.GL_SHADER_STORAGE_BUFFER;

public class ParticleSystem {
    private final Particle[] particlePool;

    @Getter @Setter private ParticleSetting particleSettings;
    private final ShaderProgram shader;

    private final Vector3f incrementVector = new Vector3f();

    private final Quad particleModel;
    private final Vector4f colorOffSet = new Vector4f();

    private final int ssbo;
    private final Matrix4f[] matrices;
    private final Vector4f[] colors;
    private final FloatBuffer dataBuffer;


    public ParticleSystem(ShaderProgram shader, ParticleSetting setting, int particlePoolSize) {
        if (ModelManager.getModel("quad") == null) new Quad();
        particleModel = (Quad) ModelManager.getModel("quad");

        particlePool = new Particle[particlePoolSize];

        for (int i = 0; i < particlePoolSize; i++) particlePool[i] = new Particle();

        this.particleSettings = setting;
        this.shader = shader;

        ssbo = glGenBuffers();

        matrices = new Matrix4f[particlePoolSize];
        colors = new Vector4f[particlePoolSize];
        dataBuffer = MemoryUtil.memAllocFloat(20 * particlePoolSize);

        for (int i = 0; i < particlePoolSize; i++) {
            matrices[i] = new Matrix4f();
            colors[i] = new Vector4f();
        }
    }

    public void update(double deltaTime) {
        for (Particle particle : particlePool) {
            if (!particle.isActive()) continue;

            particle.setLifeRemaining((float) (particle.getLifeRemaining() - deltaTime));
            if (particle.getLifeRemaining() <= 0.0f) {
                particle.setActive(false);
                continue;
            }

            particle.getPosition().add(incrementVector.set(particle.getVelocity()).mul((float) deltaTime));

            if (particle.isSpinningParticle()) particle.setRotation((float) (particle.getRotationSpeed() * deltaTime));
            if (particle.isFades()) particle.setAlpha((float) (particle.getAlpha() - (particle.getFadeSpeed() * deltaTime)));
        }
    }

    public void render(Camera camera) {
        shader.setBool("isAParticle", true);
        shader.setBool("usesLighting", false);

        shader.setMatrix4f("view", camera.getViewMatrix());

        particleSettings.getMaterial().setUniforms(shader, "material");

        int index = 0;
        for (Particle particle : particlePool) {
            if (!particle.isActive()) continue;

            if (particleSettings.getMaterial().getTexture() == null) colors[index].set(particle.getFinalColor()).lerp(particle.getStartingColor(), particle.getLifePercentageLived());


            float sizeScale = 1.0f;
            if (particle.isShrinking()) sizeScale = (MathUtil.lerp(particle.getEndSize(), particle.getStartSize(), particle.getLifePercentageLived()));

            matrices[index].identity().translate(particle.getPosition());
            if (particle.isBillboard()) camera.getViewMatrix().transpose3x3(matrices[index]);
            matrices[index].rotateX(particle.getRotation()).scale(sizeScale);

            index++;
        }

        dataBuffer.position(0);
        for (int i = 0; i < index; i++) {
            matrices[i].get(dataBuffer);
            dataBuffer.position(dataBuffer.position() + 16);

            colors[i].get(dataBuffer);
            dataBuffer.position(dataBuffer.position() + 4);
        }

        dataBuffer.rewind();

        glBindBuffer(GL_SHADER_STORAGE_BUFFER, ssbo);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, ssbo);
        glBufferData(GL_SHADER_STORAGE_BUFFER, dataBuffer, GL_DYNAMIC_DRAW);

        if (particleSettings.getMaterial().getTexture() != null) particleSettings.getMaterial().getTexture().bind(GL_TEXTURE0);

        particleModel.bindVAO();
        GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0, index);

        shader.setBool("isAParticle", false);
    }


    public void setParticleToSettings(Particle particle) {
        particle.getPosition().set(particleSettings.getBasePosition());

        particle.setRotation(particleSettings.getRotation());
        particle.setRotationSpeed(particleSettings.getRotationSpeed());

        particle.getVelocity().set(particleSettings.getBaseVelocity());
        particle.getVelocity().x += particleSettings.getVelocityDisplacement() * (ScopeEngine.getInstance().getRandom().nextFloat() -0.5f);
        particle.getVelocity().y += particleSettings.getVelocityDisplacement() * (ScopeEngine.getInstance().getRandom().nextFloat() -0.5f);
        particle.getVelocity().z += particleSettings.getVelocityDisplacement() * (ScopeEngine.getInstance().getRandom().nextFloat() -0.5f);

        colorOffSet.set(particleSettings.getStartingColor());
        colorOffSet.x *= 1.0 + ScopeEngine.getInstance().getRandom().nextDouble() * (particleSettings.getColorStartXVariation() - 1.0f);
        colorOffSet.y *= 1.0 + ScopeEngine.getInstance().getRandom().nextDouble() * (particleSettings.getColorStartYVariation() - 1.0f);
        colorOffSet.z *= 1.0 + ScopeEngine.getInstance().getRandom().nextDouble() * (particleSettings.getColorStartZVariation() - 1.0f);
        particle.getStartingColor().set(colorOffSet);

        colorOffSet.set(particleSettings.getFinalColor());
        colorOffSet.x *= 1.0 + ScopeEngine.getInstance().getRandom().nextDouble() * (particleSettings.getColorEndXVariation() - 1.0f);
        colorOffSet.y *= 1.0 + ScopeEngine.getInstance().getRandom().nextDouble() * (particleSettings.getColorEndYVariation() - 1.0f);
        colorOffSet.z *= 1.0 + ScopeEngine.getInstance().getRandom().nextDouble() * (particleSettings.getColorEndZVariation() - 1.0f);
        particle.getFinalColor().set(colorOffSet);

        double displacementFactor = particleSettings.getSizeDisplacementMin() + ScopeEngine.getInstance().getRandom().nextDouble() * (particleSettings.getSizeDisplacementMax() - particleSettings.getSizeDisplacementMin());
        particle.setStartSize((float) (particleSettings.getStartSize() * displacementFactor));
        particle.setEndSize(particleSettings.getEndSize());

        particle.setLifeTime(particleSettings.getLifeTime());
        particle.setLifeRemaining(particle.getLifeTime());

        particle.setAlpha(particleSettings.getStartingAlpha());
        particle.setFadeSpeed(particleSettings.getFadeSpeed());

        particle.setShrinking(particleSettings.isShrinking());
        particle.setEmitsLight(particleSettings.isEmitsLight());
        particle.setSpinningParticle(particleSettings.isSpins());
        particle.setBillboard(particleSettings.isBillboard());
        particle.setFades(particleSettings.isFades());
    }
    public void emitParticle() {
        if (particleSettings == null) {
            Debug.log(Debug.LogLevel.WARN, "Attempting to emit a particle when no particle settings have been set! No particle will be emitted.");
            return;
        }

        Particle particle = getFreeParticle();
        if (particle == null) {
            Debug.log(Debug.LogLevel.WARN, "There are no more particles left in the pool to emit!");
            return;
        }

        setParticleToSettings(particle);
        particle.setActive(true);
    }

    public Particle getFreeParticle() { // TODO: This is slow, for now it is likely fast enough but should its performance become a problem implementing a free list system would be better. Implementation can be found here: https://gameprogrammingpatterns.com/object-pool.html#a-free-list
        for (Particle particle : particlePool) if (!particle.isActive()) return particle;
        return null;
    }

    public void emitParticle(ParticleSetting setting) {
        setParticleSettings(setting);
        emitParticle();
    }

    public void render() {
        render(Camera.getCurrentCamera());
    }
}
