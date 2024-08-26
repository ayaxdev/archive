
//Stars effect
#extension GL_OES_standard_derivatives : enable

#ifdef GL_ES
precision mediump float;
#endif

uniform float time;
uniform vec2 resolution;

float field(in vec3 p,float s) {
	float strength = 7. + .03 * log(1.e-6 + fract(sin(time) * 4373.11));
	float accum = s/4.;
	float prev = 0.;
	float tw = 0.;
	for (int i = 0; i < 20; ++i) {
		float mag = dot(p, p);
		float w = exp(-float(i) / 7.);
		p = abs(p) / mag + vec3(-.5, -.4, -1.5);
		accum += w * exp(-strength * pow(abs(mag - prev), 2.2));
		tw += w;
		prev = mag;
	}
	return max(0., 5. * accum / tw - .7) + 0.2;
}

float field2(in vec3 p, float s) {
	float strength = 7. + .03 * log(1.e-6 + fract(sin(time) * 4373.11));
	float accum = s/4.;
	float prev = 0.;
	float tw = 0.;
	for (int i = 0; i < 13; ++i) {
		float mag = dot(p, p);
		p += abs(p) / mag + vec3(-.5, -.4, -1.5);
		float w = exp(-float(i) / 7.);
		accum += w * exp(-strength * pow(abs(mag - prev), 2.2));
		tw += w;
		prev = mag;
	}
	return max(0., 5. * accum / tw - .7);
}

vec3 nrand3( vec2 co ) {
	vec3 a = fract(cos(co.x*0.3e-3 + co.y * vec3(12.,87.,1.0)) * vec3(1.3e5, 4.7e5, 2.9e5) * fract(sin(dot(co,vec2(12.8697,776.1243)))));
	vec3 b = fract(sin(co.x*8.3e-3 + co.y * vec3(12.,87.,1.0)) * vec3(8.1e5, 1.0e5, 0.1e5) * floor(sin(dot(co,vec2(26.7416,17.8943)))));
	vec3 c = mix(a, b, 0.2);
	return c;
}

// Simple 2D random function
float random(vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.9898, 78.233))) * 43758.5453123);
}

vec2 rotateUV(vec2 uv, float rotation, vec2 mid)
{
    return vec2(
      cos(rotation) * (uv.x - mid.x) + sin(rotation) * (uv.y - mid.y) + mid.x,
      cos(rotation) * (uv.y - mid.y) - sin(rotation) * (uv.x - mid.x) + mid.y
    );
}

void mainImage( out vec4 fragColor, in vec2 fragCoord ) {
	vec2 uv = 2. * fragCoord.xy / resolution.xy - 1.;
	vec2 uvs = uv * resolution.xy / max(resolution.x, resolution.y);
	uvs = rotateUV(uvs, sin(time * 0.2), vec2(0.025, 0.025));
	vec3 p = vec3(uvs / 4., 0) + vec3(1., -1.3, 0.);
	p += .2 * vec3(sin(time / 16.), sin(time / 12.),  sin(time / 128.));
	//p.z += 0.2;

	float freqs[4];
	freqs[0] = 0.02;
	freqs[1] = 0.37;//GREEN
	freqs[2] = 0.31;//RED
	freqs[3] = 0.87;//BLUE

	float t = field(p,freqs[2]);
	float v = (1. - exp((abs(uv.x) - 1.) * 6.)) * (1. - exp((abs(uv.y) - 1.) * 6.));
	
	float zoom_pos = time;
	vec3 p2 = vec3(uvs / (4.+sin(zoom_pos*0.11)*0.2+0.2+sin(zoom_pos*0.15)*0.3+0.4), 1.5) + vec3(2., -1.3, -1.);
	p2 += 0.25 * vec3(sin(time / 16.), sin(time / 12.),  sin(time / 128.));
	//p2.z += 0.2;
	vec4 c2 = vec4(0.0, 0.0, 0.0, 1.0);

	fragColor = mix(freqs[3]-.3, 1., v) * vec4(1.5*freqs[2] * t * t* t , 1.2*freqs[1] * t * t, freqs[3]*t, 1.0)+c2;
	
	//quantize frag color
	//fragColor.x = floor(fragColor.x * 8.0) / 8.0;
	//fragColor.y = floor(fragColor.y * 8.0) / 8.0;
	//fragColor.z = floor(fragColor.z * 8.0) / 8.0;
	
}

void main() {
    mainImage(gl_FragColor, gl_FragCoord.xy);
    }