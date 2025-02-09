#version 330

in vec2 outTextureCoords;

uniform sampler2D texture_sampler;

out vec4 fragColor;

void main() {
    fragColor = texture(texture_sampler, outTextureCoords);
}