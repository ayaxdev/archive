package com.skidding.atlas.util.render.shader.input;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Shaders {
	KAWASE_UP_GLOW("""
            #version 120

            uniform sampler2D inTexture, textureToCheck;
            uniform vec2 halfpixel, offset, iResolution;
            uniform bool check;
            uniform float lastPass;
            uniform float exposure;

            void main() {
                if(check && texture2D(textureToCheck, gl_TexCoord[0].st).a != 0.0) discard;
                vec2 uv = vec2(gl_FragCoord.xy / iResolution);

                vec4 sum = texture2D(inTexture, uv + vec2(-halfpixel.x * 2.0, 0.0) * offset);
                sum.rgb *= sum.a;
                vec4 smpl1 =  texture2D(inTexture, uv + vec2(-halfpixel.x, halfpixel.y) * offset);
                smpl1.rgb *= smpl1.a;
                sum += smpl1 * 2.0;
                vec4 smp2 = texture2D(inTexture, uv + vec2(0.0, halfpixel.y * 2.0) * offset);
                smp2.rgb *= smp2.a;
                sum += smp2;
                vec4 smp3 = texture2D(inTexture, uv + vec2(halfpixel.x, halfpixel.y) * offset);
                smp3.rgb *= smp3.a;
                sum += smp3 * 2.0;
                vec4 smp4 = texture2D(inTexture, uv + vec2(halfpixel.x * 2.0, 0.0) * offset);
                smp4.rgb *= smp4.a;
                sum += smp4;
                vec4 smp5 = texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset);
                smp5.rgb *= smp5.a;
                sum += smp5 * 2.0;
                vec4 smp6 = texture2D(inTexture, uv + vec2(0.0, -halfpixel.y * 2.0) * offset);
                smp6.rgb *= smp6.a;
                sum += smp6;
                vec4 smp7 = texture2D(inTexture, uv + vec2(-halfpixel.x, -halfpixel.y) * offset);
                smp7.rgb *= smp7.a;
                sum += smp7 * 2.0;
                vec4 result = sum / 12.0;
                gl_FragColor = vec4(result.rgb / result.a, mix(result.a, 1.0 - exp(-result.a * exposure), step(0.0, lastPass)));
            }"""),
	KAWASE_GLOW("""
            #version 120

            uniform sampler2D textureIn, textureToCheck;
            uniform vec2 texelSize, direction;
            uniform vec3 color;
            uniform bool avoidTexture;
            uniform float exposure, radius;
            uniform float weights[256];

            #define offset direction * texelSize

            void main() {
                if (direction.y == 1 && avoidTexture) {
                    if (texture2D(textureToCheck, gl_TexCoord[0].st).a != 0.0) discard;
                }
                vec4 innerColor = texture2D(textureIn, gl_TexCoord[0].st);
                innerColor.rgb *= innerColor.a;
                innerColor *= weights[0];
                for (float r = 1.0; r <= radius; r++) {
                    vec4 colorCurrent1 = texture2D(textureIn, gl_TexCoord[0].st + offset * r);
                    vec4 colorCurrent2 = texture2D(textureIn, gl_TexCoord[0].st - offset * r);

                    colorCurrent1.rgb *= colorCurrent1.a;
                    colorCurrent2.rgb *= colorCurrent2.a;

                    innerColor += (colorCurrent1 + colorCurrent2) * weights[int(r)];
                }

                gl_FragColor = vec4(innerColor.rgb / innerColor.a, mix(innerColor.a, 1.0 - exp(-innerColor.a * exposure), step(0.0, direction.y)));
            }
            """),
	KAWASE_UP_BLOOM("""
            #version 120

            uniform sampler2D inTexture, textureToCheck;
            uniform vec2 halfpixel, offset, iResolution;
            uniform int check;

            void main() {
              //  if(check && texture2D(textureToCheck, gl_TexCoord[0].st).a > 0.0) discard;
                vec2 uv = vec2(gl_FragCoord.xy / iResolution);

                vec4 sum = texture2D(inTexture, uv + vec2(-halfpixel.x * 2.0, 0.0) * offset);
                sum.rgb *= sum.a;
                vec4 smpl1 =  texture2D(inTexture, uv + vec2(-halfpixel.x, halfpixel.y) * offset);
                smpl1.rgb *= smpl1.a;
                sum += smpl1 * 2.0;
                vec4 smp2 = texture2D(inTexture, uv + vec2(0.0, halfpixel.y * 2.0) * offset);
                smp2.rgb *= smp2.a;
                sum += smp2;
                vec4 smp3 = texture2D(inTexture, uv + vec2(halfpixel.x, halfpixel.y) * offset);
                smp3.rgb *= smp3.a;
                sum += smp3 * 2.0;
                vec4 smp4 = texture2D(inTexture, uv + vec2(halfpixel.x * 2.0, 0.0) * offset);
                smp4.rgb *= smp4.a;
                sum += smp4;
                vec4 smp5 = texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset);
                smp5.rgb *= smp5.a;
                sum += smp5 * 2.0;
                vec4 smp6 = texture2D(inTexture, uv + vec2(0.0, -halfpixel.y * 2.0) * offset);
                smp6.rgb *= smp6.a;
                sum += smp6;
                vec4 smp7 = texture2D(inTexture, uv + vec2(-halfpixel.x, -halfpixel.y) * offset);
                smp7.rgb *= smp7.a;
                sum += smp7 * 2.0;
                vec4 result = sum / 12.0;
                gl_FragColor = vec4(result.rgb / result.a, mix(result.a, result.a * (1.0 - texture2D(textureToCheck, gl_TexCoord[0].st).a),check));
            }"""),
	KAWASE_DOWN_BLOOM("""
            #version 120

            uniform sampler2D inTexture;
            uniform vec2 offset, halfpixel, iResolution;

            void main() {
                vec2 uv = vec2(gl_FragCoord.xy / iResolution);
                vec4 sum = texture2D(inTexture, gl_TexCoord[0].st);
                sum.rgb *= sum.a;
                sum *= 4.0;
                vec4 smp1 = texture2D(inTexture, uv - halfpixel.xy * offset);
                smp1.rgb *= smp1.a;
                sum += smp1;
                vec4 smp2 = texture2D(inTexture, uv + halfpixel.xy * offset);
                smp2.rgb *= smp2.a;
                sum += smp2;
                vec4 smp3 = texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset);
                smp3.rgb *= smp3.a;
                sum += smp3;
                vec4 smp4 = texture2D(inTexture, uv - vec2(halfpixel.x, -halfpixel.y) * offset);
                smp4.rgb *= smp4.a;
                sum += smp4;
                vec4 result = sum / 8.0;
                gl_FragColor = vec4(result.rgb / result.a, result.a);
            }"""),
	KAWASE_UP("""
            #version 120

            uniform sampler2D inTexture, textureToCheck;
            uniform vec2 halfpixel, offset, iResolution;
            uniform int check;

            void main() {
                vec2 uv = vec2(gl_FragCoord.xy / iResolution);
                vec4 sum = texture2D(inTexture, uv + vec2(-halfpixel.x * 2.0, 0.0) * offset);
                sum += texture2D(inTexture, uv + vec2(-halfpixel.x, halfpixel.y) * offset) * 2.0;
                sum += texture2D(inTexture, uv + vec2(0.0, halfpixel.y * 2.0) * offset);
                sum += texture2D(inTexture, uv + vec2(halfpixel.x, halfpixel.y) * offset) * 2.0;
                sum += texture2D(inTexture, uv + vec2(halfpixel.x * 2.0, 0.0) * offset);
                sum += texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset) * 2.0;
                sum += texture2D(inTexture, uv + vec2(0.0, -halfpixel.y * 2.0) * offset);
                sum += texture2D(inTexture, uv + vec2(-halfpixel.x, -halfpixel.y) * offset) * 2.0;

                gl_FragColor = vec4(sum.rgb /12.0, mix(1.0, texture2D(textureToCheck, gl_TexCoord[0].st).a, check));
            }
            """),
	KAWASE_DOWN("""
            #version 120

            uniform sampler2D inTexture;
            uniform vec2 offset, halfpixel, iResolution;

            void main() {
                vec2 uv = vec2(gl_FragCoord.xy / iResolution);
                vec4 sum = texture2D(inTexture, gl_TexCoord[0].st) * 4.0;
                sum += texture2D(inTexture, uv - halfpixel.xy * offset);
                sum += texture2D(inTexture, uv + halfpixel.xy * offset);
                sum += texture2D(inTexture, uv + vec2(halfpixel.x, -halfpixel.y) * offset);
                sum += texture2D(inTexture, uv - vec2(halfpixel.x, -halfpixel.y) * offset);
                gl_FragColor = vec4(sum.rgb * .125, 1.0);
            }
            """),
	GRADIENT_MASK("""
            #version 120

            uniform vec2 location, rectSize;
            uniform sampler2D tex;
            uniform vec3 color1, color2, color3, color4;
            uniform float alpha;

            #define NOISE .5/255.0

            vec3 createGradient(vec2 coords, vec3 color1, vec3 color2, vec3 color3, vec3 color4){
                vec3 color = mix(mix(color1.rgb, color2.rgb, coords.y), mix(color3.rgb, color4.rgb, coords.y), coords.x);
                //Dithering the color from https://shader-tutorial.dev/advanced/color-banding-dithering/
                color += mix(NOISE, -NOISE, fract(sin(dot(coords.xy, vec2(12.9898,78.233))) * 43758.5453));
                return color;
            }

            void main() {
                vec2 coords = (gl_FragCoord.xy - location) / rectSize;
                float texColorAlpha = texture2D(tex, gl_TexCoord[0].st).a;
                gl_FragColor = vec4(createGradient(coords, color1, color2, color3, color4), texColorAlpha * alpha);
            }"""),
	CHAMS("""
            #version 120

            uniform sampler2D textureIn;
            uniform vec4 color;
            void main() {
                float alpha = texture2D(textureIn, gl_TexCoord[0].st).a;
                gl_FragColor = vec4(color.rgb, color.a * mix(0.0, alpha, step(0.0, alpha)));
            }
            """),
	TEXTURED_ROUNDED_RECT("""
            #version 120

            uniform vec2 location, rectSize;
            uniform sampler2D textureIn;
            uniform float radius, alpha;

            float roundedBoxSDF(vec2 centerPos, vec2 size, float radius) {
                return length(max(abs(centerPos) -size, 0.)) - radius;
            }


            void main() {
                float distance = roundedBoxSDF((rectSize * .5) - (gl_TexCoord[0].st * rectSize), (rectSize * .5) - radius - 1., radius);
                float smoothedAlpha =  (1.0-smoothstep(0.0, 2.0, distance)) * alpha;
                gl_FragColor = vec4(texture2D(textureIn, gl_TexCoord[0].st).rgb, smoothedAlpha);
            }"""),
	OUTLINED_ROUNDED_RECT("""
            #version 120

            uniform vec2 location, rectSize;
            uniform vec4 color, outlineColor;
            uniform float radius, outlineThickness;

            float roundedSDF(vec2 centerPos, vec2 size, float radius) {
                return length(max(abs(centerPos) - size + radius, 0.0)) - radius;
            }

            void main() {
                float distance = roundedSDF(gl_FragCoord.xy - location - (rectSize * .5), (rectSize * .5) + (outlineThickness *.5) - 1.0, radius);

                float blendAmount = smoothstep(0., 2., abs(distance) - (outlineThickness * .5));

                vec4 insideColor = (distance < 0.) ? color : vec4(outlineColor.rgb,  0.0);
                gl_FragColor = mix(outlineColor, insideColor, blendAmount);

            }"""),
	ROUNDED_RECT("""
            #version 120

            uniform vec2 location, rectSize;
            uniform vec4 color;
            uniform float radius;
            uniform bool blur;

            float roundSDF(vec2 p, vec2 b, float r) {
                return length(max(abs(p) - b, 0.0)) - r;
            }


            void main() {
                vec2 rectHalf = rectSize * .5;
                // Smooth the result (free antialiasing).
                float smoothedAlpha =  (1.0-smoothstep(0.0, 1.0, roundSDF(rectHalf - (gl_TexCoord[0].st * rectSize), rectHalf - radius - 1., radius))) * color.a;
                gl_FragColor = vec4(color.rgb, smoothedAlpha);// mix(quadColor, shadowColor, 0.0);

            }"""),
	BLUR("""
			#version 120
			  
			  uniform sampler2D currentTexture;
			  uniform vec2 texelSize;
			  uniform vec2 coords;
			  uniform float blurRadius;
			  uniform float blursigma;
			  uniform sampler2D texture20;
			  
			  float CalcGauss(float x)
			  {
			      float sigmaMultiplication = blursigma * blursigma;
			      float sigmaFactor = blursigma < 1.0 ? 1.0 : blursigma;
			      return exp(-0.5 * x * x / sigmaMultiplication) / (sigmaFactor * sqrt(2.0 * 3.14159265359 * sigmaMultiplication));
			  }
			  
			  void main() {
			      vec2 texCoord = gl_TexCoord[0].st;
			  
			      float alpha = texture2D(texture20, texCoord).a;
			      if (coords.x == 0.0 && alpha == 0.0) {
			          discard;
			      }
			  
			      vec3 color = vec3(0.0);
			      float totalWeight = 0.0;
			      for (float radiusF = -blurRadius; radiusF <= blurRadius; radiusF += 1.0) {
			          float weight = CalcGauss(radiusF);
			          color += texture2D(currentTexture, texCoord + radiusF * texelSize * coords).rgb * weight;
			          totalWeight += weight;
			      }
			      color /= totalWeight; // Normalize by total weight
			  
			      // Apply saturation
			      const vec3 W = vec3(0.2125, 0.7154, 0.0721);
			      vec3 intensity = vec3(dot(color, W));
			      color = mix(intensity, color, 1.1);
			  
			      gl_FragColor = vec4(color, 1.0);
			  }
            """),
	GAUSSIAN("""
            #version 120

            uniform sampler2D textureIn;
            uniform vec2 texelSize, direction;
            uniform float radius;
            uniform float weights[256];

            #define offset texelSize * direction

            void main() {
                vec3 blr = texture2D(textureIn, gl_TexCoord[0].st).rgb * weights[0];

                for (float f = 1.0; f <= radius; f++) {
                    blr += texture2D(textureIn, gl_TexCoord[0].st + f * offset).rgb * (weights[int(abs(f))]);
                    blr += texture2D(textureIn, gl_TexCoord[0].st - f * offset).rgb * (weights[int(abs(f))]);
                }

                gl_FragColor = vec4(blr, 1.0);
            }
            """),
	GRADIENT_ROUNDED_RECT("""
            #version 120

            uniform vec2 location, rectSize;
            uniform vec4 color1, color2, color3, color4;
            uniform float radius;

            #define NOISE .5/255.0

            float roundSDF(vec2 p, vec2 b, float r) {
                return length(max(abs(p) - b , 0.0)) - r;
            }

            vec4 createGradient(vec2 coords, vec4 color1, vec4 color2, vec4 color3, vec4 color4){
                vec4 color = mix(mix(color1, color2, coords.y), mix(color3, color4, coords.y), coords.x);
                //Dithering the color
                // from https://shader-tutorial.dev/advanced/color-banding-dithering/
                color += mix(NOISE, -NOISE, fract(sin(dot(coords.xy, vec2(12.9898, 78.233))) * 43758.5453));
                return color;
            }

            void main() {
                vec2 st = gl_TexCoord[0].st;
                vec2 halfSize = rectSize * .5;
               \s
               // use the bottom leftColor as the alpha
                float smoothedAlpha =  (1.0-smoothstep(0.0, 2., roundSDF(halfSize - (gl_TexCoord[0].st * rectSize), halfSize - radius - 1., radius)));
                vec4 gradient = createGradient(st, color1, color2, color3, color4);    gl_FragColor = vec4(gradient.rgb, gradient.a * smoothedAlpha);
            }"""),
	VERTEX("""
			#version 120
			   
			void main() {
			    gl_TexCoord[0] = gl_MultiTexCoord0;
			    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
			}
			""");
	
	public final String source;
}
