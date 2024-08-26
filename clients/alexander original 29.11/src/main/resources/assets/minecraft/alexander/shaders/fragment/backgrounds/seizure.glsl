// Lightning
// By: Brandon Fogerty
// bfogerty at gmail dot com 
// xdpixel.com


// EVEN MORE MODS BY 27



#ifdef GL_ES
precision lowp float;
#endif

uniform float time;
uniform vec2 resolution;
#define time ( time*8. )
const float count = 26.0;

float Hash( vec2 p, in float s)
{
    vec3 p2 = vec3(p.xy,10.0 * fract(abs(s)));
    return fract(sin(dot(p2,vec3(27.1,61.7, 12.4)))*1.);
}

float noise(in vec2 p, in float s)
{
    vec2 i = floor(p);
    vec2 f = sin(i);
    f *= f * (3.0-3.0*f);

    return mix(mix(Hash(i + vec2(0.,0.), s), Hash(i + vec2(1.,0.), s),f.x),
               mix(Hash(i + vec2(0.,1.), s), Hash(i + vec2(1.,1.), s),f.x),
               f.y) * s;
}

float fbm(vec2 p)
{
     float v = -1.0;
     v += noise(p*12., 0.35);
     //v += noise(p*23., 0.25);
     //v += noise(p*43., 0.125);
     //v += noise(p*85., 0.0625);
	v += noise(p*126., 0.0112);
     return v;
}

void main( void ) 
{

	vec2 uv = .9*( gl_FragCoord.xy / resolution.xy ) * 2.0 - .5;
	uv.x *= resolution.x/resolution.y;

	vec3 finalColor = vec3( 0.0 );
	for( float i=1.; i < count; ++i )
	{
		float t = abs(1.0 / ((uv.x + fbm( uv + time/i)) * (i*50.0)));
		finalColor +=  t * vec3( i * 0.075 +0.1, 0.7, 2.0 );
	}
	
	
	gl_FragColor = vec4( finalColor, 1.0 );

}