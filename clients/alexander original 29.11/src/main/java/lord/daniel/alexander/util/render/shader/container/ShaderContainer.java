package lord.daniel.alexander.util.render.shader.container;

import lord.daniel.alexander.interfaces.Methods;
import lord.daniel.alexander.util.render.shader.annotation.Info;
import lord.daniel.alexander.util.render.shader.data.ShaderRenderType;
import lord.daniel.alexander.util.render.shader.render.Type;
import lord.daniel.alexander.util.render.shader.render.ingame.ShaderRenderer;

import java.util.List;

public abstract class ShaderContainer extends ShaderReload implements Methods {

    public String vert, frag;

    public ShaderContainer() {
        final Info info = getClass().getAnnotation(Info.class);
        vert = info.vert();
        frag = info.frag();
    }


    protected abstract void reload();

    public abstract void doRender(final ShaderRenderType type, Type renderType, List<ShaderRenderer.IShaderAccess> runnables);

}
