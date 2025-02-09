#version 330

layout(location = 0) in vec3 position;
layout(location = 1) in vec2 textureCoords;

uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;

out vec2 outTextureCoords;

void main() {
    gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 1);
    outTextureCoords = textureCoords / 4;
}