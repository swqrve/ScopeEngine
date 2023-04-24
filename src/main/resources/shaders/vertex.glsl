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

uniform int isAParticle;

uniform int instanced;

uniform float atlasWidthSize;
uniform float totalAtlasDimension;

struct Particle {
    mat4 model;
    vec4 color;
};

struct InstancedObject {
    mat4 model;
    float textureID;
};


layout (std430, binding = 0) buffer ParticleSSBO {
    Particle particles[];
};

layout (std430, binding = 1) buffer InstancedSSBO {
    InstancedObject instancedObjects[];
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

    if (instanced == 1) {
        vec4 worldPos = instancedObjects[gl_InstanceID].model * vec4(aPos, 1.0);
        gl_Position = projection * view * worldPos;

        normal = normalize(worldPos).xyz;
        fragPos = worldPos.xyz;

        float tileWidthNormalized = atlasWidthSize / totalAtlasDimension;

        float leftTextureCoord = instancedObjects[gl_InstanceID].textureID * tileWidthNormalized;
        float rightTextureCoord = leftTextureCoord + tileWidthNormalized;
        texCoords = aTexCoords + vec2(leftTextureCoord, 0.0);
        texCoords.x = clamp(texCoords.x, leftTextureCoord, rightTextureCoord);

        //  texCoords = vec2(aTexCoords.x * instancedObjects[gl_InstanceID].textureID, aTexCoords.y);
        // texCoords = aTexCoords;

        return;
    }

    vec4 worldPos = model * vec4(aPos, 1.0);
    gl_Position = projection * view * worldPos;

    normal = normalize(worldPos).xyz;
    fragPos = worldPos.xyz;

    texCoords = aTexCoords;
}