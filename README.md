# ScopeEngine

Hey! This is my personal game "engine. Currently, it is more of a lightweight game framework that is in active development. Trying to get back into game development and I want to have a framework that I'm comfortable using whenever I'm starting new projects.

### Basic Project info and why it exists:

It is being created with LWJGL.

This project is mainly a framework and not a game engine so it doesn't really have many of the features that you'd expect from an engine. Furthermore, it is being made with a very specific style of game in mind. Specifically 2.5D games. It is meant for games like Wolfenstien 3D with a modern twist, which is why it currently doesn't have any form of model loader (Though a OBJ loader will be added soon). Think of games like *Delver* if you'd like to understand the intentions a bit more.

It is in a fairly limited state. Currently it supports creating your own models when providing a list of vertices, basic lighting (Point lights, Spot lights, Directional Light), textures, shaders, cameras, skyboxs, particles, collision detection, raycasting, 3D audio, text rendering, etc. This is still the very early state. As of right now, a solid understanding of opengl is necessary to make use of the framework, though this will likely remain the case for any game of even slightly substantial portion. Further below is a preview of just a couple of those mentioned features.

## Usage

If you'd like to check out how to use it check out the game package. It is a bit finicky now and I'm aware of a couple bad design choices that I will be improving in the coming weeks, and I will likely abstract SOME more of the code. I prefer to leave as much control as possible to the game however.

## Preview
![image](https://user-images.githubusercontent.com/59324927/229053567-c51ba313-8e9b-48d5-82eb-7fabb9ee83b3.png)

*Particle System Current Version*

![ezgif-4-2b48d856e8](https://user-images.githubusercontent.com/59324927/229319856-02551d80-30c5-4d8b-b095-713647bf30bd.gif)
![ezgif-4-b8123365a3](https://user-images.githubusercontent.com/59324927/230031237-5c8dbf57-3a0a-45b9-973d-647d89b95d6e.gif)

```
        ParticleSetting setting = new ParticleSetting()
                .setBasePosition(particleBasePosition.x, particleBasePosition.y, particleBasePosition.z)
                .setBaseVelocity(0.25f, 1.0f, 0.25f)
                .setStartingColor(47 / 255.0f, 130 / 255.0f, 186 / 255.0f, 1.0f)
                .setFinalColor( 151 / 255.0f, 214 / 255.0f, 255 / 255.0f, 0.0f )
                .setColorStartXVariation(1.1f)
                .setColorStartYVariation(1.2f)
                .setColorStartZVariation(1.1f)
                .setColorEndXVariation(1.1f)
                .setColorEndYVariation(1.2f)
                .setColorEndZVariation(1.1f)
                .setVelocityDisplacement(2.0f)
                .setStartSize(0.4f)
                .setEndSize(0.0f)
                .setSizeDisplacementMax(1.0f)
                .setSizeDisplacementMin(0.4f)
                .setLifeTime(1.0f)
                .setRotation(0.0f)
                .setBillboard(true)
                .setAffectedByLight(false)
                .setEmitsLight(false)
                .setShrinking(true)
                .setMaterial(new Material(Material.StandardMaterial.EMERALD));

        ParticleSystem system = new ParticleSystem(defaultShader, setting,202);
```

*3D Audio Current Version*
```
source = new SoundSource("song") // TODO: Make soundbuffer source files non absolute file paths
                .setBuffer(new SoundBuffer("sounds/comedy-Tricker.ogg").getBufferID())
                .setGain(0.10f)
                .setLoops(true)
                .setIsRelative(true) // Disables the 3D Audio Aspect!
                .setState(SoundSource.SoundState.PLAY);

        source = new SoundSource("creak")
                .setBuffer(new SoundBuffer("sounds/creak.ogg").getBufferID())
                .setGain(0.10f)
                .setLoops(false)
                .setIsRelative(false)
                .setPosition(particleBasePosition);
```

```
        // Set the UI Camera! You can also just directly pass a camera to the render call for either the TextManager or text source!
        TextManager.getInstance().setCurrentCamera(camera); 
        
        // The first argument is the name, similar to the ModelManager and lets you make render calls without a reference to the original text source.
        textSource = new TextSource("arial", "fonts/arial.ttf", 0, 48);
        
        // This example uses a render call using the default UI Camera! (Shader, String to render, X, Y, Scale, RGB Values)
        textSource.renderText(textShader, "FPS: " + ScopeEngine.getInstance().getEngineManager().getFps(), 0.0f, 600.0f - 25.0f, 0.5f, 0.0f, 0.0f, 0.0f);
```

## Future Features:
All the following or almost all the following are expected in the next month in order to be ready for Ludum Dare in time. They will get checkmarks as they get completed.

```
-Particle System (Version 1 Implemented) ✔️
-AABB Collision Detection (Limited Version Implemented) ✔️
-Raycasting (Version 1 Implemented) ✔️
-3D Audio (Version 1 Implemented) ✔️
-Text Rendering (Limited Version Implemented, Costly!) ✔️
```

There are plans for documentation in the future when it has more features to document..
