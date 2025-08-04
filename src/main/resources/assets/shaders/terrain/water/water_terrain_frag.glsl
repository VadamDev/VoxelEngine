#version 330

//consts
const float FRESNEL_POWER = 4;

//uniforms
uniform struct EngineData {
    mat4 projectionViewMatrix;
    vec3 cameraPos;
    float currentFrameTime;
} engineData;

uniform sampler2D textureId;

//io
in vec3 pWorldPos;
in vec2 pTexCoords;
in vec3 pNormal;

out vec4 fragColor;

//Shader

vec4 calculateFresnel(vec4 originalColor) {
    vec3 viewVector = normalize(engineData.cameraPos - pWorldPos);
    float fresnel = 1 - dot(viewVector, pNormal);

    //= below water
    if(fresnel > 1)
        return originalColor;

    //hardcoded deep water color for now
    return mix(originalColor, vec4(0.1, 0.2, 0.31, 0.9), pow(fresnel, FRESNEL_POWER));
}

void main() {
    vec4 originalColor = texture(textureId, pTexCoords);
    fragColor = calculateFresnel(originalColor);
}
