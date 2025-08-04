#version 330

//Vertices Data
layout(location = 0) in vec3 inPosition;
layout(location = 1) in vec2 inTexCoords;
layout(location = 2) in vec3 inNormal;

//Uniforms
uniform struct EngineData {
    mat4 projectionViewMatrix;
    vec3 cameraPos;
    float currentFrameTime;
} engineData;
uniform ivec3 chunkPos;

uniform struct WaveEffect {
    bool enabled;
    float maxDistance;
    float strength;
    float falloff;
} waveEffect;

//Out
out vec3 pWorldPos;
out vec2 pTexCoords;
out vec3 pNormal;

//Shader

float distanceFromCamera(vec3 position) {
    vec3 newCameraPos = vec3(engineData.cameraPos);
    newCameraPos.y = position.y;

    return distance(newCameraPos, position);
}

void applyWaveEffect(inout vec3 position) {
    if(!waveEffect.enabled)
        return;

    if(abs(position.y - engineData.cameraPos.y) > waveEffect.maxDistance)
        return;

    float dst = distanceFromCamera(position);
    if(dst > waveEffect.maxDistance)
        return;

    float distanceFactor = clamp(pow(1 - (dst / waveEffect.maxDistance), waveEffect.falloff), 0, 1);

    float yOffset = sin(position.x + engineData.currentFrameTime) * sin(position.z + engineData.currentFrameTime);
    position.y += yOffset * waveEffect.strength * distanceFactor;
}

void main() {
    vec3 worldPos = inPosition + chunkPos;
    applyWaveEffect(worldPos);

    pWorldPos = worldPos;
    pTexCoords = inTexCoords;
    pNormal = inNormal;

    gl_Position = engineData.projectionViewMatrix * vec4(worldPos, 1);
}
