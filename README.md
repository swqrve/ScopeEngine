# ScopeEngine

Hey! This is my personal game "engine". Currently, it is more of a lightweight game framework that is in development. Trying to get back into game development and I want to have a framework that I'm comfortable using whenever I'm starting new projects.

### Basic Project info and why it exists:

It is being created with LWJGL.

This project is mainly a framework and not a game engine so it doesn't really have many of the features that you'd expect from an engine. Furthermore, it is being made with a very specific style of game in mind. Specifically 2.5D games. It is meant for games like Wolfenstien 3D with a modern twist, which is why it currently doesn't have any form of model loader (Though a OBJ loader will be added soon). Think of games like *Delver* if you'd like to understand the intentions a bit more.

It is usable, but very limited. Though (as of 4/30/23) it now has all the features I've wanted to implement before starting on a project I have in mind.

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
source = new SoundSource("song")
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

*Text Rendering Current Version*

```
        // Set the UI Camera! You can also just directly pass a camera to the render call for either the TextManager or text source!
        TextManager.getInstance().setCurrentCamera(camera); 
        
        // The first argument is the name, similar to the ModelManager and lets you make render calls without a reference to the original text source.
        textSource = new TextSource("arial", "fonts/arial.ttf", 0, 48);
        
        // This example uses a render call using the default UI Camera! (Shader, String to render, X, Y, Scale, RGB Values)
        textSource.renderText(textShader, "FPS: " + ScopeEngine.getInstance().getEngineManager().getFps(), 0.0f, 600.0f - 25.0f, 0.5f, 0.0f, 0.0f, 0.0f);
```

## Example Game!

This is a pretty rough example game made in around 5 hours. The code is not an example of how to make a good game whatsoever, and is quite a mess. However, it is understandable and can be used to understand how the engine works. Last warning though, don't use the code in an actual game haha. The source code can be found here: https://github.com/swqrve/ScopeExampleGame

![image](https://user-images.githubusercontent.com/59324927/233926252-8084621f-d772-4b5d-a0f0-68705c88784d.png)
![image](https://user-images.githubusercontent.com/59324927/233926648-0db3ed77-d6f7-4d3a-8064-9bece8826e27.png)


## Included Large Features:
These are all the large or more notable features, simpler features like rendering, window management, debug logging, etc. are not included in this list.

```
-Particle System (Version 1 Implemented) ✔️
-AABB Collision Detection (Version 1 Implemented) ✔️
-Raycasting (Version 1 Implemented) ✔️
-3D Audio (Version 1 Implemented) ✔️
-Text Rendering (Limited Version Implemented, Costly!) ✔️
-2D Animation (Version 1 Implemented) ✔️
```

There are no current plans for documentation since I never really planned on having other users make use of the engine and I've tried to ensure the code is more than readable enough to be interpreted without it. 
If someone were to PR documentation, it would be appreciated.
