#version 120

uniform sampler2D texture;
uniform vec2 texelSize;
uniform vec2 direction;

uniform float sigma;

uniform vec3 mixColor = vec3(.2, .9, 0.0);
uniform float ratio = 0.025;

varying vec2 uv;

#define step texelSize * direction
#define radius 1.75 * 5

float gauss(float x, float sigma) {
    float sq = x / sigma;
    return 1.0 / (abs(sigma) * 2.50662827) * exp(-0.5 * sq * sq);
}

void main() {

    vec3 color = vec3(0.0);
    float totalAlpha = 0.0;

    for (float i = -radius; i <= radius; i++) {
        vec4 smpl = texture2D(texture, uv + step * i);
        float alpha = smpl.a * gauss(i, 5);
        color += smpl.rgb * alpha;
        totalAlpha += alpha;
    }

    color /= totalAlpha;
    gl_FragColor = vec4(color, totalAlpha);
}