package lord.daniel.alexander.util.render.shader.util;

import lombok.experimental.UtilityClass;
import lord.daniel.alexander.interfaces.IMinecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnd;

@UtilityClass
public class ShaderUtil implements IMinecraft {

    public static final String VERTEX_SHADER = """
            #version 120

            void main() {
                gl_TexCoord[0] = gl_MultiTexCoord0;
                gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
            }""";

    public static final String ROUNDED_RECT = """
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

            }""";

    public static final String ROUNDED_RECT_GRADIENT = """
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
            }""";

    public  static final String ROUNDED_RECT_OUTLINE = """
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

            }""";

    public static final String MODERN_BLUR = """
            #version 120

            uniform sampler2D currentTexture;
            uniform vec2 texelSize;
            uniform vec2 coords;
            uniform float blurRadius;
            uniform float blursigma;
            uniform sampler2D texture20;

            float CalcGauss(float x)
            {

                float sigmaMultiplication = ((blursigma * blursigma));

                if (blursigma < 1) {
                    return (exp(-.5 * x * x) * .4);
                } else {
                    return (exp(-.5 * x * x / (sigmaMultiplication)) / blursigma) * .4;//bisschen umgeschrieben von der eigendlichen methode, da die eigendliche für einen full solid blur ist
                }

            }
            float hash(vec2 p) {
                vec3 p3 = fract(vec3(p.xyx) * 0.1031);
                p3 += dot(p3, p3.yzx + 19.19);
                return fract((p3.x + p3.y) * p3.z);
            }

            const vec2 add = vec2(1.0, 0.0);
            float noise(vec2 x) {
                vec2 p = floor(x);
                vec2 f = fract(x);
                f = f * f * (3.0 - 2.0 * f);
                float res = mix(mix(hash(p), hash(p + add.xy), f.x),
                                mix(hash(p + add.yx), hash(p + add.xx), f.x), f.y);
                return res;
            }

            float fnoise(vec2 uv) {
                float f = 0.0;
                mat2 m = mat2(1.6, 1.2, -1.2, 1.6);
                f = 0.5000 * noise(uv); uv = m * uv;
                f += 0.2500 * noise(uv); uv = m * uv;\s
                f += 0.1250 * noise(uv); uv = m * uv;
                f += 0.0625 * noise(uv); uv = m * uv;
                return f;
            }

            const float f = 0.5 * (1.0 + sqrt(2.0));
            float repeat;
            float lerpx(vec2 uv) {
                float v = fnoise(uv + vec2(-repeat * 0.5, 0)) * ((uv.x) / repeat);
                v += fnoise(uv + vec2(repeat * 0.5, 0)) * ((repeat - uv.x) / repeat);
                return mix(v, f * pow(v, f), 4.0 * ((uv.x) / repeat) * ((repeat - uv.x) / repeat));
            }

            float lerpy(vec2 uv) {
                float v = lerpx(uv + vec2(0, -repeat * 0.5)) * ((uv.y) / repeat);
                v += lerpx(uv + vec2(0, repeat * 0.5)) * ((repeat - uv.y) / repeat);
                return mix(v, f * pow(v, f), 4.0 * ((uv.y) / repeat) * ((repeat - uv.y) / repeat));
            }

            vec3 noisetile(vec2 uv) {
                return vec3(clamp(lerpy(uv), 0.0, 1.0));
            }

            vec3 czm_saturation(vec3 rgb, float adjustment)
            {
                // Algorithm from Chapter 16 of OpenGL Shading Language
                const vec3 W = vec3(0.2125, 0.7154, 0.0721);
                vec3 intensity = vec3(dot(rgb, W));
                return mix(intensity, rgb, adjustment);
            }

            void main() {

                vec2 texCoord = gl_TexCoord[0].st;

                float alpha = texture2D(texture20, texCoord).a;
                if (coords.x == 0.0 && alpha == 0.0) {
                    discard;
                }

                vec3 color = vec3(0.0);
                for (float radiusF = -blurRadius; radiusF <= blurRadius; radiusF++) {
                    color += texture2D(currentTexture, gl_TexCoord[0].st + radiusF * texelSize * coords).rgb * CalcGauss(radiusF);
                }

                const float scale = 49.0;
                float rc = 6.0+1.5* .13;
                float sc = 3.5+0.9* .08;

                repeat = 2.0+(scale-1.0)*pow(rc, 2.0);
                float size = 1.0+scale*pow(sc, 2.0);

                vec2 uv = -1.0+2.0*gl_FragCoord.xy / texelSize.xy;
                uv = mod(uv*size, repeat);

                vec3 colorNoise = noisetile(uv) * .09;

                vec3 blurWithSaturation = czm_saturation(color, 1.1);

                vec3 blurWithSaturationAndNoise = blurWithSaturation;

                gl_FragColor = vec4(blurWithSaturationAndNoise, 1.0);
            }
            """;
    public static final String GAUSSIAN_BLUR = """
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
            """;

    public static final String BLOOM_SHADER = """
            #version 120

            uniform sampler2D inTexture, textureToCheck;
            uniform vec2 texelSize, direction;
            uniform float radius;
            uniform float weights[256];

            vec4 czm_saturation(vec3 rgb, float adjustment)
            {
                const vec3 W = vec3(0.2125, 0.7154, 0.0721);
                vec3 intensity = vec3(dot(rgb, W));
                return vec4(mix(intensity, rgb, adjustment), 1.0);
            }

            void main() {
                vec2 texCoord = gl_TexCoord[0].st;
                if (direction.x == 0.0)
                if (texture2D(textureToCheck, texCoord).a > 0.0) discard;

                vec4 color = texture2D(inTexture, texCoord);
                color.rgb *= color.a;
                color *= weights[0];
                for (float radiusF = -radius; radiusF <= radius; radiusF++) {
                    vec2 offset = radiusF * texelSize * direction;
                    vec2 leftDirection = texCoord - offset;
                    vec2 rightDirection = texCoord + offset;
                    vec4 leftTexture = texture2D(inTexture, leftDirection);
                    vec4 rightTexture = texture2D(inTexture, rightDirection);
                    leftTexture.rgb *= leftTexture.a;
                    rightTexture.rgb *= rightTexture.a;

                    color += (leftTexture + rightTexture) * weights[int(radiusF)];
                }

                gl_FragColor = vec4(color.rgb / color.a, color.a);
            }""";

    public static Framebuffer updateFramebuffer(final Framebuffer framebuffer, boolean depth) {
        if (framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.displayWidth, mc.displayHeight, depth);
        }
        return framebuffer;
    }

    public static Framebuffer updateFramebuffer(final Framebuffer framebuffer) {
        if (framebuffer == null || framebuffer.framebufferWidth != mc.displayWidth || framebuffer.framebufferHeight != mc.displayHeight) {
            if (framebuffer != null) {
                framebuffer.deleteFramebuffer();
            }
            return new Framebuffer(mc.displayWidth, mc.displayHeight, true);
        }
        return framebuffer;
    }

    public void drawQuad(float x, float y, float width, float height) {
        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2f(x, y);
        glTexCoord2f(0, 1);
        glVertex2f(x, y + height);
        glTexCoord2f(1, 1);
        glVertex2f(x + width, y + height);
        glTexCoord2f(1, 0);
        glVertex2f(x + width, y);
        glEnd();
    }

    public void drawFullScreenQuad() {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        float screenWidth = (float) scaledResolution.getScaledWidth_double();
        float screenHeight = (float) scaledResolution.getScaledHeight_double();

        glBegin(GL_QUADS);
        glTexCoord2f(0, 1);
        glVertex2f(0, 0);
        glTexCoord2f(0, 0);
        glVertex2f(0, screenHeight);
        glTexCoord2f(1, 0);
        glVertex2f(screenWidth, screenHeight);
        glTexCoord2f(1, 1);
        glVertex2f(screenWidth, 0);
        glEnd();
    }

}
