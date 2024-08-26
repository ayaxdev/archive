#ifdef GL_ES
precision mediump float;
#endif

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;

float rand (in vec2 uv) { return fract(cos(dot(uv,vec2(12.4124,48.4124)))*48512.41241); }
const vec2 O = vec2(2.,1.);
float noise (in vec2 uv) {
	vec2 b = floor(uv);
	return mix(mix(rand(b),rand(b+O.yx),.5),mix(rand(b+O),rand(b+O.yy),.5),.5);
}

#define DIR_RIGHT -1.
#define DIR_LEFT 1.
#define DIRECTION DIR_LEFT

#define LAYERS 8
#define SPEED 50.
#define SIZE 5.

#define PI 3.1415926536
#define CHS 0.18
float sdBox2(in vec2 p,in vec2 b) {vec2 d=abs(p)-b;return length(max(d,vec2(0))) + min(max(d.x,d.y),0.0);}
float line2(float d,vec2 p,vec4 l){vec2 pa=p-l.xy;vec2 ba=l.zw-l.xy;float h=clamp(dot(pa,ba)/dot(ba,ba),0.0,1.0);return min(d,length(pa-ba*h));}
float TB(vec2 p, float d){p.y=abs(p.y);return line2(d,p,vec4(2,3.25,-2,3.25)*CHS);}
float B(vec2 p,float d){p.y+=1.75*CHS;d=min(d,abs(sdBox2(p,vec2(2.0,1.5)*CHS)));p+=vec2(0.5,-3.25)*CHS;return min(d,abs(sdBox2(p,vec2(1.5,1.75)*CHS)));} float E(vec2 p,float d){d=TB(p,d);d=line2(d,p,vec4(-2,3.25,-2,-3.25)*CHS);return line2(d,p,vec4(0,-0.25,-2,-0.25)*CHS);} float I(vec2 p,float d){d=line2(d,p,vec4(0,-3.25,0,3.25)*CHS);p.y=abs(p.y);return line2(d,p,vec4(1.5,3.25,-1.5,3.25)*CHS);} float R(vec2 p,float d){d=line2(d,p,vec4(0.5,-0.25,2,-3.25)*CHS);d=line2(d,p,vec4(-2,-3.25,-2,0.0)*CHS);p.y-=1.5*CHS;return min(d, abs(sdBox2(p,vec2(2.0,1.75)*CHS)));} float T(vec2 p,float d){d=line2(d,p,vec4(0,-3.25,0,3.25)*CHS);return line2(d,p,vec4(2,3.25,-2,3.25)*CHS);} float X(vec2 p,float d){d = line2(d,p,vec4(-2,3.25,2,-3.25)*CHS);return line2(d,p,vec4(-2,-3.25,2,3.25)*CHS);} // DOGSHIT


 vec2 res = vec2(resolution.x,resolution.y);
const mat3 mRot = mat3(0.9553, -0.2955, 0.0, 0.2955, 0.9553, 0.0, 0.0, 0.0, 1.0);
const vec3 ro = vec3(0.0,0.0,-4.0);

const vec3 cRed = vec3(0.15,0.15,0.15);
const vec3 cWhite = vec3(1.0);
const vec3 cGrey = vec3(0.66);
const vec3 cPurple = vec3(0.51,0.29,0.51);

const float maxx = 0.378;

void main()
{
	gl_FragColor=vec4(0.);
	float x = gl_FragCoord.x;
	float y = gl_FragCoord.y;
    vec2 p = gl_FragCoord.xy / resolution.xy;
	vec2 c = p - vec2(0.25, 0.5);
    	
    float coppers = time*10.0;
    float rep = 8.;// try 8 16 32 64 128 256 ...
    vec3 col2 = vec3(0.5 + 0.5 * sin(x/rep + 3.14 + coppers), 0.5 + 0.5 * cos (x/rep + coppers), 0.5 + 0.5 * sin (x/rep + coppers));
    vec3 col3 = vec3(0.5 + 0.5 * sin(x/rep + 3.14 - coppers), 0.5 + 0.5 * cos (x/rep -coppers), 0.5 + 0.5 * sin (x/rep - coppers));
    vec3 col4 = vec3(0.5 + 0.5 * sin(y/rep + 3.14 + coppers), 0.5 + 0.5 * cos (y/rep + coppers), 0.5 + 0.5 * sin (y/rep + coppers));
    vec3 col5 = vec3(0.5 + 0.5 * sin(y/rep + 3.14 - coppers), 0.5 + 0.5 * cos (y/rep -coppers), 0.5 + 0.5 * sin (y/rep - coppers));
    
   
   	if ( p.y > 0.985 && p.y < 1.0 ) gl_FragColor = vec4 ( col3, 1.0 );
	   
   	if ( p.x > 0.990 && p.x < 1.0 ) gl_FragColor = vec4 ( col4, 1.0 );
		
	if ( p.y > 0.0 && p.y < .02)    gl_FragColor = vec4 ( col2, 1.0 );
	
	if ( p.x > 0.0 && p.x < .01 && p.y<.985) gl_FragColor = vec4 ( col5, 1.0 );
   
	
	{
	vec2 uv = ( gl_FragCoord.xy / resolution.xy )*SIZE;
	
	float stars = 0.;
	float fl=0., s=0.;
	for (int layer = 0; layer < LAYERS; layer++) {
		fl = float(layer);
		s = (300.-fl*30.);
		stars += step(.1,pow(noise(mod(vec2(uv.x*s + time*SPEED*DIRECTION - fl*100.,uv.y*s),resolution.x)),18.)) * (fl/float(LAYERS));
	}
	gl_FragColor += vec4( vec3(stars), 1.0 );
	
	}
	
	
	
	float asp = resolution.y/resolution.x;
	vec2 uv = (gl_FragCoord.xy / resolution.xy);
	vec2 uvR = floor(uv*res*1.0);
	vec2 g = step(2.0,mod(uvR,16.0));
	vec3 bgcol=vec3(0.);// = mix(cPurple,mix(cPurple,cGrey,g.x),g.y);
	uv = uvR/res;
	float xt = mod(time+1.0,6.0);
	float dir = (step(xt,3.0)-.5)*-2.0;
	uv.x -= (maxx*2.0*dir)*mod(xt,3.0)/3.0+(-maxx*dir);
	uv.y -= abs(sin(4.5+time*1.3))*0.5-0.3;
	bgcol = mix(bgcol,bgcol-vec3(0.2),1.0-step(0.12,length(vec2(uv.x,uv.y*asp)-vec2(0.57,0.29))));
	vec3 rd = normalize(vec3((uv*2.0-1.0)*vec2(1.0,asp),1.5));
	float b = dot(rd,ro);
	float t1 = b*b-15.6;
        float t = -b-sqrt(t1);
	vec3 nor = normalize(ro+rd*t)*mRot;
	vec2 tuv = floor(vec2(atan(nor.x,nor.z)/PI+((floor((time*-dir)*60.0)/60.0)*0.5),acos(nor.y)/PI)*8.0);
	
	vec3 cc = mix(bgcol,mix(cRed,cWhite,clamp(mod(tuv.x+tuv.y,2.0),0.0,1.0)),1.0-step(t1,0.0));
	
	
	 uv = (gl_FragCoord.xy * 2.0 - resolution) / min(resolution.x, resolution.y);
	uv.y += abs(sin(time+uv.x)*0.2);
	
	cc = mix(cc+vec3(.5,0.4,1.9), cc, 1.0);
			
	
	gl_FragColor += vec4(cc,1.0);
	
   
}
 