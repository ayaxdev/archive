/*
 * Original shader from: https://www.shadertoy.com/view/7tVBW1
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

// --------[ Original ShaderToy begins here ]---------- //
/*
Forget Gas heating, invest into GPU Heating !
*/
vec3 erot(vec3 p,vec3 ax,float t){return mix(dot(ax,p)*ax,p,cos(t))+cross(ax,p)*sin(t);}
void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
     vec2 uv = (fragCoord.xy -.5* iResolution.xy)/iResolution.y;
    
    vec3 col = vec3(0.);
    
    
    vec3 p,d=normalize(vec3(uv,1.));

    float e=0.,g=0.;
    for(float i=0.;i<99.;i++){
        p = d*g;
        vec3 op=p;
        p.z -=15.+sin(iTime);
        
        float v=0.3,qq=0.;
        for(int i=0;i<100;i++){
            if (v>=5.) break;
                 // ^-- Thermostat, increase to make GPU hotter
            qq+=abs(dot((sin(op*v)),vec3(.2)/v));           
            op = erot(op,normalize(vec3(-.5,.7,2.7)),iTime*.1+.741);
            op = erot(op,vec3(0.,1.,0.),v+=v);
        }
        float h = length(p)-1.-qq;;
        h = max((abs(p.y)-5.1),abs(qq)-.5);
        g+=e=max(.01,abs(h));
        col += vec3(1.)*.0255/exp(e*e*i);
    }
    col =mix(vec3(.2,.05,.01),vec3(.95,.4,.1),col*col);
    fragColor = vec4(mix(col,sqrt(col),.5),1.0);
                          // ^-- this is the a color normalization technics
                          // based on a new scientific approach 
                          // developed at the International Institute of La RACHE
                          // https://www.la-rache.com/
                          // Scientific paper will come soon
                          
}
// --------[ Original ShaderToy ends here ]---------- //

void main(void)
{
    mainImage(gl_FragColor, gl_FragCoord.xy);
}