package com.skidding.atlas.screen.simple;

import com.skidding.atlas.font.ClientFontRenderer;
import com.skidding.atlas.font.FontManager;
import com.skidding.atlas.font.FontRendererValue;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.module.ModuleManager;
import com.skidding.atlas.module.impl.hud.ClickGuiModule;
import com.skidding.atlas.screen.ScreenContext;
import com.skidding.atlas.setting.SettingFeature;
import com.skidding.atlas.setting.SettingManager;
import com.skidding.atlas.setting.builder.impl.ModeBuilder;
import com.skidding.atlas.setting.builder.impl.SliderBuilder;
import com.skidding.atlas.util.animation.Animation;
import com.skidding.atlas.util.animation.Direction;
import com.skidding.atlas.util.animation.impl.SmoothStepAnimation;
import com.skidding.atlas.util.math.MathUtil;
import com.skidding.atlas.util.render.DrawUtil;
import com.skidding.atlas.util.render.gl.GLUtil;
import com.skidding.atlas.util.render.shader.manager.ShaderRenderer;
import de.florianmichael.rclasses.math.Arithmetics;
import de.florianmichael.rclasses.math.integration.Boundings;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Window {

    public final ModuleFeature module;
    private float windowX, windowY;

    private boolean interpolated = false;
    private final Animation openingAnimation = new SmoothStepAnimation(250, 1000f, Direction.BACKWARDS);

    public Window(ModuleFeature module, float windowX, float windowY) {
        this.module = module;
        this.windowX = windowX;
        this.windowY = windowY;
    }

    private SettingFeature<?> lastInteractedWith = null;

    private final ClickGuiModule clickGuiModule = ModuleManager.getSingleton().getByClass(ClickGuiModule.class);
    private final FontManager fontManager = FontManager.getSingleton();
    private final ClientFontRenderer roboto17 = FontManager.getSingleton().get("Roboto", 17);
    private final List<Object> interacting = new ArrayList<>();

    public void update(float windowX, float windowY) {
        this.windowX = windowX;
        this.windowY = windowY;
    }

    public void draw(ScreenContext screenContext) {
        if(screenContext.event() == ScreenContext.Event.ON_DRAW) {
            ShaderRenderer.INSTANCE.drawAndRun(shaders -> internalDraw(screenContext, shaders));
        } else {
            internalDraw(screenContext, false);
        }
    }

    @SuppressWarnings("unchecked")
    private void internalDraw(ScreenContext screenContext, boolean shaders) {
        final Collection<SettingFeature<?>> settings = SettingManager.getSingleton().getByOwner(module);

        if(screenContext.event() == ScreenContext.Event.ON_DRAW) {
            if(!interpolated) {
                openingAnimation.setDirection(Direction.FORWARDS);
                openingAnimation.reset();

                interpolated = true;
            }

            GLUtil.scaleStart(windowX, windowY, openingAnimation.getOutput() / 1000d);
        }

        float windowHeight = 3,
                windowWidth = 0;

        for(SettingFeature<?> setting : settings) {
            if(!setting.isVisible()) {
                continue;
            }

            switch (setting.type) {
                case "Checkbox" -> {
                    final float width = roboto17.getStringWidth(setting.getName()) + 5 + 15,
                            height = Math.round(roboto17.getHeight() + 1f);

                    windowWidth = Math.max(windowWidth, width);
                    windowHeight += height;
                }
                case "Text" -> {
                    final float width = roboto17.getStringWidth(STR."\{setting.name}: \{setting.getValue()}\{interacting.contains(setting) ? (System.currentTimeMillis() % 1000 < 500 ? "_" : "") : ""}") + 5,
                            height = Math.round(roboto17.getHeight() + 1f);

                    windowWidth = Math.max(windowWidth, width);
                    windowHeight += height;
                }
                case "Color" -> {
                    windowWidth = Math.max(windowWidth, 110);
                    windowHeight += interacting.contains(setting) ? (Math.round(roboto17.getHeight() + 1f) + 4) * 5 : Math.round(roboto17.getHeight() + 1f);
                }
                case "Slider" -> {
                    final SettingFeature<Float> sliderSetting = (SettingFeature<Float>) setting;
                    final SliderBuilder sliderBuilder = (SliderBuilder) sliderSetting.getBuilder();

                    final float width = Math.max(roboto17.getStringWidth(STR."\{sliderSetting.getName()}: \{String.format(STR."%.\{sliderBuilder.decimals}f", sliderSetting.getValue())}") + 5, 90),
                            height = Math.round(roboto17.getHeight() + 1f) + 4;

                    windowWidth = Math.max(windowWidth, width);
                    windowHeight += height;
                }
                case "Mode" -> {
                    final SettingFeature<String> stringSetting = (SettingFeature<String>) setting;
                    final ModeBuilder modeBuilder  = (ModeBuilder) stringSetting.getBuilder();
                    final float baseHeight = Math.round(roboto17.getHeight() + 1f);
                    final boolean expanded = interacting.contains(setting);

                    final float width = roboto17.getStringWidth(setting.getName()) + 5 + roboto17.getStringWidth(setting.getValue().toString()) + 2.5f + 5,
                            height = baseHeight + (expanded ? modeBuilder.modes.length * baseHeight : 0);

                    windowWidth = Math.max(windowWidth, width);
                    windowHeight += height;
                }
                case "Font" -> {
                    final SettingFeature<FontRendererValue> fontSetting = (SettingFeature<FontRendererValue>) setting;

                    final List<Triple<String, String, Integer>> modeValues = new ArrayList<>();

                    modeValues.add(new ImmutableTriple<>(STR."\{fontSetting.getName()} family", fontSetting.getValue().family(), fontManager.available.keySet().size()));
                    modeValues.add(new ImmutableTriple<>(STR."\{fontSetting.getName()} type", fontSetting.getValue().fontType(), fontManager.available.get(fontSetting.getValue().family()).size()));

                    for(Triple<String, String, Integer> mode : modeValues) {
                        final float baseHeight = Math.round(roboto17.getHeight() + 1f);
                        final boolean expanded = interacting.contains(mode.getLeft());

                        final float width = roboto17.getStringWidth(mode.getLeft()) + 5 + roboto17.getStringWidth(mode.getMiddle()) + 2.5f + 5,
                                height = baseHeight + (expanded ? mode.getRight() * baseHeight : 0);

                        windowWidth = Math.max(windowWidth, width);
                        windowHeight += height;
                    }

                    final float width = Math.max(roboto17.getStringWidth(STR."\{fontSetting.getName()} size: \{Math.round(fontSetting.getValue().size())}") + 5, 90),
                            height = Math.round(roboto17.getHeight() + 1f) + 4;

                    windowWidth = Math.max(windowWidth, width);
                    windowHeight += height;
                }
            }
        }

        windowWidth = Math.max(windowWidth, 100);

        if(!shaders) {
            float settingY = windowY + 1.5f;

            for(SettingFeature<?> setting : settings) {
                if(!setting.isVisible()) {
                    continue;
                }

                switch (setting.type) {
                    case "Checkbox", "Text" -> {
                        final float height = Math.round(roboto17.getHeight() + 1f);

                        if(screenContext.event() == ScreenContext.Event.ON_CLICK) {
                            if(Boundings.isInBounds(screenContext.mouseX(), screenContext.mouseY(), windowX, settingY, windowWidth, height)) {
                                lastInteractedWith = setting;
                            }
                        }

                        settingY += height;
                    }
                    case "Color" -> {
                        final float height = interacting.contains(setting) ? (Math.round(roboto17.getHeight() + 1f) + 4) * 5 : Math.round(roboto17.getHeight() + 1f);

                        if(screenContext.event() == ScreenContext.Event.ON_CLICK) {
                            if(Boundings.isInBounds(screenContext.mouseX(), screenContext.mouseY(), windowX, settingY, windowWidth, height)) {
                                lastInteractedWith = setting;
                            }
                        }

                        settingY += height;
                    }
                    case "Slider" -> {
                        final float height = Math.round(roboto17.getHeight() + 1f) + 4;

                        if(screenContext.event() == ScreenContext.Event.ON_CLICK) {
                            if(Boundings.isInBounds(screenContext.mouseX(), screenContext.mouseY(), windowX, settingY, windowWidth, height)) {
                                lastInteractedWith = setting;
                            }
                        }

                        settingY += height;
                    }
                    case "Mode" -> {
                        final SettingFeature<String> stringSetting = (SettingFeature<String>) setting;
                        final ModeBuilder modeBuilder  = (ModeBuilder) stringSetting.getBuilder();
                        final float baseHeight = Math.round(roboto17.getHeight() + 1f);
                        final boolean expanded = interacting.contains(setting);

                        final float height = baseHeight + (expanded ? modeBuilder.modes.length * baseHeight : 0);

                        if(screenContext.event() == ScreenContext.Event.ON_CLICK) {
                            if(Boundings.isInBounds(screenContext.mouseX(), screenContext.mouseY(), windowX, settingY, windowWidth, height)) {
                                lastInteractedWith = setting;
                            }
                        }

                        settingY += height;
                    }
                    case "Font" -> {
                        final SettingFeature<FontRendererValue> fontSetting = (SettingFeature<FontRendererValue>) setting;

                        /*Family*/ {
                            final float baseHeight = Math.round(roboto17.getHeight() + 1f);
                            final boolean expanded = interacting.contains(setting);

                            final float height = baseHeight + (expanded ? fontManager.available.keySet().size() * baseHeight : 0);

                            if(screenContext.event() == ScreenContext.Event.ON_CLICK) {
                                if(Boundings.isInBounds(screenContext.mouseX(), screenContext.mouseY(), windowX, settingY, windowWidth, height)) {
                                    lastInteractedWith = setting;
                                }
                            }

                            settingY += height;
                        }

                        /*Type*/ {
                            final float baseHeight = Math.round(roboto17.getHeight() + 1f);
                            final boolean expanded = interacting.contains(setting);

                            final float height = baseHeight + (expanded ? fontManager.available.get(fontSetting.getValue().fontType()).size() * baseHeight : 0);

                            if(screenContext.event() == ScreenContext.Event.ON_CLICK) {
                                if(Boundings.isInBounds(screenContext.mouseX(), screenContext.mouseY(), windowX, settingY, windowWidth, height)) {
                                    lastInteractedWith = setting;
                                }
                            }

                            settingY += height;
                        }

                        /*Size*/ {
                            final float height = Math.round(roboto17.getHeight() + 1f) + 4;

                            if(screenContext.event() == ScreenContext.Event.ON_CLICK) {
                                if(Boundings.isInBounds(screenContext.mouseX(), screenContext.mouseY(), windowX, settingY, windowWidth, height)) {
                                    lastInteractedWith = setting;
                                }
                            }

                            settingY += height;
                        }
                    }
                }
            }
        }

        final float finalWindowHeight = windowHeight,
                finalWindowWidth = windowWidth;

        DrawUtil.drawRectRelative(windowX, windowY, finalWindowWidth, finalWindowHeight, new Color(0, 0, 0, shaders ? 255 : 100).getRGB());

        float elementY = windowY + 1.5f;

        settings: for(SettingFeature<?> setting : settings) {
            if(!setting.isVisible()) {
                continue;
            }

            switch (setting.type) {
                case "Text" -> {
                    final float baseHeight = Math.round(roboto17.getHeight() + 1f);

                    final SettingFeature<String> textSetting = (SettingFeature<String>) setting;

                    switch (screenContext.event()) {
                        case ON_DRAW ->  {
                            roboto17.drawString(STR."\{textSetting.name}: \{textSetting.getValue()}\{interacting.contains(setting) ? (System.currentTimeMillis() % 1000 < 500 ? "_" : "") : ""}", windowX + 2.5f, elementY, -1);
                        }
                        case ON_CLICK -> {
                            if(Boundings.isInBounds(screenContext.mouseX(), screenContext.mouseY(), windowX, elementY, windowWidth, baseHeight)) {
                                if(interacting.contains(setting))
                                    interacting.remove(setting);
                                else
                                    interacting.add(setting);
                            }
                        }
                        case ON_KEY -> {
                            if (interacting.contains(setting)) {
                                if(screenContext.key() == Keyboard.KEY_ESCAPE || screenContext.key() == Keyboard.KEY_RETURN) {
                                    interacting.remove(setting);
                                    break settings;
                                } else if (screenContext.key() == Keyboard.KEY_BACK) {
                                    if (!textSetting.getValue().isEmpty()) {
                                        textSetting.setValue(textSetting.getValue().substring(0, textSetting.getValue().length() - 1));
                                    }
                                } else if (ChatAllowedCharacters.isAllowedCharacter(screenContext.character())) {
                                    textSetting.setValue(textSetting.getValue() + screenContext.character());
                                }
                            }
                        }
                    }

                    elementY += baseHeight;
                }
                case "Color" -> {
                    final float baseHeight = Math.round(roboto17.getHeight() + 1f);

                    final SettingFeature<Integer> colorSetting = (SettingFeature<Integer>) setting;

                    roboto17.drawString(colorSetting.name, windowX + 2.5f, elementY, -1);
                    DrawUtil.drawRectRelative(windowX + windowWidth - 10 - 2.5f, elementY + (baseHeight - 10) / 2f, 10, 10, colorSetting.getValue());

                    if(screenContext.event() == ScreenContext.Event.ON_CLICK && Boundings.isInBounds(screenContext.mouseX(), screenContext.mouseY(), windowX, elementY, windowWidth, baseHeight)) {
                        if(interacting.contains(setting)) {
                            interacting.remove(setting);
                        } else {
                            interacting.add(setting);
                        }
                    }

                    elementY += baseHeight;

                    if(interacting.contains(setting)) {
                        final AtomicReference<Float> red = new AtomicReference<>((float) (colorSetting.getValue() >> 16 & 0xFF));
                        elementY += slider(screenContext, windowWidth, windowHeight, elementY, STR."\{colorSetting.name} red", 0, 255, 1, red);

                        final AtomicReference<Float> green = new AtomicReference<>((float) (colorSetting.getValue() >> 8 & 0xFF));
                        elementY += slider(screenContext, windowWidth, windowHeight, elementY, STR."\{colorSetting.name} green", 0, 255, 1, green);

                        final AtomicReference<Float> blue = new AtomicReference<>((float) (colorSetting.getValue() & 0xFF));
                        elementY += slider(screenContext, windowWidth, windowHeight, elementY, STR."\{colorSetting.name} blue", 0, 255, 1, blue);

                        final AtomicReference<Float> alpha = new AtomicReference<>((float) (colorSetting.getValue() >> 24 & 0xFF));
                        elementY += slider(screenContext, windowWidth, windowHeight, elementY, STR."\{colorSetting.name} alpha", 0, 255, 1, alpha);

                        colorSetting.setValue(new Color(red.get().intValue(), green.get().intValue(), blue.get().intValue(), alpha.get().intValue()).getRGB());
                    }
                }
                case "Checkbox" -> {
                    final SettingFeature<Boolean> booleanSetting = (SettingFeature<Boolean>) setting;
                    final AtomicBoolean value = new AtomicBoolean(booleanSetting.getValue());

                    elementY += check(screenContext, windowWidth, windowHeight, elementY, setting.getName(), value);

                    booleanSetting.setValue(value.get());
                }

                case "Slider" -> {
                    final SettingFeature<Float> numberSetting = (SettingFeature<Float>) setting;
                    final SliderBuilder sliderBuilder = (SliderBuilder) numberSetting.getBuilder();
                    final AtomicReference<Float> value = new AtomicReference<>(numberSetting.getValue());

                    elementY += slider(screenContext, windowWidth, windowHeight, elementY, setting.getName(), sliderBuilder.minimum, sliderBuilder.maximum, sliderBuilder.decimals, value);

                    numberSetting.setValue(value.get());
                }

                case "Mode" -> {
                    final SettingFeature<String> stringSetting = (SettingFeature<String>) setting;
                    final ModeBuilder modeBuilder  = (ModeBuilder) stringSetting.getBuilder();
                    final AtomicReference<String> value = new AtomicReference<>(stringSetting.getValue());

                    elementY += mode(screenContext, windowWidth, windowHeight, elementY, setting, setting.getName(), modeBuilder.modes, value);

                    stringSetting.setValue(value.get());
                }

                case "Font" -> {
                    final SettingFeature<FontRendererValue> fontSetting = (SettingFeature<FontRendererValue>) setting;

                    String family = fontSetting.getValue().family(),
                            type = fontSetting.getValue().fontType();
                    float size;

                    final HashMap<String, List<String>> available = fontManager.available;

                    /*Family*/ {
                        final AtomicReference<String> value = new AtomicReference<>(family);

                        final String[] familyArray = new String[available.entrySet().size()];
                        int familyIndex = 0;
                        for (String element : available.keySet()) {
                            familyArray[familyIndex++] = element;
                        }

                        elementY += mode(screenContext, windowWidth, windowHeight, elementY, STR."\{fontSetting.getName()} family", STR."\{fontSetting.getName()} family", familyArray, value);

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

                        elementY += mode(screenContext, windowWidth, windowHeight, elementY, STR."\{fontSetting.getName()} type", STR."\{fontSetting.getName()} type", typeArray, value);

                        type = value.get();
                    }

                    /*Size*/ {
                        final AtomicReference<Float> value = new AtomicReference<>((float) Math.round(fontSetting.getValue().size()));

                        elementY += slider(screenContext, windowWidth, windowHeight, elementY, STR."\{fontSetting.getName()} size", 0, 50, 1, value);

                        size = Math.round(value.get());
                    }

                    if(!family.equals(fontSetting.getValue().family()) || !type.equals(fontSetting.getValue().fontType()) || size != fontSetting.getValue().size())
                        fontSetting.setValue(new FontRendererValue(family, type, size, FontManager.getSingleton().get(family, type, size)));
                }
            }
        }

        if(screenContext.event() == ScreenContext.Event.ON_DRAW)
            GLUtil.scaleEnd();

        if(shaders)
            return;

        if(screenContext.event() == ScreenContext.Event.ON_KEY && lastInteractedWith != null) {
            if(lastInteractedWith.type.equalsIgnoreCase("Slider")) {
                if(screenContext.key() == Keyboard.KEY_UP || screenContext.key() == Keyboard.KEY_DOWN || screenContext.key() == Keyboard.KEY_LEFT || screenContext.key() == Keyboard.KEY_RIGHT) {
                    final SettingFeature<Float> sliderSetting = (SettingFeature<Float>) lastInteractedWith;
                    final SliderBuilder sliderBuilder = (SliderBuilder) lastInteractedWith.getBuilder();

                    if(screenContext.key() == Keyboard.KEY_UP || screenContext.key() == Keyboard.KEY_RIGHT)
                        sliderSetting.setValue((float) (sliderSetting.getValue() + (1 / Math.pow(10, sliderBuilder.decimals))));
                    else
                        sliderSetting.setValue((float) (sliderSetting.getValue() - (1 / Math.pow(10, sliderBuilder.decimals))));
                }
            }
        }
    }

    private float check(ScreenContext screenContext, float windowWidth, float windowHeight, float elementY, String name, AtomicBoolean value) {
        final float height = Math.round(roboto17.getHeight() + 1f);

        return switch (screenContext.event()) {
            case ON_DRAW -> {
                roboto17.drawString(name, windowX + 2.5f, elementY, -1);
                DrawUtil.drawRectRelative(windowX + windowWidth - 10 - 2.5f, elementY + (height - 10) / 2f, 10, 10,
                        new Color(value.get() ? clickGuiModule.getEnabledRed() : 0, value.get() ? clickGuiModule.getEnabledGreen() : 0, value.get() ? clickGuiModule.getEnabledBlue() : 0, 100).getRGB());

                yield height;
            }
            case ON_CLICK -> {
                if(Boundings.isInBounds(screenContext.mouseX(), screenContext.mouseY(), windowX + windowWidth - 10 - 2.5f, elementY + (height - 10) / 2f, 10, 10)) {
                    value.set(!value.get());
                }

                yield height;
            }
            default -> height;
        };
    }

    private float slider(ScreenContext screenContext, float windowWidth, float windowHeight, float elementY, String name, float min, float max, int decimals, AtomicReference<Float> value) {
        final float startingY = elementY;
        final float height = Math.round(roboto17.getHeight() + 1f);
        final float addHeight = height + 4;

        return switch (screenContext.event()) {
            case ON_DRAW -> {
                roboto17.drawString(STR."\{name}: \{String.format(STR."%.\{decimals}f", value.get())}", windowX + 2.5f, elementY, -1);

                elementY += height;

                final float sliderWidth = windowWidth - 2f;
                final float selectedSliderLength = MathHelper.floor_double((value.get() - min) / (max - min) * sliderWidth);

                if (Mouse.isButtonDown(0) && Boundings.isInBounds(screenContext.mouseX(), screenContext.mouseY(),windowX, startingY, windowWidth, addHeight)) {
                    float percentage = screenContext.mouseX() - windowX - 1f;
                    percentage /= sliderWidth;
                    percentage = Math.max(Math.min(percentage, 1), 0);

                    value.set((min + (max - min) * percentage));
                    value.set(MathUtil.roundAvoid(value.get(), decimals));
                }

                DrawUtil.drawRectRelative(windowX + 1f, elementY + (4 - 3) / 2f - 0.5f, selectedSliderLength, 3, new Color(clickGuiModule.getEnabledRed(), clickGuiModule.getEnabledGreen(), clickGuiModule.getEnabledBlue(), clickGuiModule.getEnabledAlpha()).getRGB());
                DrawUtil.drawRectRelative(windowX + 1f + selectedSliderLength, elementY + (4 - 3) / 2f - 0.5f, sliderWidth - selectedSliderLength, 3, new Color(0, 0, 0, 100).getRGB());

                yield addHeight;
            }
            default -> addHeight;
        };
    }

    private float mode(ScreenContext screenContext, float windowWidth, float windowHeight, float elementY, Object hook, String name, String[] modes, AtomicReference<String> value) {
        final float height = Math.round(roboto17.getHeight() + 1f);
        final boolean expanded = interacting.contains(hook);

        final float addHeight = expanded ? modes.length * height : 0;

        return switch (screenContext.event()) {
            case ON_DRAW -> {
                roboto17.drawString(name, windowX + 2.5f, elementY, -1);
                roboto17.drawString(value.get(), windowX + windowWidth - roboto17.getStringWidth(value.get()) - 2.5f, elementY, -1);

                if(expanded) {
                    for (String mode : modes) {
                        elementY += Math.round(roboto17.getHeight() + 1f);

                        roboto17.drawString(mode, windowX + 5f, elementY, value.get().equals(mode) ? new Color(clickGuiModule.getEnabledRed(), clickGuiModule.getEnabledGreen(), clickGuiModule.getEnabledBlue(), 255).getRGB() : -1);
                    }
                }

                yield height + addHeight;
            }
            case ON_CLICK -> {
                if(Boundings.isInBounds(screenContext.mouseX(), screenContext.mouseY(), windowX, elementY, windowWidth, Math.round(roboto17.getHeight() + 1f))) {
                    if(interacting.contains(hook))
                        interacting.remove(hook);
                    else
                        interacting.add(hook);
                }

                if(expanded) {
                    for (String mode : modes) {
                        elementY += Math.round(roboto17.getHeight() + 1f);

                        if(Boundings.isInBounds(screenContext.mouseX(), screenContext.mouseY(), windowX, elementY, windowWidth, Math.round(roboto17.getHeight() + 1f))) {
                            value.set(mode);
                            interacting.remove(hook);
                        }
                    }
                }

                yield height + addHeight;
            }
            default -> addHeight;
        };
    }



    public boolean closed() {
        return interpolated && openingAnimation.finished(Direction.BACKWARDS);
    }

    public void close() {
        openingAnimation.setDirection(Direction.BACKWARDS);
    }

}
