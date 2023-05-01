package org.scope.animation;

import lombok.Getter;
import lombok.Setter;
import org.scope.render.shader.ShaderProgram;

public class Animator {
    @Getter private final Animation animation;

    @Getter @Setter private int currentFrame;
    @Getter @Setter private boolean paused;

    @Getter private double timeSinceLastFrameChange = 0;
    public Animator(Animation animation, int startFrame) {
        this.animation = animation;

        this.currentFrame = startFrame;
        this.paused = false;
    }

    public void update(double deltaTime) {
        if (paused) return;

        timeSinceLastFrameChange += deltaTime;

        if (timeSinceLastFrameChange >= animation.getTimeInBetweenFrames()) {
            currentFrame = (currentFrame + 1) % animation.getTotalFrames();
            timeSinceLastFrameChange = 0;
        }
    }

    public void bindFrame(ShaderProgram shader, int textureLoc) {
        shader.setInt("currentFrame", currentFrame);
        shader.setBool("isAnimated", true);
        shader.setFloat("atlasWidthSize", 512.0f);
        shader.setFloat("totalAtlasDimension", 3072.0f);

        animation.getAnimationAtlas().bind(textureLoc);
    }
}
