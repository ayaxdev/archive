/*
 * Original shader from: https://www.shadertoy.com/view/ld3czS
 */

#ifdef GL_ES
precision mediump float;
#endif

// glslsandbox uniforms
uniform float time;
uniform vec2 resolution;

// shadertoy emulation
#define iTime time
#define iResolution resolution


#define NS 100.
#define CI 0.3

float N21(vec2 p) {
    return fract(sin(p.x*100.+p.y*7446.)*8345.);
}

float SS(vec2 uv) {
    vec2 lv = fract(uv);
    lv = lv*lv*(3.-2.*lv);
    vec2 id = floor(uv);

    float bl = N21(id);
    float br = N21(id+vec2(1., 0.));
    float b = mix(bl, br, lv.x);

    float tl = N21(id+vec2(0., 1.));
    float tr = N21(id+vec2(1., 1.));
    float t = mix(tl, tr, lv.x);

    return mix(b, t, lv.y);
}

float L(vec2 uv, vec2 ofs, float b, float l) {
    return smoothstep(0., 1000., b*max(0.1, l)/pow(max(0.0000000000001, length(uv-ofs)), 1./max(0.1, l)));
}

float rand(vec2 co, float s){
    float PHI = 1.61803398874989484820459;
    return fract(tan(distance(co*PHI, co)*s)*co.x);
}

vec2 H12(float s) {
    float x = rand(vec2(243.234,63.834), s)-.5;
    float y = rand(vec2(53.1434,13.1234), s)-.5;
    return vec2(x, y);
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec2 uv = fragCoord/iResolution.xy;

    uv -= .5;
    uv.x *= iResolution.x/iResolution.y;



    vec4 col = vec4(.0);

    vec4 b = vec4(0.01176470588, 0.05098039215, 0.14117647058, 1.);
    vec4 p = vec4(0.13333333333, 0.07843137254, 0.13725490196, 1.);
    vec4 lb = vec4(0.10196078431, 0.21568627451, 0.33333333333, 1.);

    vec4 blb = mix(b, lb, -uv.x*.2-(uv.y*.5));

    col += mix(blb, p, uv.x-(uv.y*1.5));

    for(float i=0.; i < NS; i++) {

        vec2 ofs = H12(i+1.);
        ofs *= vec2(1.8, 1.1);
        float r = (mod(i, 20.) == 0.)? 0.5+abs(sin(i/50.)): 0.25;
        col += vec4(L(uv, ofs, r+(sin(fract(iTime)*.5*i)+1.)*0.02, 1.));
    }

    uv.x += iTime*.03;


    float c = 0.;

    for(float i = 1.; i < 8.; i+=1.) {
        c += SS(uv*pow(2.,i))*pow(0.5, i);
    }

    col = col + c * CI;

    fragColor = col;
}

void main(void)
{
    mainImage(gl_FragColor, gl_FragCoord.xy);
}