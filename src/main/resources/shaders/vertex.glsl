#version 430 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNormal;
layout (location = 2) in vec2 aTexCoords;

out vec2 texCoords;
out vec3 normal;
out vec3 fragPos;

out vec4 particleColor;
flat out int isParticle;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

// Particle Info
uniform int isAParticle;

struct Particle {
    mat4 model;
    vec4 color;
};

layout (std430, binding = 0) buffer ParticleSSBO {
    Particle particles[];
};

void main() {
    isParticle = isAParticle;
    if (isParticle == 1) {
        particleColor = particles[gl_InstanceID].color;

        vec4 worldPos = particles[gl_InstanceID].model * vec4(aPos, 1.0);
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