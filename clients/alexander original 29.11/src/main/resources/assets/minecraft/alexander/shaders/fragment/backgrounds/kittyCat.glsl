/*
 * Original shader from: https://www.shadertoy.com/view/cd33R8
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
#define PI 3.14159


vec2 rotate2D( in vec2 uv, in float angle )
{
    mat2 transform = mat2(
        cos(angle), -sin(angle),
        sin(angle), cos(angle)
    );
    return uv * transform;
}


/* Primitive SDFs by iq https://iquilezles.org/articles/distfunctions2d/ */

float sdCircle( in vec2 p, in float r, in vec2 origin )
{
    return length(p - origin) - r;
}

float sdTriangle( in vec2 p, in float size, in float rotation, in vec2 origin )
{
    // Transformations
    p = rotate2D(p, rotation);
    p -= origin;
    p /= size;
    

    const float k = sqrt(3.0);
    p.x = abs(p.x) - 1.0;
    p.y = p.y + 1.0/k;
    if( p.x+k*p.y>0.0 ) p = vec2(p.x-k*p.y,-k*p.x-p.y)/2.0;
    p.x -= clamp( p.x, -2.0, 0.0 );
 
    
    return -length(p)*sign(p.y) * size;
}

float dot2( in vec2 v ) { return dot(v,v); }

float sdBezier( in vec2 pos, in vec2 A, in vec2 B, in vec2 C )
{    
    vec2 a = B - A;
    vec2 b = A - 2.0*B + C;
    vec2 c = a * 2.0;
    vec2 d = A - pos;
    float kk = 1.0/dot(b,b);
    float kx = kk * dot(a,b);
    float ky = kk * (2.0*dot(a,a)+dot(d,b)) / 3.0;
    float kz = kk * dot(d,a);      
    float res = 0.0;
    float p = ky - kx*kx;
    float p3 = p*p*p;
    float q = kx*(2.0*kx*kx-3.0*ky) + kz;
    float h = q*q + 4.0*p3;
    if( h >= 0.0) 
    { 
        h = sqrt(h);
        vec2 x = (vec2(h,-h)-q)/2.0;
        vec2 uv = sign(x)*pow(abs(x), vec2(1.0/3.0));
        float t = clamp( uv.x+uv.y-kx, 0.0, 1.0 );
        res = dot2(d + (c + b*t)*t);
    }
    else
    {
        float z = sqrt(-p);
        float v = acos( q/(p*z*2.0) ) / 3.0;
        float m = cos(v);
        float n = sin(v)*1.732050808;
        vec3  t = clamp(vec3(m+m,-n-m,n-m)*z-kx,0.0,1.0);
        res = min( dot2(d+(c+b*t.x)*t.x),
                   dot2(d+(c+b*t.y)*t.y) );
        // the third root cannot be the closest
        // res = min(res,dot2(d+(c+b*t.z)*t.z));
    }
    return sqrt( res );
}


/* Bound SDF for a cat's face from primitives */
float sdCat(in vec2 uv)
{
    float head = sdCircle(uv, 0.36, vec2(0.0, -0.1));
    float earLeft = sdTriangle(uv, 0.2, -0.6, vec2(-0.1, 0.29));
    float earRight = sdTriangle(uv, 0.2, 0.6, vec2(0.1, 0.29));
    float nose = sdTriangle(uv, 0.03, PI, vec2(0.0, 0.2));
    float eyeLeft = max(
            sdCircle(uv, 0.045, vec2(0.1, -0.09)),
            -sdCircle(uv, 0.009, vec2(0.095, -0.082))
    );
    float eyeRight = max(
            sdCircle(uv, 0.045, vec2(-0.1, -0.09)),
            -sdCircle(uv, 0.009, vec2(-0.105, -0.082))
    );
    float mouthLeft = sdBezier(uv, vec2(0.0,-0.23), vec2(-0.05,-0.33), vec2(-0.1,-0.28));
    float mouthRight = sdBezier(uv, vec2(0.0,-0.23), vec2(0.05,-0.33), vec2(0.1,-0.28));
    
    // Put it all together
    float cat = min(head, min(earLeft, earRight));
    cat = max(cat, -nose);
    cat = max(max(cat, -eyeLeft), -eyeRight);
    cat = max(max(cat, -mouthLeft), -mouthRight);
    
    // Whiskers
    for (float y = -0.22; y <= -0.17; y += 0.02) {
        float whiskerLeft = sdBezier(uv, vec2(-0.1, y), vec2(-0.25, y + 0.03),
                vec2(-0.45, y - 0.9 * (-0.15 - y)));
        float whiskerRight = sdBezier(uv, vec2(0.1, y), vec2(0.25, y + 0.03),
                vec2(0.45, y - 0.9 * (-0.15 - y)));
        
        // The whiskers being both outside and inside the face makes this weird...
        cat = cat > 0.0 ? min(cat, whiskerLeft) : max(cat, -whiskerLeft);
        cat = cat > 0.0 ? min(cat, whiskerRight) : max(cat, -whiskerRight);
    }
    
    return cat;
}

/* Version without details */
float sdSimpleCat(vec2 uv)
{
    float head = sdCircle(uv, 0.36, vec2(0.0, -0.1));
    float earLeft = sdTriangle(uv, 0.2, -0.6, vec2(-0.1, 0.29));
    float earRight = sdTriangle(uv, 0.2, 0.6, vec2(0.1, 0.29));
    
    float cat = min(head, min(earLeft, earRight));
    
    return cat;
}


void mainImage( out vec4 fragColor, in vec2 fragCoord )
{
    // Normalized pixel coordinates (from 0 to 1)
    vec2 uv = fragCoord/iResolution.xy;
    
    // Center and correct for aspect ratio
    uv -= 0.5;
    uv.x *= iResolution.x / iResolution.y;
    
    uv.y -= 0.03;

    float cat = sdCat(rotate2D(uv, sin(iTime) / 8.0));
    float outline = 1.0 - smoothstep(0.002, 0.006, abs(cat));
    float inside = step(0.0, -cat);

	// coloring
    /*float d = cat*2.0;
    vec3 col = (d>0.0) ? vec3(0.9,0.6,0.3) : vec3(0.65,0.85,1.0);
    col *= 1.0 - exp(-6.0*abs(d));
	col *= 0.8 + 0.2*cos(150.0*d);
	col = mix( col, vec3(1.0), 1.0-smoothstep(0.0,0.01,abs(d)) );*/
    
    vec3 col = vec3(0.0, 0.0, 0.1);
    
    vec2 bgPatternUV = fract(rotate2D(uv + iTime * 0.1, -0.4) * 10.0) - 0.5;
    float bgPattern = smoothstep(-0.01, 0.01, sdSimpleCat(bgPatternUV));
    vec3 bgColor = mix(vec3(uv * 0.2, 0.2), vec3(uv * 0.6 + 0.4, 1.0), bgPattern);
    col = mix(col, bgColor, clamp(cat * 5.0, 0.0, 1.0));
    
    vec3 insideColor = vec3(uv * 0.8 + 0.8, 1.0);
    col = mix(col, insideColor, inside);
    
    vec3 lineColor = vec3(uv * 1.8 + 0.6, 1.0);
    col = mix(col, lineColor, outline);

    // Output to screen
    fragColor = vec4(col,1.0);
}
// --------[ Original ShaderToy ends here ]---------- //

void main(void)
{
    mainImage(gl_FragColor, gl_FragCoord.xy);
}