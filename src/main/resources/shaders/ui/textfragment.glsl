#version 330 core

in vec2 texCoords;
flat in int isText;

out vec4 color;

uniform sampler2D sprite;
uniform vec3 spriteColor;

void main() {
    if (isText == 1) {
        vec4 sampled = vec4(1.0, 1.0, 1.0, texture(sprite, texCoords).r);
        color = vec4(spriteColor, 1.0) * sampled;

        return;
    }

    color = texture(sprite, texCoords);
}