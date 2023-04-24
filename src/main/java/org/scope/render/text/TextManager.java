package org.scope.render.text;

import lombok.Getter;
import lombok.Setter;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.scope.ScopeEngine;
import org.scope.camera.Camera;
import org.scope.framework.Cleanable;
import org.scope.logger.Debug;
import org.scope.render.model.ModelManager;
import org.scope.render.shader.ShaderProgram;
import org.scope.render.text.type.TextSource;
import org.scope.render.model.type.TextModel;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.util.freetype.FreeType.FT_Done_FreeType;
import static org.lwjgl.util.freetype.FreeType.FT_Init_FreeType;

public class TextManager implements Cleanable {
    @Getter private static TextManager instance;

    @Getter @Setter private Camera currentCamera = null;

    @Getter private TextModel textModel;

    private final List<TextSource> loadedSources = new ArrayList<>();

    @Getter private long libraryPointer;

    public TextManager() {
        instance = this;
    }

    public void init() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer pLibraryPointer = stack.mallocPointer(1);
            int errorCode = FT_Init_FreeType(pLibraryPointer);

            if (errorCode != 0) {
                Debug.log(Debug.LogLevel.FATAL, "Error instantiating FreeType Library!", true);
                ScopeEngine.getInstance().end();
                return;
            }

            libraryPointer = pLibraryPointer.get(0);
        }

        if (ModelManager.getModel("text") == null) textModel = new TextModel("text");
    }

    public TextSource getTextSource(String name) {
        for (TextSource source : loadedSources) if (source.getName().equalsIgnoreCase(name)) return source;

        Debug.log(Debug.LogLevel.WARN, "Unable to find a source by name of " + name + ". Attempting to return another text source!", true);

        if (loadedSources.size() == 0) {
            Debug.log(Debug.LogLevel.FATAL, "Unable to find any font to fall back on, crashing!", true);
            ScopeEngine.getInstance().end();
            return null;
        }

        return loadedSources.get(0);
    }

    public void addTextSource(TextSource source) {
        for (TextSource s : loadedSources) if (s.getName().equalsIgnoreCase(source.getName())) {
            Debug.log(Debug.LogLevel.WARN, "Attempting to add a text source with the same name as another! Adding a \"!\" to the end of the new sources name!", true);
            source.setName(s.getName() + "!");
        }

        loadedSources.add(source);
    }

    public void renderText(TextSource source, Camera uiCamera, ShaderProgram shader, String text, float x, float y, float scale, float colorR, float colorG, float colorB) {
        source.renderText(uiCamera, shader, text, x, y, scale, colorR, colorG, colorB);
    }

    public void renderText(String sourceName, Camera uiCamera, ShaderProgram shader, String text, float x, float y, float scale, float colorR, float colorG, float colorB) {
        renderText(getTextSource(sourceName), uiCamera, shader, text, x, y, scale, colorR, colorG, colorB);
    }

    public void renderText(String sourceName, ShaderProgram shader, String text, float x, float y, float scale, float colorR, float colorG, float colorB) {
        renderText(getTextSource(sourceName), shader, text, x, y, scale, colorR, colorG, colorB);
    }

    public void renderText(TextSource source, ShaderProgram shader, String text, float x, float y, float scale, float colorR, float colorG, float colorB) {
        if (currentCamera == null) {
            Debug.log(Debug.LogLevel.ERROR, "You've attempted to render text using the default UI camera, but there isn't one!");
            return;
        }

        renderText(source, currentCamera, shader, text, x, y, scale, colorR, colorG, colorB);
    }

    @Override
    public void cleanup() {
        FT_Done_FreeType(libraryPointer);
    }
}
