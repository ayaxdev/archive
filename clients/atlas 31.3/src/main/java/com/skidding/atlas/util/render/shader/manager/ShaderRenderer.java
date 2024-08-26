package com.skidding.atlas.util.render.shader.manager;

import com.skidding.atlas.module.ModuleManager;
import com.skidding.atlas.module.impl.hud.PostProcessingModule;
import com.skidding.atlas.util.render.shader.usage.Bloom;
import com.skidding.atlas.util.render.shader.usage.NewBlur;
import com.skidding.atlas.util.render.shader.usage.OldBlur;

import java.util.Arrays;

public class ShaderRenderer {

    public static final ShaderRenderer INSTANCE = new ShaderRenderer();

    private OldBlur oldBlur;
    private NewBlur newBlur;
    private Bloom bloom;

    private PostProcessingModule postProcessing;

    public void init() {
        oldBlur = new OldBlur();
        newBlur = new NewBlur();
        bloom = new Bloom();

        postProcessing = ModuleManager.getSingleton().getByClass(PostProcessingModule.class);
    }

    public void drawAndRun(ShaderDrawContext... drawContexts) {
        blur(drawContexts);
        bloom(drawContexts);

        for(ShaderDrawContext drawContext : drawContexts)
            drawContext.draw(false);
    }

    public void draw(ShaderDrawContext... drawContexts) {
        for(ShaderDrawContext drawContext : drawContexts)
            drawContext.draw(false);
    }

    public void blurAndRun(ShaderDrawContext... drawContexts) {
        blur(drawContexts);

        for(ShaderDrawContext drawContext : drawContexts)
            drawContext.draw(false);
    }

    public void blur(ShaderDrawContext... drawContexts) {
        if(!postProcessing.isEnabled() || !postProcessing.blur.getValue())
            return;

        switch (postProcessing.blurQuality.getValue()) {
            case "Old":
                oldBlur.renderBlur(Arrays.asList(drawContexts),
                        postProcessing.blurRange.getValue());
                break;
            case "New":
                newBlur.renderBlur(Arrays.asList(drawContexts),
                        postProcessing.blurRadius.getValue(),
                        postProcessing.blurSigma.getValue());
                break;
        }
    }

    public void bloomAndRun(ShaderDrawContext... drawContexts) {
        bloom(drawContexts);

        for(ShaderDrawContext drawContext : drawContexts)
            drawContext.draw(false);
    }

    public void bloom(ShaderDrawContext... drawContexts) {
        if(!postProcessing.isEnabled() || !postProcessing.shadow.getValue())
            return;

        bloom.renderBloom(Arrays.asList(drawContexts),
                postProcessing.shadowIterations.getValue().intValue(),
                postProcessing.shadowOffset.getValue());
    }

    public interface ShaderDrawContext {

        void draw(boolean runningShaders);

    }

}
