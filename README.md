# ScopeEngine

Hey! This is my personal game "engine. Currently, it is more of a lightweight game framework that is in active development. Trying to get back into game development and I want to have a framework that I'm comfortable using whenever I'm starting new projects.

### Basic Project info and why it exists:

It is being created with LWJGL.

This project is mainly a framework and not a game engine so it doesn't really have many of the features that you'd expect from an engine. Furthermore, it is being made with a very specific style of game in mind. Specifically 2.5D games. It is meant for games like Wolfenstien 3D with a modern twist, which is why it currently doesn't have any form of model loader (Though a OBJ loader will be added soon). Think of games like *Delver* if you'd like to understand the intentions a bit more.

As of when this readme is being written, it is in a fairly limited state. Currently it supports creating your own models when providing a list of vertices, basic lighting (Point lights, Spot lights, Directional Light), textures, shaders, cameras, skyboxs, etc. This is still the very early state. As of right now, a solid understanding of opengl is necessary to make use of the framework, though this will likely remain the case for any game of even slightly substantial portion. Further below is a preview of just a couple of those mentioned features.


## Usage

If you'd like to check out how to use it check out the game package. It is a bit finicky now and I'm aware of a couple bad design choices that I will be improving in the coming weeks, and I will likely abstract SOME more of the code. I prefer to leave as much control as possible to the game however.

## Preview
![image](https://user-images.githubusercontent.com/59324927/229053567-c51ba313-8e9b-48d5-82eb-7fabb9ee83b3.png)


## Future Features:
All the following or almost all the following are expected in the next month in order to be ready for Ludum Dare in time. They will get checkmarks as they get completed.

```
-Particle System (Basic Version implemented)
-3D Audio
-Raycasting
-AABB Collision Detection
-Basic Raycasting for object Picking and collisions
-Text Rendering
-Entity System (Still not decided how I'm going to implement it, I dislike ECS so I'm working on a effective alternative)
```
There are plans for documentation in the future when it has more features to document..
