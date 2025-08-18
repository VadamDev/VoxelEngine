#version 330

//Vertices Data
layout(location = 0) in vec3 aPosition;
layout(location = 1) in vec2 aTexCoords;
layout(location = 2) in vec3 aNormal;
layout(location = 3) in float aAO;

//Uniforms
uniform struct EngineData {
    mat4 projectionViewMatrix;
    vec3 cameraPos;
    float currentFrameTime;
} engineData;
uniform ivec3 chunkPos;

//Out
out vec2 pTexCoords;
out float pAmbiantOcclusion;

//Shader

void main() {
    pTexCoords = aTexCoords;
    pAmbiantOcclusion = aAO; //normalized in frag shader

    gl_Position = engineData.projectionViewMatrix * vec4(aPosition + chunkPos, 1);
}
