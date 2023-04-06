package org.scope.sound.type;

import lombok.Getter;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryUtil;
import org.scope.framework.Cleanable;
import org.scope.sound.SoundManager;

import java.nio.ShortBuffer;

import static org.lwjgl.openal.AL10.*;

public class SoundBuffer implements Cleanable {

    @Getter private final int bufferID;
    private final ShortBuffer pcmSource;

    public SoundBuffer(String sourceFile) {
        this.bufferID = alGenBuffers();

        try (STBVorbisInfo info = STBVorbisInfo.malloc()) {
            pcmSource = SoundManager.readVorbis(sourceFile, info);
            alBufferData(bufferID, info.channels() == 1 ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16, pcmSource, info.sample_rate());
        }

        SoundManager.getInstance().getSoundBuffers().add(this);
    }

    @Override
    public void cleanup() {
        alDeleteBuffers(bufferID);
        if (pcmSource != null) MemoryUtil.memFree(pcmSource);
    }
}
