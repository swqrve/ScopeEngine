package org.scope.animation;

import lombok.Getter;
import org.scope.render.model.struct.Texture;

public class Animation {

    @Getter private final Texture animationAtlas;

    @Getter private final int textureGap;
    @Getter private final int totalFrames;

    @Getter private final long timeInBetweenFrames;

    public Animation(String animationAtlasPath, int textureSize, int totalTextures, long timeInBetweenFrames) {
        this.animationAtlas = new Texture(animationAtlasPath);

        this.textureGap = textureSize;
        this.totalFrames = totalTextures;

        this.timeInBetweenFrames = timeInBetweenFrames;
    }

}
