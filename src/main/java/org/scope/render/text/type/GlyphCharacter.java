package org.scope.render.text.type;

import lombok.Getter;
import org.joml.Vector2f;

public class GlyphCharacter {
    @Getter private final int textureID;
    @Getter private final Vector2f size = new Vector2f();
    @Getter private final Vector2f bearing = new Vector2f();
    @Getter private final long advance;

    public GlyphCharacter(int textureID, int sizeWidth, int sizeHeight, int bitMapLeft, int bitMapTop, long advance) {
        this.textureID = textureID;
        this.size.set(sizeWidth, sizeHeight);
        this.bearing.set(bitMapLeft, bitMapTop);
        this.advance = advance;
    }
}
