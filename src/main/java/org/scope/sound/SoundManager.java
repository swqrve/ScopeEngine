package org.scope.sound;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALUtil;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.scope.ScopeEngine;
import org.scope.camera.Camera;
import org.scope.framework.Cleanable;
import org.scope.logger.Debug;
import org.scope.sound.type.SoundBuffer;
import org.scope.sound.type.SoundListener;
import org.scope.sound.type.SoundSource;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.sql.Types.NULL;
import static org.lwjgl.openal.AL10.AL_DISTANCE_MODEL;
import static org.lwjgl.openal.AL10.alDistanceModel;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.openal.ALC11.*;
import static org.lwjgl.stb.STBVorbis.*;

public class SoundManager implements Cleanable {
    @Getter private static SoundManager instance = null;

    @Getter private final List<SoundBuffer> soundBuffers = new ArrayList<>();
    @Getter private final Map<String, SoundSource> soundSources = new HashMap<>();

    @Getter @Setter private SoundListener listener;

    @Getter private long audioContext;
    @Getter private long audioDevice; // TODO: Add audio device switching functionality

    public void init(String soundDeviceType) {
        instance = this;

        audioDevice = alcOpenDevice(soundDeviceType);
        if (audioDevice == NULL) {
            Debug.log(Debug.LogLevel.FATAL, "Failed to initialize the sound device of type " + (soundDeviceType == null ? "default" : soundDeviceType) + ".", true);
            ScopeEngine.getInstance().end();
            return;
        }

        audioContext = alcCreateContext(audioDevice, (IntBuffer) null);
        if (audioContext == NULL) {
            Debug.log(Debug.LogLevel.FATAL, (soundDeviceType == null ? "default" : soundDeviceType) + " device failed to create context!", true);
            ScopeEngine.getInstance().end();
            return;
        }

        if (!alcMakeContextCurrent(audioContext)) {
            Debug.log(Debug.LogLevel.FATAL, "Error making alcContextCurrent!");
            ScopeEngine.getInstance().end();
            return;
        }

        ALCCapabilities deviceCapabilities = ALC.createCapabilities(audioDevice);
        if (!deviceCapabilities.OpenALC10) {
            Debug.log(Debug.LogLevel.FATAL, (soundDeviceType == null ? "default" : soundDeviceType) + " does not support openAl!", true);
            ScopeEngine.getInstance().end();
            return;
        }

        AL.createCapabilities(deviceCapabilities);

        if (deviceCapabilities.OpenALC11) {
            List<String> devices = ALUtil.getStringList(NULL, ALC_ALL_DEVICES_SPECIFIER);
            if (devices != null) for (int i = 0; i < devices.size(); i++) {
              Debug.log(Debug.LogLevel.INFO, i + ": " + devices.get(i), true);
            }
        }

        // TODO: Add preference for announcing these debug messages
        Debug.log(Debug.LogLevel.INFO, "OpenALC10  : " + true);
        Debug.log(Debug.LogLevel.INFO, "OpenALC11  : " + deviceCapabilities.OpenALC11);
        Debug.log(Debug.LogLevel.INFO, "ALC_EXT_EFX: " + deviceCapabilities.ALC_EXT_EFX);
        Debug.log(Debug.LogLevel.INFO,"ALC_FREQUENCY     : " + alcGetInteger(audioDevice, ALC_FREQUENCY) + "Hz");
        Debug.log(Debug.LogLevel.INFO,"ALC_REFRESH       : " + alcGetInteger(audioDevice, ALC_REFRESH) + "Hz");
        Debug.log(Debug.LogLevel.INFO,"ALC_SYNC          : " + (alcGetInteger(audioDevice, ALC_SYNC) == ALC_TRUE));
        Debug.log(Debug.LogLevel.INFO,"ALC_MONO_SOURCES  : " + alcGetInteger(audioDevice, ALC_MONO_SOURCES));
        Debug.log(Debug.LogLevel.INFO,"ALC_STEREO_SOURCES: " + alcGetInteger(audioDevice, ALC_STEREO_SOURCES));

        setAttenuationModel(AL_DISTANCE_MODEL);

        listener = new SoundListener(0.0f, 0.0f, 0.0f);
    }

    public SoundSource getSoundSource(String sourceName) {
        if (!soundSources.containsKey(sourceName)) {
            Debug.log(Debug.LogLevel.WARN, "Could not find a sound source with name " + sourceName + ". Will return null!");
            return null;
        }

        return soundSources.get(sourceName);
    }

    public void removeSoundSource(String name) {
        if (!soundSources.containsKey(name)) {
            Debug.log(Debug.LogLevel.WARN, "You're trying to remove a key by name " + name + " that doesn't exist!");
            return;
        }

        soundSources.get(name).cleanup();
        soundSources.remove(name);
    }

    public void setAttenuationModel(int model) {
        alDistanceModel(model);
    }

    public void updateListener() {
        listener.updateListener(Camera.getCurrentCamera());
    }

    @SneakyThrows
    public static ShortBuffer readVorbis(String filePath, STBVorbisInfo info) {
        if (filePath.charAt(0) != '/') filePath = "/" + filePath;
        System.out.println(Paths.get(SoundManager.class.getResource(filePath).toURI()).toFile().getAbsolutePath().replaceAll("/", "\\"));

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer error = stack.mallocInt(1);
            long decoder = stb_vorbis_open_filename(Paths.get(SoundManager.class.getResource(filePath).toURI()).toFile().getAbsolutePath().replaceAll("/", "\\"), error, null);
            if (decoder == NULL) {
                Debug.log(Debug.LogLevel.ERROR, "Failed to open Ogg Vorbis file. Error: " + error.get(0));
                ScopeEngine.getInstance().end();
                return null;
            }

            stb_vorbis_get_info(decoder, info);

            int channels = info.channels();
            int lengthSamples = stb_vorbis_stream_length_in_samples(decoder);

            ShortBuffer result = MemoryUtil.memAllocShort(lengthSamples * channels);

            result.limit(stb_vorbis_get_samples_short_interleaved(decoder, channels, result) * channels);
            stb_vorbis_close(decoder);

            return result;
        }
    }

    @Override
    public void cleanup() {
        if (alcGetCurrentContext() == audioContext) alcMakeContextCurrent(NULL);

        soundSources.values().forEach(SoundSource::cleanup);
        soundBuffers.forEach(SoundBuffer::cleanup);

        if (audioContext != NULL) alcDestroyContext(audioContext);
        if (audioDevice != NULL) alcCloseDevice(audioDevice);

        soundSources.clear();
        soundBuffers.clear();
    }
}
