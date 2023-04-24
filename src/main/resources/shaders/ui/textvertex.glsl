#version 430 core
layout (location = 0) in vec4 vertex;

out vec2 texCoords;
flat out int isText;

uniform int aIsText;

uniform mat4 projection;
uniform mat4 model;


void main() {
   isText = aIsText;

   texCoords = vertex.zw;

   if (isText == 1) {
      gl_Position = projection * vec4(vertex.xy, 0.0, 1.0);
      return;
   }

   gl_Position = projection * model * vec4(vertex.xy, 0.0, 1.0);
}