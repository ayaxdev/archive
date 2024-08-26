package lord.daniel.alexander.handler.render;

import lombok.experimental.UtilityClass;
import lord.daniel.alexander.Modification;
import lord.daniel.alexander.event.Event;
import lord.daniel.alexander.event.impl.render.Render2DEvent;
import lord.daniel.alexander.module.impl.design.BloomModule;
import lord.daniel.alexander.module.impl.design.BlurModule;
import lord.daniel.alexander.storage.impl.ModuleStorage;
import lord.daniel.alexander.util.render.bloom.BloomShaderImpl;
import lord.daniel.alexander.util.render.blur.OldGaussianBlurImpl;
import lord.daniel.alexander.util.render.blur.NewGaussianBlurImpl;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Written by Daniel. on 25/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
@UtilityClass
public class ShaderHandler {

    private final OldGaussianBlurImpl oldGaussianBlur = new OldGaussianBlurImpl();
    private final NewGaussianBlurImpl newGaussianBlur = new NewGaussianBlurImpl();

    private final BloomShaderImpl bloomShader = new BloomShaderImpl();

    private BlurModule blurModule;
    private BloomModule bloomModule;

    private final List<Runnable> runnables = new ArrayList<>();

    public void draw(ScaledResolution scaledResolution, float partialTicks) {
        if(blurModule == null)
            blurModule = ModuleStorage.getModuleStorage().getByClass(BlurModule.class);

        if(bloomModule == null)
            bloomModule = ModuleStorage.getModuleStorage().getByClass(BloomModule.class);

        runnables.add(() -> Modification.getModification().getPubSub().publish(new Render2DEvent(Event.Stage.PRE, scaledResolution, partialTicks)));

        renderRunnable(runnables);

        runnables.clear();
    }

    public void renderRunnable(List<Runnable> runnables) {
        if(blurModule.isEnabled()) {
            switch (blurModule.mode.getValue()) {
                case "NewGaussian" -> newGaussianBlur.renderBlur(runnables, blurModule.radius.getValue(), blurModule.sigma.getValue());
                case "OldGaussian" -> oldGaussianBlur.renderBlur(runnables, blurModule.offset.getValue());
            }
        }

        if(bloomModule.isEnabled()) {
            bloomShader.renderBloom(runnables, bloomModule.radius.getValue());
        }
    }

    public void renderAndRunRunnable(List<Runnable> runnables) {
        renderRunnable(runnables);
        runnables.forEach(Runnable::run);
    }

    //
    public void render(List<IShaderRenderer> runnables) {
        if(blurModule.isEnabled()) {
            switch (blurModule.mode.getValue()) {
                case "NewGaussian" -> newGaussianBlur.renderBlur(convertForShaders(runnables), blurModule.radius.getValue(), blurModule.sigma.getValue());
                case "OldGaussian" -> oldGaussianBlur.renderBlur(convertForShaders(runnables), blurModule.offset.getValue());
            }
        }

        if(bloomModule.isEnabled()) {
            bloomShader.renderBloom(convertForShaders(runnables), bloomModule.radius.getValue());
        }
    }

    public void renderAndRun(List<IShaderRenderer> runnables) {
        render(runnables);
        runnables.forEach(iShaderRenderer -> iShaderRenderer.render(false));
    }

    public interface IShaderRenderer {
        void render(boolean shader);
    }

    public List<Runnable> convertForShaders(List<IShaderRenderer> iShaderRenderers) {
        List<Runnable> newRunnables = new ArrayList<>();

        for(IShaderRenderer shaderRenderer : iShaderRenderers) {
            newRunnables.add(() -> shaderRenderer.render(true));
        }

        return newRunnables;
    }

}
