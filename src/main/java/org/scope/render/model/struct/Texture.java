package org.scope.render.model.struct;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryUtil;
import org.scope.logger.Debug;
import org.scope.util.BufferUtil;
import org.scope.util.FileUtil;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_MAX_LEVEL;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30C.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

public class Texture {

    @Getter @Setter private static Texture errorTexture;
    @Getter private final int textureID;

    @SneakyThrows
    public Texture(String fileName) {
        if (fileName.charAt(0) != '/') fileName = "/" + fileName;

        IntBuffer width = MemoryUtil.memAllocInt(1);
        IntBuffer height = MemoryUtil.memAllocInt(1);
        IntBuffer nrChannels = MemoryUtil.memAllocInt(1);

        STBImage.stbi_set_flip_vertically_on_load(true); // TODO: Add this to the constructor if necessary

        ByteBuffer data = FileUtil.fileDirToBuffer(fileName);
        if (Texture.class.getResourceAsStream(fileName) != null) data = stbi_load_from_memory(data, width, height, nrChannels, 0);

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

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR  );
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 1);

        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, 1000);

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
