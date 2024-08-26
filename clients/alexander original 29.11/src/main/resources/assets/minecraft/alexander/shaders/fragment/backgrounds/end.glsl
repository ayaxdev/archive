#ifdef GL_ES
precision mediump float;
#endif

#define M_PI 3.1415926535897932384626433832795

uniform float time;
uniform vec2 resolution;

float rand (in vec2 uv) { return fract(sin(dot(uv,vec2(12.4124,48.4124)))*48512.41241); }
const vec2 O = vec2(2.,1.);
float noise (in vec2 uv) {
	vec2 b = floor(uv);
	return mix(mix(rand(b),rand(b+O.yx),.5),mix(rand(b+O),rand(b+O.yy),.5),.5);
}

mat4 rotationMatrix(vec3 axis, float angle) {
    axis = normalize(axis);
    float s = sin(angle);
    float c = cos(angle);
    float oc = 1.0 - c;
    return mat4(oc * axis.x * axis.x + c,           oc * axis.x * axis.y - axis.z * s,  oc * axis.z * axis.x + axis.y * s,  0.0,
                oc * axis.x * axis.y + axis.z * s,  oc * axis.y * axis.y + c,           oc * axis.y * axis.z - axis.x * s,  0.0,
                oc * axis.z * axis.x - axis.y * s,  oc * axis.y * axis.z + axis.x * s,  oc * axis.z * axis.z + c,           0.0,
                0.0,                                0.0,                                0.0,                                1.0);
}

vec4 stars(in vec2 p) {
	vec2 c = p - vec2(0.25, 0.5);
	
	const int layers = 3;
    	vec2 size = vec2(resolution.x / resolution.y, 1.);
	
	
	vec2 uv = p*size;
	
	float stars = 0.;
	float fl=0., s=0.;
	for (int layer = 0; layer < layers; layer++) {
		fl = float(layer);
		s = (300.-fl*30.);
		stars += smoothstep(0.0, 0.1,pow(noise(mod(vec2(uv.x*s - fl*100.,uv.y*s),resolution.x)),18.)) * (fl/float(layers));
	}
	return vec4( vec3(stars), 1.0 );
}


void main()
{	
	float pitch = 0.;
	float yaw = 0.;
	vec4 col = vec4(0.044,0.036,0.063,1);
    	vec2 p = gl_FragCoord.xy / resolution.xy;
	
	vec4 dir = normalize(vec4( -p, 2., 0));

	float sb = sin(pitch);
	float cb = cos(pitch);
	dir = normalize(vec4(dir.x, dir.y * cb - dir.z * sb, dir.y * sb + dir.z * cb, 0));
	
	float sa = sin(-yaw);
	float ca = cos(-yaw);
	dir = normalize(vec4(dir.z * sa + dir.x * ca, dir.y, dir.z * ca - dir.x * sa, 0));
	
	vec4 ray;
	
	for (int i=0; i<16; i++) {
		float mult = 16.-float(i);
	
		float j = float(i) + 7.;
		float rand1 = (j * j * 4321. + j * 8.) * 2.0;
		float k = j + 1.;
		float rand2 = (k * k * k * 239. + k * 37.) * 3.6;
		float rand3 = rand1 * 347.4 + rand2 * 63.4;
		
		vec3 axis = normalize(vec3(sin(rand1), sin(rand2) , cos(rand3)));
		
		ray = dir * rotationMatrix(axis, mod(rand3, 2.*M_PI));
		
		float u = 0.5 + (atan(ray.z,ray.x)/(2.*M_PI));
		float v = 0.5 + (asin(ray.y)/M_PI);
		
		float scale = mult*0.5 + 2.75;
		vec2 tex = vec2( u * scale, (v + time * 0.0006) * scale * .6 );
		
		vec4 tcol = stars(tex);
		
		float a = tcol.r * (0.05 + (1.0/mult) * 0.65) * (1.-smoothstep(0.15, 0.48, abs(v-0.25)));
		
		float r = (mod(rand1, 29.0)/29.0) * 0.5 + 0.1;
    		float g = (mod(rand2, 35.0)/35.0) * 0.5 + 0.4;
    		float b = (mod(rand1, 17.0)/17.0) * 0.5 + 0.5;
		
		col = col*(1.-a) + vec4(r,g,b,1.)*a;
	}
    
    	gl_FragColor = col;
}