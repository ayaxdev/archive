package com.skidding.atlas.screen.draggable;

import com.skidding.atlas.font.ClientFontRenderer;
import com.skidding.atlas.font.FontManager;
import com.skidding.atlas.font.FontRendererValue;
import com.skidding.atlas.hud.HUDFactory;
import com.skidding.atlas.hud.HUDElement;
import com.skidding.atlas.hud.HUDManager;
import com.skidding.atlas.hud.util.Side;
import com.skidding.atlas.module.ModuleManager;
import com.skidding.atlas.module.impl.hud.ClickGuiModule;
import com.skidding.atlas.screen.ScreenContext;
import com.skidding.atlas.setting.SettingFeature;
import com.skidding.atlas.setting.SettingManager;
import com.skidding.atlas.setting.builder.impl.ModeBuilder;
import com.skidding.atlas.setting.builder.impl.SliderBuilder;
import com.skidding.atlas.util.animation.deprecated.DirectAnimation;
import com.skidding.atlas.util.math.MathUtil;
import com.skidding.atlas.util.render.DrawUtil;
import com.skidding.atlas.util.render.gl.GLUtil;
import com.skidding.atlas.util.render.shader.manager.ShaderRenderer;
import de.florianmichael.rclasses.math.integration.Boundings;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("unchecked")
public class DesignerUI extends GuiScreen {

    private State state = State.SELECTING;

    private float x = Float.MIN_VALUE,
            y = Float.MIN_VALUE;
    private float windowWidth = 150F;

    private boolean dragging;
    private float dragX, dragY;

    private HUDElement selected;

    private final ClientFontRenderer roboto17 = FontManager.getSingleton().get("Roboto", 17);
    private final ClickGuiModule clickGuiModule = ModuleManager.getSingleton().getByClass(ClickGuiModule.class);
    private final List<Object> expanded = new ArrayList<>();
    @SuppressWarnings("deprecation")
    private final DirectAnimation scrollAnimation = new DirectAnimation(0, 1);
    private float scroll;

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        final ScreenContext screenContext = new ScreenContext(ScreenContext.Event.ON_DRAW, mouseX, mouseY, -1, -1, (char) -1);
        final ScaledResolution scaledResolution = new ScaledResolution(mc);

        scroll += Mouse.getDWheel() / 10F;
        scroll = Math.min(0, scroll);

        scrollAnimation.interpolate(scroll);

        if(x == Float.MIN_VALUE || y == Float.MIN_VALUE) {
            x = scaledResolution.getScaledWidth() / 2f + 10f;
            y = scaledResolution.getScaledHeight() / 2f + 10f;
        }

        if(!Mouse.isButtonDown(0))
            dragging = false;

        if(dragging) {
            x = mouseX - dragX;
            y = mouseY - dragY;
        }

        windowWidth = 150F;

        if(selected != null) {
            for(SettingFeature<?> settingFeature : SettingManager.getSingleton().getByOwner(selected)) {
                switch (settingFeature.type) {
                    case "Text" -> {
                        windowWidth = Math.max(windowWidth,
                                roboto17.getStringWidth(STR."\{settingFeature.name}: \{settingFeature.getValue()}\{expanded.contains(settingFeature) ? (System.currentTimeMillis() % 1000 < 500 ? "_" : "") : ""}") + 4);
                    }
                    case "Mode" -> {
                        windowWidth = Math.max(windowWidth,
                                roboto17.getStringWidth(STR."\{settingFeature.name}: \{settingFeature.getValue()}") + 4);

                        if (expanded.contains(settingFeature)) {
                            final ModeBuilder modeBuilder = (ModeBuilder) settingFeature.getBuilder();

                            for(String mode : modeBuilder.modes) {
                                windowWidth = Math.max(windowWidth,
                                        roboto17.getStringWidth(mode) + 10 + 4);
                            }
                        }
                    }
                    case "Color" -> {
                        windowWidth = Math.max(windowWidth,
                                roboto17.getStringWidth(STR."\{settingFeature.name}") + 4);

                        if(expanded.contains(settingFeature)) {
                            windowWidth = Math.max(windowWidth,
                                    roboto17.getStringWidth(STR."\{settingFeature.name} red") + 4);
                            windowWidth = Math.max(windowWidth,
                                    roboto17.getStringWidth(STR."\{settingFeature.name} green") + 4);
                            windowWidth = Math.max(windowWidth,
                                    roboto17.getStringWidth(STR."\{settingFeature.name} blue") + 4);
                            windowWidth = Math.max(windowWidth,
                                    roboto17.getStringWidth(STR."\{settingFeature.name} alpha") + 4);
                        }
                    }
                    case "Font" -> {
                        windowWidth = Math.max(windowWidth,
                                roboto17.getStringWidth(STR."\{settingFeature.name} family") + 4);
                        windowWidth = Math.max(windowWidth,
                                roboto17.getStringWidth(STR."\{settingFeature.name} type") + 4);
                    }
                    case "Check" -> windowWidth = Math.max(windowWidth,
                            roboto17.getStringWidth(STR."\{settingFeature.name}: X") + 4);
                    case "Slider" -> windowWidth = Math.max(windowWidth,
                            roboto17.getStringWidth(STR."\{settingFeature.name}") + 4);
                }
            }
        }

        ShaderRenderer.INSTANCE.drawAndRun(runningShaders -> {
            DrawUtil.drawRectRelative(x, y + roboto17.getHeight(), windowWidth, 200, new Color(0, 0, 0, runningShaders ? 255 : 100).getRGB());
            DrawUtil.drawRectRelative(x, y, windowWidth, roboto17.getHeight(), new Color(clickGuiModule.getEnabledRed(), clickGuiModule.getEnabledGreen(), clickGuiModule.getEnabledBlue(), clickGuiModule.getEnabledAlpha()).getRGB());
        });

        float elementX = x + 2f;
        float elementY = y + 0.5f;

        roboto17.drawString(selected == null ? state == State.ADDING ? "Add" : "Home" : STR."\{selected.name.split(":")[0]}\{selected.getPreview() == null ? "" : STR." - \{selected.getPreview()}"}", elementX, elementY, -1);

        elementY += roboto17.getHeight() + 1f + Math.round(scrollAnimation.getValueF());

        GLUtil.startScissorBox();
        GLUtil.drawScissorBox(x, y + roboto17.getHeight() + 0.5f, windowWidth, 200);

        if(selected != null) {
            roboto17.drawString("Back", elementX, elementY, -1);
            elementY += roboto17.getHeight();

            roboto17.drawString("Remove", elementX, elementY, -1);
            elementY += roboto17.getHeight();

            roboto17.drawString("---", elementX, elementY, -1);
            elementY += roboto17.getHeight();

            for(SettingFeature<?> settingFeature : SettingManager.getSingleton().getByOwner(selected)) {
                switch (settingFeature.type) {
                    case "Text" -> {
                        roboto17.drawString(STR."\{settingFeature.name}: \{settingFeature.getValue()}\{expanded.contains(settingFeature) ? (System.currentTimeMillis() % 1000 < 500 ? "_" : "") : ""}", elementX, elementY, -1);
                        elementY += roboto17.getHeight();
                    }
                    case "Mode" -> {
                        final SettingFeature<String> modeSetting = (SettingFeature<String>) settingFeature;
                        final ModeBuilder modeBuilder = (ModeBuilder) modeSetting.getBuilder();

                        final AtomicReference<Float> atomicElementY = new AtomicReference<>(elementY);
                        final AtomicReference<String> atomicValue = new AtomicReference<>(modeSetting.getValue());

                        mode(screenContext, modeSetting.name, atomicElementY, elementX, atomicValue, modeSetting, modeBuilder.modes);

                        elementY = atomicElementY.get();
                        modeSetting.setValue(atomicValue.get());
                    }
                    case "Color" -> {
                        final SettingFeature<Integer> colorSetting = (SettingFeature<Integer>) settingFeature;

                        roboto17.drawString(colorSetting.name, elementX, elementY, -1);

                        if(screenContext.event() == ScreenContext.Event.ON_CLICK && Boundings.isInBounds(screenContext.mouseX(), screenContext.mouseY(), elementX, elementY, windowWidth, roboto17.getHeight())) {
                            if(expanded.contains(settingFeature)) {
                                expanded.remove(settingFeature);
                            } else {
                                expanded.add(settingFeature);
                            }
                        }

                        elementY += roboto17.getHeight();

                        final AtomicReference<Float> atomicElementY = new AtomicReference<>(elementY);

                        if(expanded.contains(settingFeature)) {
                            final AtomicReference<Float> red = new AtomicReference<>((float) (colorSetting.getValue() >> 16 & 0xFF));
                            slider(screenContext, STR."\{colorSetting.name} red", atomicElementY, elementX, red, 0, 255, 1);

                            final AtomicReference<Float> green = new AtomicReference<>((float) (colorSetting.getValue() >> 8 & 0xFF));
                            slider(screenContext, STR."\{colorSetting.name} green", atomicElementY, elementX, green, 0, 255, 1);

                            final AtomicReference<Float> blue = new AtomicReference<>((float) (colorSetting.getValue() & 0xFF));
                            slider(screenContext, STR."\{colorSetting.name} blue", atomicElementY, elementX, blue, 0, 255, 1);

                            final AtomicReference<Float> alpha = new AtomicReference<>((float) (colorSetting.getValue() >> 24 & 0xFF));
                            slider(screenContext, STR."\{colorSetting.name} alpha", atomicElementY, elementX, alpha, 0, 255, 1);

                            colorSetting.setValue(new Color(red.get().intValue(), green.get().intValue(), blue.get().intValue(), alpha.get().intValue()).getRGB());
                        }

                        elementY = atomicElementY.get();
                    }
                    case "Check" -> {
                        final SettingFeature<Boolean> booleanSetting = (SettingFeature<Boolean>) settingFeature;
                        roboto17.drawString(STR."\{settingFeature.name}: \{booleanSetting.getValue() ? "X" : ""}", elementX, elementY, -1);
                        elementY += roboto17.getHeight();
                    }
                    case "Slider" -> {
                        final SettingFeature<Float> numberSetting = (SettingFeature<Float>) settingFeature;
                        final SliderBuilder sliderBuilder = (SliderBuilder) settingFeature.getBuilder();

                        final AtomicReference<Float> atomicElementY = new AtomicReference<>(elementY);
                        final AtomicReference<Float> atomicValue = new AtomicReference<>(numberSetting.getValue());

                        slider(screenContext, numberSetting.name, atomicElementY, elementX, atomicValue, sliderBuilder.minimum, sliderBuilder.maximum, sliderBuilder.decimals);

                        elementY = atomicElementY.get();
                        numberSetting.setValue(atomicValue.get());
                    }
                    case "Font" -> {
                        final SettingFeature<FontRendererValue> fontSetting = (SettingFeature<FontRendererValue>) settingFeature;
                        final AtomicReference<Float> atomicElementY = new AtomicReference<>(elementY);

                        String family = fontSetting.getValue().family(),
                                type = fontSetting.getValue().fontType();
                        float size;

                        final HashMap<String, List<String>> available = FontManager.getSingleton().available;

                        /*Family*/ {
                            final AtomicReference<String> value = new AtomicReference<>(family);

                            final String[] familyArray = new String[available.entrySet().size()];
                            int familyIndex = 0;
                            for (String element : available.keySet()) {
                                familyArray[familyIndex++] = element;
                            }

                            mode(screenContext, STR."\{fontSetting.getName()} family", atomicElementY, elementX, value, STR."\{fontSetting.getName()} family", familyArray);

                            family = value.get();
                        }

                        if(!fontSetting.getValue().family().equals(family))
                            type = "Regular";

                        /*Type*/ {
                            final AtomicReference<String> value = new AtomicReference<>(type);

                            String[] typeArray = new String[available.get(family).size()];
                            int typeIndex = 0;
                            for (String element : available.get(family)) {
                                typeArray[typeIndex++] = element;
                            }

                            mode(screenContext, STR."\{fontSetting.getName()} type", atomicElementY, elementX, value, STR."\{fontSetting.getName()} type", typeArray);

                            type = value.get();
                        }

                        /*Size*/ {
                            final AtomicReference<Float> value = new AtomicReference<>((float) Math.round(fontSetting.getValue().size()));

                            slider(screenContext, STR."\{fontSetting.getName()} size", atomicElementY, elementX, value, 0, 50, 1);

                            size = Math.round(value.get());
                        }

                        if(!family.equals(fontSetting.getValue().family()) || !type.equals(fontSetting.getValue().fontType()) || size != fontSetting.getValue().size())
                            fontSetting.setValue(new FontRendererValue(family, type, size, FontManager.getSingleton().get(family, type, size)));

                        elementY = atomicElementY.get();
                    }
                }
            }
        } else {
            switch (state) {
                case ADDING -> {
                    roboto17.drawString("Back", elementX, elementY, -1);
                    elementY += roboto17.getHeight();
                    for(HUDFactory hudFactory : HUDManager.getSingleton().getFeatures()) {
                        roboto17.drawString(STR." - \{hudFactory.name}", elementX, elementY, -1);
                        elementY += roboto17.getHeight();
                    }
                }
                case SELECTING -> {
                    roboto17.drawString("Add", elementX, elementY, -1);
                    elementY += roboto17.getHeight();
                    for(HUDElement element : HUDManager.getSingleton().renderElements) {
                        roboto17.drawString(STR."\{element.name.split(":")[0]}\{element.getPreview() == null ? "" : STR." - \{element.getPreview()}"}", elementX, elementY, -1);
                        elementY += roboto17.getHeight();
                    }
                }
            }
        }

        GLUtil.endScissorBox();

        for (HUDElement element : HUDManager.getSingleton().renderElements) {
            final float scaledX = mouseX / element.scale;
            final float scaledY = mouseY / element.scale;
            final float prevMouseX = element.prevMouseX;
            final float prevMouseY = element.prevMouseY;

            element.prevMouseX = scaledX;
            element.prevMouseY = scaledY;

            if (element.drag) {
                final double moveX = scaledX - prevMouseX;
                final double moveY = scaledY - prevMouseY;

                if (moveX == 0F && moveY == 0F)
                    continue;

                if(Boundings.isInBounds(mouseX, mouseY, scaledX, scaledY, element.width, element.height)) {
                    element.setRenderX((float) moveX);
                    element.setRenderY((float) moveY);
                }
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        final ScreenContext screenContext = new ScreenContext(ScreenContext.Event.ON_KEY, -1, -1, -1, keyCode, typedChar);

        if (keyCode == Keyboard.KEY_ESCAPE && state != State.SELECTING) {
            state = State.SELECTING;
            selected = null;

            return;
        }

        float elementX = x + 2f;
        float elementY = y + 0.5f;

        elementY += roboto17.getHeight() + 1f + scrollAnimation.getValueF();

        if(selected != null) {
            elementY += roboto17.getHeight() * 3;

            for(SettingFeature<?> settingFeature : SettingManager.getSingleton().getByOwner(selected)) {
                switch (settingFeature.type) {
                    case "Text" -> {
                        final SettingFeature<String> textSetting = (SettingFeature<String>) settingFeature;

                        if (expanded.contains(textSetting)) {
                            if(screenContext.key() == Keyboard.KEY_ESCAPE || screenContext.key() == Keyboard.KEY_RETURN) {
                                expanded.remove(textSetting);
                            } else if (screenContext.key() == Keyboard.KEY_BACK) {
                                if (!textSetting.getValue().isEmpty()) {
                                    textSetting.setValue(textSetting.getValue().substring(0, textSetting.getValue().length() - 1));
                                }
                            } else if (ChatAllowedCharacters.isAllowedCharacter(screenContext.character())) {
                                textSetting.setValue(textSetting.getValue() + screenContext.character());
                            }
                        }

                        elementY += roboto17.getHeight();
                    }
                    case "Mode" -> {
                        final SettingFeature<String> modeSetting = (SettingFeature<String>) settingFeature;
                        final ModeBuilder modeBuilder = (ModeBuilder) modeSetting.getBuilder();

                        final AtomicReference<Float> atomicElementY = new AtomicReference<>(elementY);
                        final AtomicReference<String> atomicValue = new AtomicReference<>(modeSetting.getValue());

                        mode(screenContext, modeSetting.name, atomicElementY, elementX, atomicValue, modeSetting, modeBuilder.modes);

                        elementY = atomicElementY.get();
                        modeSetting.setValue(atomicValue.get());
                    }
                    case "Check" -> {
                        elementY += roboto17.getHeight();
                    }
                    case "Color" -> {
                        final SettingFeature<Integer> colorSetting = (SettingFeature<Integer>) settingFeature;

                        roboto17.drawString(colorSetting.name, elementX, elementY, -1);

                        if(screenContext.event() == ScreenContext.Event.ON_CLICK && Boundings.isInBounds(screenContext.mouseX(), screenContext.mouseY(), elementX, elementY, windowWidth, roboto17.getHeight())) {
                            if(expanded.contains(settingFeature)) {
                                expanded.remove(settingFeature);
                            } else {
                                expanded.add(settingFeature);
                            }
                        }

                        elementY += roboto17.getHeight();

                        final AtomicReference<Float> atomicElementY = new AtomicReference<>(elementY);

                        if(expanded.contains(settingFeature)) {
                            final AtomicReference<Float> red = new AtomicReference<>((float) (colorSetting.getValue() >> 16 & 0xFF));
                            slider(screenContext, STR."\{colorSetting.name} red", atomicElementY, elementX, red, 0, 255, 1);

                            final AtomicReference<Float> green = new AtomicReference<>((float) (colorSetting.getValue() >> 8 & 0xFF));
                            slider(screenContext, STR."\{colorSetting.name} green", atomicElementY, elementX, green, 0, 255, 1);

                            final AtomicReference<Float> blue = new AtomicReference<>((float) (colorSetting.getValue() & 0xFF));
                            slider(screenContext, STR."\{colorSetting.name} blue", atomicElementY, elementX, blue, 0, 255, 1);

                            final AtomicReference<Float> alpha = new AtomicReference<>((float) (colorSetting.getValue() >> 24 & 0xFF));
                            slider(screenContext, STR."\{colorSetting.name} alpha", atomicElementY, elementX, alpha, 0, 255, 1);

                            colorSetting.setValue(new Color(red.get().intValue(), green.get().intValue(), blue.get().intValue(), alpha.get().intValue()).getRGB());
                        }

                        elementY = atomicElementY.get();
                    }
                    case "Slider" -> {
                        final SettingFeature<Float> numberSetting = (SettingFeature<Float>) settingFeature;
                        final SliderBuilder sliderBuilder = (SliderBuilder) settingFeature.getBuilder();

                        final AtomicReference<Float> atomicElementY = new AtomicReference<>(elementY);
                        final AtomicReference<Float> atomicValue = new AtomicReference<>(numberSetting.getValue());

                        slider(screenContext, numberSetting.name, atomicElementY, elementX, atomicValue, sliderBuilder.minimum, sliderBuilder.maximum, sliderBuilder.decimals);

                        elementY = atomicElementY.get();
                        numberSetting.setValue(atomicValue.get());
                    }
                    case "Font" -> {
                        final SettingFeature<FontRendererValue> fontSetting = (SettingFeature<FontRendererValue>) settingFeature;
                        AtomicReference<Float> atomicElementY = new AtomicReference<>(elementY);

                        String family = fontSetting.getValue().family(),
                                type = fontSetting.getValue().fontType();
                        float size;

                        final HashMap<String, List<String>> available = FontManager.getSingleton().available;

                        /*Family*/ {
                            final AtomicReference<String> value = new AtomicReference<>(family);

                            final String[] familyArray = new String[available.entrySet().size()];
                            int familyIndex = 0;
                            for (String element : available.keySet()) {
                                familyArray[familyIndex++] = element;
                            }

                            mode(screenContext, STR."\{fontSetting.getName()} family", atomicElementY, elementX, value, STR."\{fontSetting.getName()} family", familyArray);

                            family = value.get();
                        }

                        if(!fontSetting.getValue().family().equals(family))
                            type = "Regular";

                        /*Type*/ {
                            final AtomicReference<String> value = new AtomicReference<>(type);

                            String[] typeArray = new String[available.get(family).size()];
                            int typeIndex = 0;
                            for (String element : available.get(family)) {
                                typeArray[typeIndex++] = element;
                            }

                            mode(screenContext, STR."\{fontSetting.getName()} type", atomicElementY, elementX, value, STR."\{fontSetting.getName()} type", typeArray);

                            type = value.get();
                        }

                        /*Size*/ {
                            final AtomicReference<Float> value = new AtomicReference<>((float) Math.round(fontSetting.getValue().size()));

                            slider(screenContext, STR."\{fontSetting.getName()} size", atomicElementY, elementX, value, 0, 50, 1);

                            size = Math.round(value.get());
                        }

                        if(!family.equals(fontSetting.getValue().family()) || !type.equals(fontSetting.getValue().fontType()) || size != fontSetting.getValue().size())
                            fontSetting.setValue(new FontRendererValue(family, type, size, FontManager.getSingleton().get(family, type, size)));

                        elementY = atomicElementY.get();
                    }
                }
            }
        }

        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        final ScreenContext screenContext = new ScreenContext(ScreenContext.Event.ON_CLICK, mouseX, mouseY, mouseButton, -1, (char) -1);

        float elementX = x + 2f;
        float elementY = y + 0.5f;

        if(Boundings.isInBounds(mouseX, mouseY, elementX, elementY, windowWidth, roboto17.getHeight() + 1f)) {
            dragging = true;
            dragX = mouseX - x;
            dragY = mouseY - y;
        }

        elementY += roboto17.getHeight() + 1f + scrollAnimation.getValueF();

        if(selected != null) {
            if(Boundings.isInBounds(mouseX, mouseY, elementX, elementY, windowWidth, roboto17.getHeight())) {
                selected = null;
                return;
            }
            elementY += roboto17.getHeight();
            if(Boundings.isInBounds(mouseX, mouseY, elementX, elementY, windowWidth, roboto17.getHeight())) {
                HUDManager.getSingleton().remove(selected);
                selected = null;
                return;
            }
            elementY += roboto17.getHeight() * 2;


            for(SettingFeature<?> settingFeature : SettingManager.getSingleton().getByOwner(selected)) {
                switch (settingFeature.type) {
                    case "Text" -> {
                        if(Boundings.isInBounds(mouseX, mouseY, elementX, elementY, windowWidth, roboto17.getHeight())) {
                            if(expanded.contains(settingFeature))
                                expanded.remove(settingFeature);
                            else
                                expanded.add(settingFeature);
                        }

                        elementY += roboto17.getHeight();
                    }
                    case "Mode" -> {
                        final SettingFeature<String> modeSetting = (SettingFeature<String>) settingFeature;
                        final ModeBuilder modeBuilder = (ModeBuilder) modeSetting.getBuilder();

                        final AtomicReference<Float> atomicElementY = new AtomicReference<>(elementY);
                        final AtomicReference<String> atomicValue = new AtomicReference<>(modeSetting.getValue());

                        mode(screenContext, modeSetting.name, atomicElementY, elementX, atomicValue, modeSetting, modeBuilder.modes);

                        elementY = atomicElementY.get();
                        modeSetting.setValue(atomicValue.get());
                    }
                    case "Check" -> {
                        final SettingFeature<Boolean> booleanSetting = (SettingFeature<Boolean>) settingFeature;

                        if(Boundings.isInBounds(mouseX, mouseY, elementX, elementY, windowWidth, roboto17.getHeight())) {
                            booleanSetting.setValue(!booleanSetting.getValue());
                        }

                        elementY += roboto17.getHeight();
                    }
                    case "Color" -> {
                        final SettingFeature<Integer> colorSetting = (SettingFeature<Integer>) settingFeature;

                        roboto17.drawString(colorSetting.name, elementX, elementY, -1);

                        if(screenContext.event() == ScreenContext.Event.ON_CLICK && Boundings.isInBounds(screenContext.mouseX(), screenContext.mouseY(), elementX, elementY, windowWidth, roboto17.getHeight())) {
                            if(expanded.contains(settingFeature)) {
                                expanded.remove(settingFeature);
                            } else {
                                expanded.add(settingFeature);
                            }
                        }

                        elementY += roboto17.getHeight();

                        final AtomicReference<Float> atomicElementY = new AtomicReference<>(elementY);

                        if(expanded.contains(settingFeature)) {
                            final AtomicReference<Float> red = new AtomicReference<>((float) (colorSetting.getValue() >> 16 & 0xFF));
                            slider(screenContext, STR."\{colorSetting.name} red", atomicElementY, elementX, red, 0, 255, 1);

                            final AtomicReference<Float> green = new AtomicReference<>((float) (colorSetting.getValue() >> 8 & 0xFF));
                            slider(screenContext, STR."\{colorSetting.name} green", atomicElementY, elementX, green, 0, 255, 1);

                            final AtomicReference<Float> blue = new AtomicReference<>((float) (colorSetting.getValue() & 0xFF));
                            slider(screenContext, STR."\{colorSetting.name} blue", atomicElementY, elementX, blue, 0, 255, 1);

                            final AtomicReference<Float> alpha = new AtomicReference<>((float) (colorSetting.getValue() >> 24 & 0xFF));
                            slider(screenContext, STR."\{colorSetting.name} alpha", atomicElementY, elementX, alpha, 0, 255, 1);

                            colorSetting.setValue(new Color(red.get().intValue(), green.get().intValue(), blue.get().intValue(), alpha.get().intValue()).getRGB());
                        }

                        elementY = atomicElementY.get();
                    }
                    case "Slider" -> {
                        final SettingFeature<Float> numberSetting = (SettingFeature<Float>) settingFeature;
                        final SliderBuilder sliderBuilder = (SliderBuilder) settingFeature.getBuilder();

                        final AtomicReference<Float> atomicElementY = new AtomicReference<>(elementY);
                        final AtomicReference<Float> atomicValue = new AtomicReference<>(numberSetting.getValue());

                        slider(screenContext, numberSetting.name, atomicElementY, elementX, atomicValue, sliderBuilder.minimum, sliderBuilder.maximum, sliderBuilder.decimals);

                        elementY = atomicElementY.get();
                        numberSetting.setValue(atomicValue.get());
                    }
                    case "Font" -> {
                        final SettingFeature<FontRendererValue> fontSetting = (SettingFeature<FontRendererValue>) settingFeature;
                        AtomicReference<Float> atomicElementY = new AtomicReference<>(elementY);

                        String family = fontSetting.getValue().family(),
                                type = fontSetting.getValue().fontType();
                        float size;

                        final HashMap<String, List<String>> available = FontManager.getSingleton().available;

                        /*Family*/ {
                            final AtomicReference<String> value = new AtomicReference<>(family);

                            final String[] familyArray = new String[available.entrySet().size()];
                            int familyIndex = 0;
                            for (String element : available.keySet()) {
                                familyArray[familyIndex++] = element;
                            }

                            mode(screenContext, STR."\{fontSetting.getName()} family", atomicElementY, elementX, value, STR."\{fontSetting.getName()} family", familyArray);

                            family = value.get();
                        }

                        if(!fontSetting.getValue().family().equals(family))
                            type = "Regular";

                        /*Type*/ {
                            final AtomicReference<String> value = new AtomicReference<>(type);

                            String[] typeArray = new String[available.get(family).size()];
                            int typeIndex = 0;
                            for (String element : available.get(family)) {
                                typeArray[typeIndex++] = element;
                            }

                            mode(screenContext, STR."\{fontSetting.getName()} type", atomicElementY, elementX, value, STR."\{fontSetting.getName()} type", typeArray);

                            type = value.get();
                        }

                        /*Size*/ {
                            final AtomicReference<Float> value = new AtomicReference<>((float) Math.round(fontSetting.getValue().size()));

                            slider(screenContext, STR."\{fontSetting.getName()} size", atomicElementY, elementX, value, 0, 50, 1);

                            size = Math.round(value.get());
                        }

                        if(!family.equals(fontSetting.getValue().family()) || !type.equals(fontSetting.getValue().fontType()) || size != fontSetting.getValue().size())
                            fontSetting.setValue(new FontRendererValue(family, type, size, FontManager.getSingleton().get(family, type, size)));

                        elementY = atomicElementY.get();
                    }
                }
            }
        } else {
            switch (state) {
                case ADDING -> {
                    if(Boundings.isInBounds(mouseX, mouseY, elementX, elementY, windowWidth, roboto17.getHeight()))
                        state = State.SELECTING;
                    elementY += roboto17.getHeight();
                    for(HUDFactory hudFactory : HUDManager.getSingleton().getFeatures()) {
                        if(Boundings.isInBounds(mouseX, mouseY, elementX, elementY, windowWidth, roboto17.getHeight())) {
                            final HUDElement hudElement = hudFactory.build(STR."\{hudFactory.name}:\{Math.round(Math.random() * 100000)}", 10, 10, 0, Side.defaultSide());
                            HUDManager.getSingleton().add(hudElement);
                            selected = hudElement;

                            return;
                        }
                        elementY += roboto17.getHeight();
                    }
                }
                case SELECTING -> {
                    if(Boundings.isInBounds(mouseX, mouseY, elementX, elementY, windowWidth, roboto17.getHeight()))
                        state = State.ADDING;

                    elementY += roboto17.getHeight();

                    for(HUDElement renderElement : HUDManager.getSingleton().renderElements) {
                        if(Boundings.isInBounds(mouseX, mouseY, elementX, elementY, windowWidth, roboto17.getHeight())) {
                            selected = renderElement;
                        }

                        elementY += roboto17.getHeight();
                    }
                }
            }
        }

        for (HUDElement element : HUDManager.getSingleton().renderElements.reversed()) {
            final float scaledX = mouseX / element.scale;
            final float scaledY = mouseY / element.scale;

            if (!Boundings.isInBounds(scaledX, scaledY, element.getRenderX(), element.getRenderY(), element.width, element.height))
                continue;

            if(mouseButton == 0) {
                selected = element;
                element.drag = true;
                break;
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        for (HUDElement element : HUDManager.getSingleton().renderElements) {
            element.drag = false;
        }

        super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    private void slider(ScreenContext screenContext, String name, AtomicReference<Float> elementY, float elementX, AtomicReference<Float> value, float min, float max, int decimals) {
        switch (screenContext.event()) {
            case ON_CLICK -> elementY.set(elementY.get() + roboto17.getHeight() + 3);
            case ON_DRAW -> {
                final float sliderWidth = (windowWidth - 8F);

                if(Mouse.isButtonDown(0) && Boundings.isInBounds(screenContext.mouseX(), screenContext.mouseY(), elementX, elementY.get(), windowWidth, roboto17.getHeight() + 3)) {
                    float percentage = screenContext.mouseX() - elementX - 2;
                    percentage /= sliderWidth;
                    percentage = Math.max(Math.min(percentage, 1), 0);

                    value.set((min + (max - min) * percentage));
                    value.set(MathUtil.roundAvoid(value.get(), decimals));
                }

                roboto17.drawString(STR."\{name}: \{value.get()}", elementX, elementY.get(), -1);
                elementY.set(elementY.get() + roboto17.getHeight());

                final float selectedSliderLength = MathHelper.floor_double((value.get() - min) / (max - min) * sliderWidth);

                DrawUtil.drawRectRelative(elementX + 2, elementY.get(), selectedSliderLength, 2, new Color(clickGuiModule.getEnabledRed(), clickGuiModule.getEnabledGreen(), clickGuiModule.getEnabledBlue(), 100).getRGB());
                DrawUtil.drawRectRelative(elementX + 2 + selectedSliderLength, elementY.get(), sliderWidth - selectedSliderLength, 2, new Color(0, 0, 0, 100).getRGB());

                elementY.set(elementY.get() + 3);
            }
        }
    }

    private void mode(ScreenContext screenContext, String name, AtomicReference<Float> elementY, float elementX, AtomicReference<String> value, Object hook, String[] modes) {
        switch (screenContext.event()) {
            case ON_CLICK -> {
                if(Boundings.isInBounds(screenContext.mouseX(), screenContext.mouseY(), elementX, elementY.get(), windowWidth, roboto17.getHeight())) {
                    if(expanded.contains(hook))
                        expanded.remove(hook);
                    else
                        expanded.add(hook);
                }

                elementY.set(elementY.get() + roboto17.getHeight());

                if(expanded.contains(hook)) {
                    for(String mode : modes) {
                        if(Boundings.isInBounds(screenContext.mouseX(), screenContext.mouseY(), elementX, elementY.get(), windowWidth, roboto17.getHeight())) {
                            value.set(mode);
                            expanded.remove(hook);
                        }

                        elementY.set(elementY.get() + roboto17.getHeight());
                    }
                }
            }

            case ON_DRAW -> {
                roboto17.drawString(STR."\{name}: \{value.get()}", elementX, elementY.get(), -1);
                elementY.set(elementY.get() + roboto17.getHeight());

                if(expanded.contains(hook)) {
                    for(String mode : modes) {
                        roboto17.drawString(mode, elementX + 10, elementY.get(), -1);

                        elementY.set(elementY.get() + roboto17.getHeight());
                    }
                }
            }
        }
    }

    public enum State {
        ADDING, SELECTING;
    }

}
