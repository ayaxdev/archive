/*
 * Original shader from: https://www.shadertoy.com/view/7lScRm
 */

#ifdef GL_ES
precision highp float;
#endif

// glslsandbox uniforms
uniform float time;
uniform vec2 resolution;

// shadertoy emulation
#define iTime time
#define iResolution resolution

// Emulate some GLSL ES 3.x
float tanh(float x) {
    float ex = exp(2.0 * x);
    return ((ex - 1.) / (ex + 1.));
}

// --------[ Original ShaderToy begins here ]---------- //
#define pi 3.14159

#define thc(a,b) tanh(a*cos(b))/tanh(a)

float seg(in vec2 p, in vec2 a, in vec2 b) {
    vec2 pa = p-a, ba = b-a;
    float h = clamp( dot(pa,ba)/dot(ba,ba), 0.0, 1.0 );
    return length( pa - ba*h );
}

vec2 pnt(float n, float spd, float t) {      
    //float t = iTime; //floor(spd * time) / spd;
    float f = fract(t);//fract(spd * time);

    vec2 p = vec2(cos(t), sin(t));
    t += n/spd;
    vec2 p2 = vec2(cos(t), sin(t));
    t -= n/spd;
    vec2 p3 = vec2(cos(t), sin(t));
    
    f = 1. - pow(1.-f,4.);
    f = smoothstep(0.,1.,f);
    
    float m = clamp(2. * f, 0., 1.);
    float m2 = clamp(2. * f, 1., 2.) - 1.;
    return 0.45 * mix(mix(p,p2,m), p3, m2);
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec2 uv = (fragCoord - 0.5 * iResolution.xy) / iResolution.y;

    const float spd = 4.;

    float s = 0.;

    float k = 1. / iResolution.y;
    const float n = spd * 4.;
    for (float i = 0.; i < n; i++) {
        float io = 2. * pi * i / n;
        vec2 p = pnt(1., spd, 0.5 * iTime + io);
        p *= (0.75 + 0.25 * thc(4., 4. * io + 0.5 * iTime));
        float o2 = 1./n;
        vec2 p2 = pnt(1., spd, 0.5 * iTime + io + o2);       
        p2 *= (0.75 + 0.25 * thc(4., 0.5 * iTime));
        
        float d = seg(uv, p, p2);// length(uv - p);
        s = max(s - 0.5 * s * cos(i * pi / n + iTime), smoothstep(-k, k, -d + 0.015));
        
    }
  
    vec3 col = vec3(s);
    col += 0.22 * exp(-1. * (uv.y + 0.5)); 
    fragColor = vec4(col,1.0);
}
// --------[ Original ShaderToy ends here ]---------- //

void main(void)
{
    mainImage(gl_FragColor, gl_FragCoord.xy);
}