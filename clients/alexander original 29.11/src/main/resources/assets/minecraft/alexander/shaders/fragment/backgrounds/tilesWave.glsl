#extension GL_OES_standard_derivatives : enable

 precision highp float;

                uniform float time;
                uniform vec2 mouse;
                uniform vec2 resolution;
                
                mat2 rotate2D(float r) {
                    return mat2(cos(r), sin(r), -sin(r), cos(r));
                }
                
                void main(void) {
                    // Normalized pixel coordinates (from 0 to 1)
                    vec2 uv = (gl_FragCoord.xy - 0.5 * resolution.xy) / resolution.y;
                    vec3 col = vec3(0);
                    float t = time * 6.; // Adjust the time factor for faster animation
                
                    vec2 n = vec2(0);
                    vec2 q = vec2(0);
                    vec2 p = uv;
                    float d = dot(p, p);
                    float S = 12.0;
                    float a = 0.0;
                    mat2 m = rotate2D(5.);
                
                    for (float j = 0.; j < 2.; j++) {
                        p *= m;
                        n *= m;
                        q = p * S + t * 4. + sin(t * 4. - d * 6.) * 0.8 + j + n;
                        a += dot(cos(q) / S, vec2(0.2));
                        n -= tan(q);
                        S *= 1.2;
                    }
                
                    col = vec3(4, 2, 1) * (a + 0.2) + a + a - d;
                
                    // Output to screen
                    gl_FragColor = vec4(col, 1.0);
                }