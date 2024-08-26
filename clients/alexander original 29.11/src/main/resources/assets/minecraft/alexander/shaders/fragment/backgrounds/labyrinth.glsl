#extension GL_OES_standard_derivatives : enable

precision highp float;

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;

#define linewidth .095
#define colordistmul .05
#define movespeed .5
#define swaymul 1.0

float fmod(float x, float y)
{
    return x - y * floor(x/y);
}

vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

float Hash21r(vec2 p)
{
    p = fract(p*vec2(123.34, 456.21));
    p += dot(p, p+45.32);
    return fract(p.x*p.y);
}

float Hash21(vec2 p)
{
    int x = int(mod(p.x, 6.0));
    int y = int(mod(p.y, 6.0));
    if(x*x+y*y>25) return Hash21r(p);
    float s=1.;

    if (y == 0||y == 3)  if (x == 0||x == 3) s= 0.0;
    else; else if (y == 2||y == 5) if (x == 2||x == 5) s= 0.0;
    else; else if (x != y) s= 0.0;
    return s;
}

vec3 rainbowstalin(vec2 pos)
{
    float dist = sqrt(dot(pos, pos)) * colordistmul;
    return hsv2rgb(vec3(fmod(dist, 1.0), 0.5, 1.0));
}

void main()
{
    vec2 uv = (gl_FragCoord.xy-.5 * resolution.xy) / resolution.y;
    float c=length(uv);

    vec3 col = vec3(0);

    float scaledtime = time * movespeed;
    uv *= 15.;
    uv += vec2(sin(scaledtime) * swaymul, scaledtime);
    vec2 gv = fract(uv)-.5;
    vec2 id = floor(uv);
    vec3 pointcolor = rainbowstalin(uv);

    float n = Hash21(id);

    if (n<.5) gv.x *= -1.;
    float d = abs(abs(gv.x + gv.y)-.5);
    float mask = smoothstep(.01, -.01, d-linewidth);
    col += mask * pointcolor;

    // if (gv.x>.48 || gv.y>.48) col = vec3(1,0,0);

    gl_FragColor = vec4(col, 1.0);
}