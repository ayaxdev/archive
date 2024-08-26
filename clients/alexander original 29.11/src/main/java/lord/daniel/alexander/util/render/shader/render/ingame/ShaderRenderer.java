package lord.daniel.alexander.util.render.shader.render.ingame;

import lord.daniel.alexander.module.impl.hud.BlurModule;
import lord.daniel.alexander.module.impl.hud.ShadowModule;
import lord.daniel.alexander.storage.impl.ModuleStorage;
import lord.daniel.alexander.util.render.shader.access.ShaderAccess;
import lord.daniel.alexander.util.render.shader.data.ShaderRenderType;
import lord.daniel.alexander.util.render.shader.render.Type;

import java.util.Arrays;

public class ShaderRenderer {

    private static BlurModule blurModule;
    private static ShadowModule shadowModule;
    
    public static void renderShaders(boolean bloom, boolean blur, IShaderAccess... interfaces) {
        if(blurModule == null || shadowModule == null) {
            blurModule = ModuleStorage.getModuleStorage().getByClass(BlurModule.class);
            shadowModule = ModuleStorage.getModuleStorage().getByClass(ShadowModule.class);
        }

    	if(bloom && shadowModule.isEnabled()) {
			ShaderAccess.bloomShader.doRender(ShaderRenderType.OVERLAY, Type.QUADS, Arrays.asList(interfaces));
    	}

    	if(blur && blurModule.isEnabled()) {
        	ShaderAccess.blurShader.doRender(ShaderRenderType.OVERLAY, Type.QUADS, Arrays.asList(interfaces));
    	}
    }

    public static void renderShaders(IShaderAccess... interfaces) {
    	ShaderRenderer.renderShaders(true, true, interfaces);
    }

    public static void render(boolean bloom, boolean blur, IShaderAccess... interfaces) {
    	ShaderRenderer.renderShaders(bloom, blur, interfaces);
        Arrays.asList(interfaces).forEach(run -> run.run(false));
    }

    public static void render(IShaderAccess... interfaces) {
    	ShaderRenderer.renderShaders(true, true, interfaces);
        Arrays.asList(interfaces).forEach(run -> run.run(false));
    }

    public interface IShaderAccess {
        void run(boolean runningShaders);
    }

}
