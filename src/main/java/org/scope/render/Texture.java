package org.scope.render;

import lombok.Getter;
import lombok.Setter;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryUtil;
import org.scope.logger.Debug;
import org.scope.util.BufferUtil;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_MAX_LEVEL;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL14.GL_MIRRORED_REPEAT;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.stbi_load;

public class Texture {

    @Getter @Setter private static Texture errorTexture;
    @Getter private final int textureID;

    public Texture(String fileName) {
        if (fileName.charAt(0) == '/' || fileName.charAt(0) == '\\') fileName = fileName.substring(1);

        IntBuffer width = MemoryUtil.memAllocInt(1);
        IntBuffer height = MemoryUtil.memAllocInt(1);
        IntBuffer nrChannels = MemoryUtil.memAllocInt(1);

        STBImage.stbi_set_flip_vertically_on_load(true); // TODO: Add this to the constructor if necessary

        ByteBuffer data = null;
        if (Texture.class.getClassLoader().getResource(fileName) != null) data = stbi_load(Texture.class.getClassLoader().getResource(fileName).getPath().substring(1), width, height, nrChannels, 0);

        if (data == null) {
            Debug.log(Debug.LogLevel.ERROR, "Failed to load texture " + fileName + "... Will return a miscellaneous texture instead.", true);
            textureID = errorTexture.getTextureID();
            return;
        }

        textureID = glGenTextures();

        int format = GL_RGBA;

        int value = nrChannels.get();
        if (value == 1) format = GL_RED;
        if (value == 3) format = GL_RGB;

        bind(0);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_MIRRORED_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_MIRRORED_REPEAT);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 4);

        glTexImage2D(GL_TEXTURE_2D, 0, format, width.get(), height.get(), 0, format, GL_UNSIGNED_BYTE, data);
        glGenerateMipmap(GL_TEXTURE_2D);

        BufferUtil.freeMemory(width);
        BufferUtil.freeMemory(height);
        BufferUtil.freeMemory(nrChannels);

        STBImage.stbi_image_free(data);
        Debug.log(Debug.LogLevel.INFO, "Texture Loaded at " + fileName);
    }

    public void bind(int textureNumber) {
        glActiveTexture(textureNumber);
        glBindTexture(GL_TEXTURE_2D, textureID);
    }

    public void bind() {
        glActiveTexture(0);
        glBindTexture(GL_TEXTURE_2D, textureID);
    }

    public void setImageToClamp() {
        bind(0);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        glBindTexture(GL_TEXTURE_2D, 0);
    }
}
