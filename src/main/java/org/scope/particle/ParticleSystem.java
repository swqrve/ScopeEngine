package org.scope.particle;

import com.sun.prism.ps.Shader;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.scope.ScopeEngine;
import org.scope.camera.Camera;
import org.scope.logger.Debug;
import org.scope.manager.ModelManager;
import org.scope.render.ShaderProgram;
import org.scope.render.type.Quad;
import org.scope.util.MathUtil;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL31.glDrawArraysInstanced;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;

public class ParticleSystem {
    @Getter private final Particle[] particlePool;

    @Getter @Setter ParticleSetting particleSettings;
    @Getter private final ShaderProgram shader;

    private final List<Particle> particlesToRender = new ArrayList<>();

    private final Matrix4f particleModelMatrix = new Matrix4f();
    private final Vector4f particleColor = new Vector4f();
    private final Vector3f incrementVector = new Vector3f();

    private final Quad particleModel;

    // TODO: Particle pool size is by default 202 in the shader, can't be changed atm
    public ParticleSystem(ShaderProgram shader, ParticleSetting setting, int particlePoolSize) {
        if (ModelManager.getModel("quad") == null) new Quad();
        particleModel = (Quad) ModelManager.getModel("quad");

        particlePool = new Particle[particlePoolSize];

        for (int i = 0; i < particlePoolSize; i++) particlePool[i] = new Particle();

        this.particleSettings = setting;
        this.shader = shader;
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
        }
    }

    public void render(Camera camera) {
        for (Particle particle : particlePool) if (particle.isActive()) particlesToRender.add(particle);
        if (particlesToRender.size() < 1) return;

        shader.setBool("isAParticle", true);
        shader.setBool("usesLighting", false);

        shader.setMatrix4f("view", camera.getViewMatrix());

        for (int i = 0; i < particlesToRender.size(); i++) {
            Particle particle = particlesToRender.get(i);

            particle.getMaterial().setUniforms(shader, "material");
            if (particle.getMaterial().getTexture() == null) {
                particleColor.set(particle.getFinalColor()).lerp(particle.getStartingColor(), particle.getLifePercentageLived());
                shader.setVec4("particleColors[" + i + "]", particleColor);
            }

            float sizeScale = 1.0f;
            if (particle.isShrinking()) sizeScale = (MathUtil.lerp(particle.getEndSize(), particle.getStartSize(), particle.getLifePercentageLived()));


            shader.setMatrix4f("particleModelMatrices[" + i + "]",
                    particleModelMatrix.identity()
                            .translate(particle.getPosition())
                            .rotateX(particle.getRotation())
                            .scale(sizeScale)
            );
        }

        if (particlesToRender.get(0).getMaterial().getTexture() != null) particlesToRender.get(0).getMaterial().getTexture().bind(GL_TEXTURE0);
        particleModel.bindVAO();
        GL31.glDrawElementsInstanced(GL11.GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0, particlesToRender.size());

        shader.setBool("isAParticle", false);
        particlesToRender.clear();
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

        particle.getPosition().set(particleSettings.getBasePosition());

        particle.setRotation(particleSettings.getRotation());
        particle.setRotationSpeed(particleSettings.getRotationSpeed());

        particle.getVelocity().set(particleSettings.getBaseVelocity());
        particle.getVelocity().x += particleSettings.getVelocityDisplacement() * (ScopeEngine.getInstance().getRandom().nextFloat() -0.5f);
        particle.getVelocity().y += particleSettings.getVelocityDisplacement() * (ScopeEngine.getInstance().getRandom().nextFloat() -0.5f);
        particle.getVelocity().z += particleSettings.getVelocityDisplacement() * (ScopeEngine.getInstance().getRandom().nextFloat() -0.5f);

        particle.getStartingColor().set(particleSettings.getStartingColor());
        particle.getFinalColor().set(particleSettings.getFinalColor());
        
        particle.setStartSize(particleSettings.getStartSize());
        particle.setEndSize(particleSettings.getEndSize());
        
        particle.setLifeTime(particleSettings.getLifeTime());
        particle.setLifeRemaining(particle.getLifeTime());

        particle.setShrinking(particleSettings.isShrinking());
        particle.setEmitsLight(particleSettings.isEmitsLight());
        particle.setSpinningParticle(particleSettings.isSpins());
        
        particle.setMaterial(particleSettings.getMaterial());
        
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
