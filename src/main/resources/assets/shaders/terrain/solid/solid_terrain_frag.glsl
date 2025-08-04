#version 330

//Uniforms
uniform sampler2D textureId;
uniform float aoIntensity;

//io
in vec2 pTexCoords;
in float pAmbiantOcclusion;
in float pDistance;

out vec4 fragColor;

//Shader Code

void main() {
    vec4 originalColor = texture(textureId, pTexCoords);
    if(originalColor.a < 1)
        discard;

    originalColor.rgb *= mix(1, 0.5, (pAmbiantOcclusion / 3) * aoIntensity);
    fragColor = originalColor;
}
