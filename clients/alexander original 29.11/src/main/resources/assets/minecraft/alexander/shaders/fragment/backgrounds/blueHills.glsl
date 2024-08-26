/*
 * Original shader from: https://www.shadertoy.com/view/flVyDw
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

// --------[ Original ShaderToy begins here ]---------- //
// https://twitter.com/XorDev/status/1563001681063079936

float snoise2D2(vec2 p) {   
    p *= 10.52;
    return sin(p.x)*.5 + cos(p.y)*.5;
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{     
    float t = iTime, s = 0.;
    vec3 col;
    vec3 d = vec3(gl_FragCoord.xy*2.-iResolution.xy,iResolution)/iResolution.x,p=vec3(0.);
    for(float i=0.;i < 2e2;i++) {
        s = exp(mod(i,7.));
        p += d*(p.y + (sin(t)*.1)+ .3 -.2  * snoise2D2((p.xz*.7+t*.2+sin(t)*.4)*s))/s;
    }
    
    col.grb = .5*d+(.03+sin(t)*.02)*++d / length(d.xy-1.3-sin(t)*.2)-.7 / ++p.z - min(.1+p+p,0.).y;
    
    fragColor = vec4(col,1.0);
}
// --------[ Original ShaderToy ends here ]---------- //

void main(void)
{
    mainImage(gl_FragColor, gl_FragCoord.xy);
}