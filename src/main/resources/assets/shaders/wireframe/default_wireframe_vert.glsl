#version 330

//Vertices Data
layout(location = 0) in vec3 aPosition;
layout(location = 1) in vec3 iOffset;

//Uniforms
uniform struct EngineData {
    mat4 projectionViewMatrix;
    vec3 cameraPos;
    float currentFrameTime;
} engineData;

//Shader

void main() {
    gl_Position = engineData.projectionViewMatrix * vec4(aPosition + iOffset, 1);
}
