#extension GL_OES_standard_derivatives : enable

precision highp float;

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;

float iTime = time;
vec2 iResolution = resolution;

float g(float a) { return fract(iTime * a) * 3.141593 * 4.; }
float l(float a, float b, float c) {
  float d = clamp(.5 + .5 * (b - a) / c, 0., 1.);
  return mix(b, a, d) - c * d * (1. - d);
}
float p(float a, float b, float c) { return -l(-a, -b, c); }
mat2 h(float a) {
  float b = sin(a), c = cos(a);
  return mat2(c, b, -b, c);
}
float n(vec3 b, float c) {
  float d = .1, a = 1.;
  a = b.y, a = abs(a) - .5, a = abs(a),
  d = p(p(abs(b.x) - .5 * a, abs(b.z) - .5 * a, c), abs(b.y) - .5, c);
  return d;
}
float i(vec3 a) {
  float b = .1;
  a.xz *= h(g(.025)), a.zy *= -h(g(.05)),
      b = l(b, n(a, 0.), .01), a.xz *= -h(g(.05)), a.zy *= h(g(.075)),
      b = l(b, n(a, 0.), .01), a.zy *= h(g(.075)), a.xy *= -h(g(.025)),
      b = l(b, n(a, 0.), .01);
  return b;
}
vec3 t(in vec3 b) {
  vec2 a = vec2(1, -1) * .5773;
  return normalize(a.xyy * i(b + a.xyy * 5e-4) + a.yyx * i(b + a.yyx * 5e-4) +
                   a.yxy * i(b + a.yxy * 5e-4) + a.xxx * i(b + a.xxx * 5e-4));
}
vec3 u(vec3 q, vec3 o) {
  float d = 0., e = 0., j = 0., k = 0., b = 0., c = .3;
  c = 1. - c, k = sqrt(1e-4) * (1. - c);
  vec3 a = vec3(0), f = vec3(0);
  for (int r = 0; r < 64; r++) {
    a = q + o * d, e = i(a), b = 1. - abs(e) / k;
    if (b > 0.)
      f += .1 * b / d, j++;
    if (j > 10.)
      break;
    d += max(abs(e), k * c);
  }
  a = vec3(0), c = .8, c = 1. - c, b = 0., j = 0.;
  for (int s = 0; s < 10; s++) {
    a = q + o * d, a = reflect(a, o), a.xy = mod(a.xy, .7) - .35, e = i(a),
    b = 1. - abs(e) / k;
    if (b > 0.) {
      vec3 m = vec3(b * .01);
      m = t(a), m = reflect(a, m) * .5, f += m + .25 * b / d, f *= .5,
      f += sign(f) * .1, j++;
    }
    if (j > 10.)
      break;
    d += max(abs(e), k * c);
  }
  return f;
}
vec4 v(out vec4 b, in vec2 c) {
  vec2 a = c / iResolution.xy;
  a = (a - .5) * 2., a.x *= iResolution.x / iResolution.y;
  vec3 d = vec3(0), f = vec3(0, 0, -3), e = vec3(a, 1);
  e.xy *= .2, d = u(f, e), b = vec4(d, 1);
  return b;
}
void main() { gl_FragColor = v(gl_FragColor, gl_FragCoord.xy); }