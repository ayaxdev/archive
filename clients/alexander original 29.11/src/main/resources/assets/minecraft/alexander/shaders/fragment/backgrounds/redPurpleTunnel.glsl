/*
 * Original shader from: https://www.shadertoy.com/view/sdcGRX
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

// Emulate a black texture
#define texture(s, uv) vec4(0.1)

// --------[ Original ShaderToy begins here ]---------- //
#define sat(a) clamp(a, 0., 1.)
#define FFT(a) texture(iChannel1, vec2(a, 0.)).x

mat2 r2d(float a) { float c = cos(a), s = sin(a); return mat2(c, -s, s, c); }

vec2 _min(vec2 a, vec2 b)
{
    if (a.x < b.x)
        return a;
    return b;
}

vec2 _max(vec2 a, vec2 b)
{
    if (a.x > b.x)
        return a;
    return b;
}

vec2 map(vec3 p)
{
    vec3 op = p;
    vec2 acc = vec2(1000.,-1.);
    
    float an = atan(p.y, p.x);
    p.xy -= vec2(sin(p.z+iTime), cos(p.z*.5+iTime))*.5;
    p.y += sin(p.z*2.+iTime)*.1;
    float rad = FFT(abs(p.z*.001))*.25;
    vec2 tube = vec2(-(length(p.xy)-2.-rad+sin(p.z*.25)), 0.);
    acc = _min(acc, tube);
    
    //acc = _min(acc, _max(tube, vec2((sin(an*1.+op.z*3.)-.8), 1.)));
    
    return acc;
}
vec3 accCol;
vec3 trace(vec3 ro, vec3 rd, int steps)
{
    accCol = vec3(0.);
    vec3 p = ro;
    for (int i = 0; i < 256; ++i)
    {
        vec2 res = map(p);
        if (res.x < 0.01)
            return vec3(res.x, distance(p, ro), res.y);
       // if (res.y == 1.)
            accCol += vec3(1., .5, sin(p.z)*.5+.5)*pow(1.-sat(res.x/.7), 30.)*.3;
        p += rd*res.x*.7;
    }
    return vec3(-1.);
}
vec3 getCam(vec3 rd, vec2 uv)
{
    float fov = 1.;
    vec3 r = normalize(cross(rd, vec3(0.,1.,0.)));
    vec3 u = normalize(cross(rd, r));
    return normalize(rd+fov*(r*uv.x+u*uv.y));
}

vec3 getNorm(vec3 p, float d)
{
    vec2 e = vec2(0.01, 0.);
    return normalize(vec3(d)-vec3(map(p-e.xyy).x, map(p-e.yxy).x, map(p-e.yyx).x));
}

float _sqr(vec2 p, vec2 s)
{
    vec2 l = abs(p)-s;
    return max(l.x, l.y);
}

vec3 rdr(vec2 uv)
{
    vec3 col = vec3(1.);
    float t= iTime*2.;
    vec3 ro = vec3(sin(iTime)*.15,cos(iTime*.5)*.12,-12.+t);
    vec3 ta = vec3(0.,0.,0.+t);
    vec3 rd = normalize(ta-ro);
    rd.xz *= r2d(sin(iTime*.5)*.15);
    rd.yz *= r2d(sin(iTime+.5)*.15);
    rd = getCam(rd, uv);
    vec3 res = trace(ro, rd, 256);
    if (res.y > 0.)
    {
        vec3 p = ro+rd*res.y;
        vec3 n = getNorm(p, res.x);
        col = n*.5+.5;
        vec3 lpos = vec3(0.);
        vec3 ldir = p-lpos;
        col = sat(dot(normalize(ldir), n))*vec3(1.);
        col += accCol;
        col = pow(col, vec3(3.));
        float an = atan(p.y, p.x);
        vec2 rep = vec2(.9, .5);
        vec2 luv = vec2(an, p.z+iTime);
        vec2 id = floor((luv+.5*rep)/rep);
        luv.x += sin(id.y*.5)*iTime*2.;
        luv = mod(luv+.5*rep, rep)-.5*rep;
        float shape = _sqr(luv, vec2(5.4*pow(FFT(abs(id.y*.01)),5.), .05));
        vec3 rgb = mix(col, vec3(1.), 1.-sat(shape*50.));
        rgb += pow(FFT(.0),2.)*2.*vec3(1., .5, sin(p.z*10.)*.5+.5)*(1.-sat(shape*1.))*(1.-sat(length(uv*1.)));
        col = mix(col, rgb, sin(iTime*5.+p.z*.5)*.5+.5);
        col += 0.2*texture(iChannel0, vec2(atan(p.y, p.x)*2., length(p.xy*.1)-.25*iTime)*.1).xyz;
        col = mix(col, col.zyx, sin(iTime*1.+p.z*.1)*.5+.5);
        //col = mix(col, col*texture(iChannel0, vec2(atan(p.y, p.x)*2., length(p.xy*.1)-iTime)*.25).xxx, 1.-sat(length(uv*2.)));

    }

    return col;
}

vec2 messupUV(vec2 uv)
{
       vec2 ouv = uv;
    uv += vec2(.1, 0.);
    uv.x = abs(uv.x);
    uv *= r2d(iTime*.25);
    //uv += vec2(.3, 0.);
    uv.y = abs(uv.y);
    uv *= r2d(-iTime*.5);
     uv *= r2d(.2*iTime+uv.x);
    uv *= sin(iTime*.15);
   
    vec2 uv3 = uv*15.*uv.yx*r2d(length(uv));
    return mix(mix(ouv, uv, sin(iTime*.2)), uv3, sin(iTime*.1));
}

void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    vec2 ouv = fragCoord.xy/iResolution.xy;

    vec2 uv = (fragCoord-vec2(.5)*iResolution.xy)/iResolution.xx;
    //uv = messupUV(uv);
    vec3 col = rdr(uv);
    col *= 1.+pow(FFT(.1), 1.)*2.;
    col = mix(col, texture(iChannel2, ouv).xyz, .5+pow(FFT(.2), 2.)*.5);
    fragColor = vec4(col,1.0);
}
// --------[ Original ShaderToy ends here ]---------- //

void main(void)
{
    mainImage(gl_FragColor, gl_FragCoord.xy);
}