/*
 * Original shader from: https://www.shadertoy.com/view/7stcRS
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
struct bubble
{
    vec2 pos;
    float radius;
};

const vec4 bubble_blue = vec4(0.017, 0.673, 0.743, 1.);
const vec4 bubble_pale = vec4(0.57, 0.673, 0.743, 1.);
const vec4 bubble_indigo = vec4(0.27, 0.073, 0.543, 1.);

vec4 render_bubble(in vec2 coord, in bubble object)
{
    float zw = distance(coord, object.pos) / (object.radius*2.);
    float alpha = 1.-smoothstep(0.49, 0.59, zw);

    vec4 color = bubble_blue;
    
    color = mix(color, bubble_indigo, (5.*sqrt(distance(coord, object.pos)))/object.radius);
    color = 
        mix(
            color, 
            bubble_pale, 
            max(
                (coord.y - object.pos.y) / object.radius,
                (object.pos.y - coord.y) / object.radius
            )
        );
    vec2 lw = (coord - object.pos) / object.radius;
        
    vec4 wlc = vec4(0.5 + 0.5*cos(iTime+(coord/iResolution.yx).xyx/0.1+vec3(atan(lw.y,lw.x),2,4)), 1.);
    color = mix(color, wlc, (5.*sqrt(distance(coord, object.pos)))/object.radius);
        
    float ralpha = (
                alpha * 
                max(
                    smoothstep(0.,1.,zw), 
                    (coord.y - object.pos.y) / object.radius
                )
            );
            
    return vec4(color.rgb+(ralpha/2.), ralpha);
    
}

float fluctuation(float r, float r2)
{
    return sin(10.*r + iTime/(2.+(r*5.))) * (30. + (60.*r2));
}



float prand(in vec2 n)
{
    return fract(sin(dot(n, vec2(12.9898, 4.1414))) * 43758.5453);
}

float prand(in float modifier)
{
    return prand(vec2(modifier, 1.));
}

float noise(vec2 p){
	vec2 ip = floor(p);
	vec2 u = fract(p);
	u = u*u*(3.0-2.0*u);
	
	float res = mix(
		mix(prand(ip),prand(ip+vec2(1.0,0.0)),u.x),
		mix(prand(ip+vec2(0.0,1.0)),prand(ip+vec2(1.0,1.0)),u.x),u.y);
	return res*res;
}

const vec4 water_color = vec4(0.12, 0.21, 0.613, 1.);
const vec4 water_color_2 = vec4(0.12, 0.41, 0.713, 1.);

mat2 rmat(float w){
    return mat2(vec2(cos(w), -sin(w)), vec2(sin(w), cos(w)));
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec2 uv = fragCoord/iResolution.xy;
    
    vec2 c = (uv-(vec2(0.5, 1.)));
    float m = atan(c.y,c.x);
    float rayx = sin(m)*cos(m);
    
    fragColor = mix(water_color, water_color_2, smoothstep(0.,1.,noise(vec2(rayx*20. + iTime, 1.))));
    
    fragColor = mix(fragColor, vec4(0.), 1.-uv.y);
    for (int i = 1; i < 25; ++i)
    {
        float bw = float(i);
        float r1 = prand(bw);
        float r2 = prand(bw + .21);
        float r3 = prand(bw + .69);
        float r4 = prand(bw + .82) + 0.1;
        bubble w = 
            bubble(
                vec2(
                    r1 * iResolution.x + fluctuation(r1, r2), 
                    mod(((10.+(50.*r4))*iTime*(0.3+prand(bw+.11)*1.9)) - (r3*iResolution.y), iResolution.y + 100.) - 50.
                ),
                40.+(prand(bw+.91)*10.)*(iResolution.x/500.)
            );
            
        vec4 c = render_bubble(fragCoord, w);
        fragColor = vec4(mix(fragColor.rgb, c.rgb, c.a), 1.);
    }
}
// --------[ Original ShaderToy ends here ]---------- //

void main(void)
{
    mainImage(gl_FragColor, gl_FragCoord.xy);
}