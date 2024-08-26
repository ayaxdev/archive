package lord.daniel.alexander.util.render.shader.access;

import lord.daniel.alexander.util.render.shader.shaders.BloomShader;
import lord.daniel.alexander.util.render.shader.shaders.BlurShader;

public interface ShaderAccess {

    BlurShader blurShader = new BlurShader();

    BloomShader bloomShader = new BloomShader();


}
