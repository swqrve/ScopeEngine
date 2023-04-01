#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNormal;
layout (location = 2) in vec2 aTexCoords;

// TODO: If we want higher particle system sizes, I need to look into either doing the matrix math in seperate arrays for the position, scaling, rotation and do it here
// TODO: or implement SSBOs https://www.khronos.org/opengl/wiki/Shader_Storage_Buffer_Object which are annoying but perfect for this scenario really
const int PARTICLE_SYSTEM_SIZE = 202; // This may be different on other systems which can be checked in opengl, but this is the max size on my system.

out vec2 texCoords;
out vec3 normal;
out vec3 fragPos;

out vec4 particleColor;
flat out int isParticle;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

// Particle Info
uniform mat4 particleModelMatrices[PARTICLE_SYSTEM_SIZE];
uniform vec4 particleColors[PARTICLE_SYSTEM_SIZE];
uniform int isAParticle;

void main() { // REFACTOR TO CLEAN UP LATER
    isParticle = isAParticle;
    if (isParticle == 1) {
        particleColor = particleColors[gl_InstanceID];

        vec4 worldPos = particleModelMatrices[gl_InstanceID] * vec4(aPos, 1.0);
        gl_Position = projection * view * worldPos;

        normal = normalize(worldPos).xyz;
        fragPos = worldPos.xyz;

        texCoords = aTexCoords;

        return;
    }

    vec4 worldPos = model * vec4(aPos, 1.0);
    gl_Position = projection * view * worldPos;

    normal = normalize(worldPos).xyz;
    fragPos = worldPos.xyz;

    texCoords = aTexCoords;
}