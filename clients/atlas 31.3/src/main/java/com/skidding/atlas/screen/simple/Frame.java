package com.skidding.atlas.screen.simple;

import com.skidding.atlas.AtlasClient;
import com.skidding.atlas.event.impl.client.BackgroundEvent;
import com.skidding.atlas.font.ClientFontRenderer;
import com.skidding.atlas.font.FontManager;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.module.ModuleManager;
import com.skidding.atlas.module.impl.hud.ClickGuiModule;
import com.skidding.atlas.screen.ScreenContext;
import com.skidding.atlas.util.animation.Direction;
import com.skidding.atlas.util.animation.Animation;
import com.skidding.atlas.util.animation.impl.SmoothStepAnimation;
import com.skidding.atlas.util.animation.deprecated.DirectAnimation;
import com.skidding.atlas.util.minecraft.IMinecraft;
import com.skidding.atlas.util.render.DrawUtil;
import com.skidding.atlas.util.render.shader.manager.ShaderRenderer;
import de.florianmichael.rclasses.math.integration.Boundings;
import io.github.racoondog.norbit.EventHandler;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Frame implements IMinecraft {

    public final ModuleCategory moduleCategory;
    public float posX, posY;
    public final float width, height;

    private boolean dragging;
    private float draggingX, draggingY;

    private float lastFramePosX = Integer.MIN_VALUE;
    public boolean interpolated = false, closing = false;

    private Window workingWindow;
    private ModuleFeature nextModule;

    @SuppressWarnings("deprecation")
    public final DirectAnimation scrollAnimation = new DirectAnimation(0d, 1f);
    public final Animation openingAnimation = new SmoothStepAnimation(250, 100f, Direction.FORWARDS);

    private final ClickGuiModule clickGuiModule = ModuleManager.getSingleton().getByClass(ClickGuiModule.class);

    public Frame(ModuleCategory moduleCategory, float posX, float posY, float width, float height) {
        this.moduleCategory = moduleCategory;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
    }

    private final ClientFontRenderer roboto17 = FontManager.getSingleton().get("Roboto", 17);

    public void init(float lastFramePosX) {
        AtlasClient.getInstance().eventPubSub.subscribe(this);

        interpolated = false;
        closing = false;


        if(this.lastFramePosX == Integer.MIN_VALUE)
            this.lastFramePosX = lastFramePosX;
    }

    public void end() {
        openingAnimation.setDirection(Direction.FORWARDS);
        openingAnimation.reset();

        AtlasClient.getInstance().eventPubSub.unsubscribe(this);
    }

    public void render(int mouseX, int mouseY) {
        if(workingWindow != null && workingWindow.closed()) {
            workingWindow = null;
        }

        if(!interpolated) {
            openingAnimation.reset();

            openingAnimation.setDirection(Direction.FORWARDS);

            interpolated = true;
        }

        final float workingPosX = getWorkingPosX();
        final float workingPosY = getWorkingPosY();

        if(!Mouse.isButtonDown(0)) {
            dragging = false;
            draggingX = Integer.MIN_VALUE;
            draggingY = Integer.MIN_VALUE;
        }

        if(dragging) {
            float xDiff = workingPosX - posX;
            float yDiff = workingPosY - posY;

            this.posX = mouseX - draggingX - xDiff;
            this.posY = mouseY - draggingY - yDiff;
        }

        ShaderRenderer.INSTANCE.drawAndRun(shaders ->
                DrawUtil.drawRectRelative(workingPosX, workingPosY, width, predictHeight(), new Color(0, 0, 0, shaders ? 255 : 100).getRGB())
        );

        roboto17.drawXYCenteredString(moduleCategory.name, workingPosX + width / 2, workingPosY + height / 2, -1);

        final List<ModuleFeature> modules = new ArrayList<>(ModuleManager.getSingleton().getByCategory(moduleCategory));
        modules.sort((o1, o2) -> roboto17.getStringWidth(o2.getName()) - roboto17.getStringWidth(o1.getName()));

        float elementY = workingPosY + height;

        for(ModuleFeature moduleFeature : modules) {
            if(moduleFeature.isEnabled()) {
                DrawUtil.drawRectRelative(workingPosX, elementY, width, height, new Color(clickGuiModule.getEnabledRed(), clickGuiModule.getEnabledGreen(), clickGuiModule.getEnabledBlue(), clickGuiModule.getEnabledAlpha()).getRGB());
            }

            roboto17.drawXYCenteredString(moduleFeature.name, workingPosX + width / 2, elementY + height / 2, -1);

            if(nextModule == moduleFeature && (workingWindow == null || workingWindow.closed())) {
                workingWindow = new Window(moduleFeature, workingPosX + width + 5, elementY + height);
                nextModule = null;
            }

            if(workingWindow != null && workingWindow.module == moduleFeature) {
                workingWindow.update(workingPosX + width + 5, elementY);
            }

            elementY += height;
        }

        if(workingWindow != null) {
            workingWindow.draw(new ScreenContext(ScreenContext.Event.ON_DRAW, mouseX, mouseY, -1, -1, (char) -1));
        }
    }

    public boolean mouseClick(int mouseX, int mouseY, int button) {
        float workingPosX = getWorkingPosX();
        float workingPosY = getWorkingPosY();

        if(Boundings.isInBounds(mouseX, mouseY, workingPosX, workingPosY, width, height)) {
            dragging = true;
            draggingX = mouseX - workingPosX;
            draggingY = mouseY - workingPosY;
            return true;
        }

        float elementY = workingPosY + height;

        final List<ModuleFeature> modules = new ArrayList<>(ModuleManager.getSingleton().getByCategory(moduleCategory));
        modules.sort((o1, o2) -> roboto17.getStringWidth(o2.getName()) - roboto17.getStringWidth(o1.getName()));

        for(ModuleFeature moduleFeature : modules) {
            if(Boundings.isInBounds(mouseX, mouseY, workingPosX, elementY, width, height)) {
                if(button == 0)
                    moduleFeature.toggleEnabled();
                else if(button == 1)
                    if(workingWindow != null) {
                        if(workingWindow.module != moduleFeature)
                            nextModule = moduleFeature;
                        workingWindow.close();
                    }
                    else
                        workingWindow = new Window(moduleFeature, workingPosX + width + 5, elementY + height);

                return true;
            }

            elementY += height;
        }

        if(workingWindow != null)
            workingWindow.draw(new ScreenContext(ScreenContext.Event.ON_CLICK, mouseX, mouseY, button, -1, (char) -1));

        return false;
    }

    private float getWorkingPosX() {
        return lastFramePosX + (float) (((posX - lastFramePosX + predictMaxHeight()) * openingAnimation.getOutput()) / 100d) - predictMaxHeight();
    }

    private float getWorkingPosY() {
        return (float) (((posY + scrollAnimation.getValue() + predictMaxHeight()) * openingAnimation.getOutput()) / 100d) - predictMaxHeight();
    }

    @EventHandler
    public void onBackgroundTick(BackgroundEvent event) {
        openingAnimation.duration = clickGuiModule.animationDuration.getValue().intValue();
    }

    public boolean keyTyped(char typedChar, int keyCode) {
        if(keyCode == Keyboard.KEY_ESCAPE) {
            openingAnimation.setDirection(Direction.BACKWARDS);
            closing = true;
        }

        if(workingWindow != null)
            workingWindow.draw(new ScreenContext(ScreenContext.Event.ON_KEY, -1, -1, -1, keyCode, typedChar));

        return false;
    }

    private float predictHeight() {
        return ModuleManager.getSingleton().getByCategory(moduleCategory).size() * height + height;
    }
    
    private float predictMaxHeight() {
        float max = 0;
        
        for(ModuleCategory moduleCategory : ModuleCategory.values()) {
            if(!ModuleManager.getSingleton().getByCategory(moduleCategory).isEmpty()) {
                max = Math.max(ModuleManager.getSingleton().getByCategory(moduleCategory).size() * height + height, max);
            }
        }
        
        return max;
    }

}
