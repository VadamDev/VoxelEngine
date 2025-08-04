#version 330

//Vertices Data
layout(location = 0) in vec3 inPosition;
layout(location = 1) in vec2 inTexCoords;
layout(location = 2) in vec3 inNormal;
layout(location = 3) in int inAO;

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
    pTexCoords = inTexCoords;
    pAmbiantOcclusion = inAO; //normalized in frag shader

    gl_Position = engineData.projectionViewMatrix * vec4(inPosition + chunkPos, 1);
}
