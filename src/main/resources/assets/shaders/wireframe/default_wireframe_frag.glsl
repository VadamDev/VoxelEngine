#version 330

//Uniforms
uniform vec3 wireframeColor;

//IO
out vec4 fragColor;

//Shader

void main() {
    fragColor = vec4(wireframeColor, 1);
}
