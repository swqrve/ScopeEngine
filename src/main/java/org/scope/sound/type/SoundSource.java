package org.scope.sound.type;

import org.joml.Vector3f;
import org.scope.framework.Cleanable;
import org.scope.logger.Debug;
import org.scope.sound.SoundManager;

import static org.lwjgl.openal.AL10.*;

public class SoundSource implements Cleanable {
    public enum SoundState { PLAY, PAUSE, STOP }

    private final int soundSourceID;

    public SoundSource(String name) {
        this.soundSourceID = alGenSources();

        setLoops(false);
        setIsRelative(false);

        setPosition(0.0f, 0.0f, 0.0f);
        setGain(1.0f);

        if (SoundManager.getInstance().getSoundSources().containsKey(name)) {
            Debug.log(Debug.LogLevel.ERROR, "SoundSource with the name of " + name + " already exists! This one won't be added..");
            cleanup();
            return;
        }

        SoundManager.getInstance().getSoundSources().put(name, this);
    }

    public boolean isPlaying() {
        return getState() == SoundState.PLAY;
    }

    public SoundSource setState(SoundState state) {
        switch (state) {
            case PAUSE:
                if (!isPlaying()) alSourcePause(soundSourceID);
                break;
            case PLAY:
                if (isPlaying()) return this;
                alSourcePlay(soundSourceID);
                break;
            case STOP:
                if (isPlaying()) alSourceStop(soundSourceID);
                break;
        }

        return this;
    }

    public SoundState getState() {
        switch (alGetSourcei(soundSourceID, AL_SOURCE_STATE)) {
            case AL_PLAYING:
                return SoundState.PLAY;
            case AL_PAUSED:
                return SoundState.PAUSE;
            case AL_STOPPED:
                return SoundState.STOP;
        }

        return null;
    }

    public float getGain() {
        return alGetSourcef(soundSourceID, AL_GAIN);
    }
    public SoundSource setGain(float gain) {
        alSourcef(soundSourceID, AL_GAIN, gain);
        return this;
    }

    public SoundSource setBuffer(int bufferID) {
        setState(SoundState.STOP);
        alSourcei(soundSourceID, AL_BUFFER, bufferID);
        return this;
    }


    public SoundSource setPosition(float x, float y, float z) {
        alSource3f(soundSourceID, AL_POSITION, x, y, z);
        return this;
    }

    public SoundSource setPosition(Vector3f position) {
        alSource3f(soundSourceID, AL_POSITION, position.x, position.y, position.z);
        return this;
    }

    public SoundSource setLoops(boolean doesLoop) {
        alSourcei(soundSourceID, AL_LOOPING, doesLoop ? AL_TRUE : AL_FALSE);
        return this;
    }

    public SoundSource setIsRelative(boolean isRelative) {
        alSourcei(soundSourceID, AL_SOURCE_RELATIVE, isRelative ? AL_TRUE : AL_FALSE);
        return this;
    }

    @Override
    public void cleanup() {
        setState(SoundState.STOP);
        alDeleteSources(soundSourceID);
    }
}
