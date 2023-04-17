package org.scope.render.text.type;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.freetype.FT_Face;
import org.scope.ScopeEngine;
import org.scope.camera.Camera;
import org.scope.logger.Debug;
import org.scope.render.ShaderProgram;
import org.scope.render.text.TextManager;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL30C.glBindVertexArray;
import static org.lwjgl.util.freetype.FreeType.*;

public class TextSource {
    @Getter @Setter private String name;

    private final Map<Character, GlyphCharacter> characters = new HashMap<>();

    @SneakyThrows
    public TextSource(String name, String directory, int pixelWidth, int pixelHeight) {
        this.name = name;
        if (directory.charAt(0) != '/') directory = "/" + directory;

        if (TextManager.getInstance() == null || TextManager.getInstance().getLibraryPointer() == 0) {
            Debug.log(Debug.LogLevel.ERROR, "A font is attempting to be created before the TextManager has been instantiated, or there was an error creating the library pointer!");
            return;
        }

        long facePointer;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer pFacePointer = stack.mallocPointer(1);
            int errorCode = FT_New_Face(TextManager.getInstance().getLibraryPointer(), Paths.get(TextSource.class.getResource(directory).toURI()).toFile().getAbsolutePath().replaceAll("/", "\\"), 0, pFacePointer);

            if (errorCode != 0) {
                Debug.log(Debug.LogLevel.FATAL, "Error #"  + errorCode + " creating FreeType font of name " + name + ".", true);
                ScopeEngine.getInstance().end();
                return;
            }

            facePointer = pFacePointer.get(0);
        }

        FT_Face face = FT_Face.create(facePointer);

        FT_Set_Pixel_Sizes(face, pixelWidth, pixelHeight); // 0, 48

        if (FT_Load_Char(face, 'X', FT_LOAD_RENDER) != 0) {
            Debug.log(Debug.LogLevel.FATAL, "Could not load glyphs for font of name " + name + ".", true);
            ScopeEngine.getInstance().end();
            return;
        }

        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        boolean announcedGlyphFailure = false;
        for (int character = 0; character < 128; character++) {
            if (FT_Load_Char(face, character, FT_LOAD_RENDER) != 0 && !announcedGlyphFailure) {
                Debug.log(Debug.LogLevel.WARN, "Could not load full glyphs for FreeType font of name " + name + ".", true);
                announcedGlyphFailure = true;
                continue;
            }

            int texture = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, texture);
            glTexImage2D(
                    GL_TEXTURE_2D,
                    0,
                    GL_RED,
                    face.glyph().bitmap().width(),
                    face.glyph().bitmap().rows(),
                    0,
                    GL_RED,
                    GL_UNSIGNED_BYTE,
                    face.glyph().bitmap().buffer(1)
            );

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            GlyphCharacter glyph = new GlyphCharacter(
                    texture,
                    face.glyph().bitmap().width(),
                    face.glyph().bitmap().rows(),
                    face.glyph().bitmap_left(),
                    face.glyph().bitmap_top(),
                    face.glyph().advance().x()
            );

            characters.put((char) character, glyph);
        }

        FT_Done_Face(face);

        TextManager.getInstance().addTextSource(this);
    }

    public void renderText(Camera uiCamera, ShaderProgram shader, String text, float x, float y, float scale, float colorR, float colorG, float colorB) {
        shader.bind();

        shader.setMatrix4f("projection", uiCamera.getCameraUIProjection());
        glUniform3f(glGetUniformLocation(shader.getProgramID(), "textColor"), colorR, colorG, colorB);

        glActiveTexture(GL_TEXTURE0);
        glBindVertexArray(TextManager.getInstance().getTextModel().getVaoID());

        for (int i = 0; i < text.length(); i++) {
            GlyphCharacter glyph = characters.get(text.charAt(i));

            float xPos = x + glyph.getBearing().x * scale;
            float yPos = y - (glyph.getSize().y - glyph.getBearing().y) * scale;

            float w = glyph.getSize().x * scale;
            float h = glyph.getSize().y * scale;

            float[] vertices = {
                    xPos, yPos + h, 0.0f, 0.0f,
                    xPos, yPos, 0.0f, 1.0f,
                    xPos + w, yPos, 1.0f, 1.0f,

                    xPos, yPos + h, 0.0f, 0.0f,
                    xPos + w, yPos, 1.0f, 1.0f,
                    xPos + w, yPos + h, 1.0f, 0.0f
            };

            glBindTexture(GL_TEXTURE_2D, glyph.getTextureID());

            glBindBuffer(GL_ARRAY_BUFFER, TextManager.getInstance().getTextModel().getVboID());
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

            glDrawArrays(GL_TRIANGLES, 0, 6);

            x += (glyph.getAdvance() >> 6) * scale;
        }
    }

    public void renderText(ShaderProgram shader, String text, float x, float y, float scale, float colorR, float colorG, float colorB) {
        if (TextManager.getInstance().getCurrentCamera() == null) {
            Debug.log(Debug.LogLevel.ERROR, "You've attempted to render text using the default UI camera, but there isn't one!");
            return;
        }

        renderText(TextManager.getInstance().getCurrentCamera(), shader, text, x, y, scale, colorR, colorG, colorB);
    }

}
