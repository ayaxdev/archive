package com.atani.nextgen.screen.simple;

import com.atani.nextgen.font.FontManager;
import com.atani.nextgen.module.ModuleFeature;
import com.atani.nextgen.setting.SettingFeature;
import com.atani.nextgen.setting.SettingManager;
import com.atani.nextgen.setting.builder.impl.ModeBuilder;
import com.atani.nextgen.setting.builder.impl.SliderBuilder;
import com.atani.nextgen.util.render.DrawUtil;
import de.florianmichael.rclasses.math.Arithmetics;
import de.florianmichael.rclasses.math.integration.Boundings;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class Window extends GuiScreen {

    public final GuiScreen previousGui;
    public final ModuleFeature moduleFeature;

    private final FontRenderer roboto19 = FontManager.getSingleton().get("Roboto", 19);
    private final FontRenderer roboto30 = FontManager.getSingleton().get("Roboto", 30);

    private final List<SettingFeature<?>> expandedSettings = new ArrayList<>();

    @Override
    @SuppressWarnings("unchecked")
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        final float windowX = this.width / 2f - 160;
        final float windowWidth = 320;
        final float windowY = this.height / 2f - 200;
        final float windowHeight = 400;

        DrawUtil.drawRectRelative(windowX, windowY, windowWidth, windowHeight, new Color(0, 0, 0, 180).getRGB());
        roboto30.drawXCenteredStringWithShadow(moduleFeature.name, windowX + windowWidth / 2, windowY + 10, -1);

        float settingY = windowY + 15 + roboto30.FONT_HEIGHT;

        for (SettingFeature<?> settingFeature : SettingManager.getSingleton().getByOwner(moduleFeature)) {
            final float height = 20;

            final float textX = windowX + 10 + roboto19.getStringWidth(settingFeature.name + ":") + 5,
                    textY = settingY + height / 2 + roboto19.FONT_HEIGHT / 2f;

            switch (settingFeature.type) {
                case "Checkbox" -> {
                    SettingFeature<Boolean> booleanSettingFeature = (SettingFeature<Boolean>) settingFeature;

                    roboto19.drawYCenteredStringWithShadow(settingFeature.name + ":", windowX + 10, settingY + height / 2, -1);
                    DrawUtil.drawRectRelative(textX, settingY + height / 2 - 14 / 2f, 14, 14, new Color(0, 0, 0, 80).getRGB());

                    if(booleanSettingFeature.getValue())
                        DrawUtil.drawCheck(textX + 3, settingY + height / 2 - 14 / 2f + 0.5f, 14, 2f, Color.WHITE);
                }

                case "Slider" -> {
                    final SliderBuilder sliderBuilder = (SliderBuilder) settingFeature.getBuilder();
                    final SettingFeature<Float> sliderSetting = (SettingFeature<Float>) settingFeature;
                    final float sliderWidth = 100;

                    final float min = sliderBuilder.minimum,
                            max = sliderBuilder.maximum,
                            curValue = (float) settingFeature.getValue();

                    final float selectedSliderLength = MathHelper.floor_double((curValue - min) / (max - min) * sliderWidth);

                    roboto19.drawYCenteredStringWithShadow(settingFeature.name + ": " + String.valueOf(settingFeature.getValue()), windowX + 10, settingY + height / 2, -1);
                    DrawUtil.drawRectRelative(windowX + 10, textY + 2, sliderWidth, 3, new Color(0, 0, 0, 80).getRGB());
                    DrawUtil.drawRectRelative(windowX + 10, textY + 2, selectedSliderLength, 3, -1);

                    if (Mouse.isButtonDown(0) && Boundings.isInBounds(mouseX, mouseY, windowX + 10, textY + 2, sliderWidth, 3)) {
                        double length = windowX + 10 + sliderWidth - mouseX;
                        double value = max - ((sliderWidth * min) - (length * min) + (length * max)) / sliderWidth + min;
                        sliderSetting.setValue((float) Arithmetics.roundAvoid(value, sliderBuilder.decimals));
                    }

                    settingY += 3;
                }

                case "Mode" -> {
                    final ModeBuilder modeBuilder = (ModeBuilder) settingFeature.getBuilder();
                    final boolean expanded = expandedSettings.contains(settingFeature);

                    String longestString = "";

                    for (String s : modeBuilder.modes) {
                        if (roboto19.getStringWidth(s) > roboto19.getStringWidth(longestString))
                            longestString = s;
                    }

                    final String renderString = expanded ? longestString : (String) settingFeature.getValue();

                    roboto19.drawYCenteredStringWithShadow(settingFeature.name + ":", windowX + 10, settingY + height / 2, -1);
                    DrawUtil.drawRectRelative(textX, settingY + height / 2 - 14 / 2f, roboto19.getStringWidth(renderString) + 4, 14, new Color(0, 0, 0, 80).getRGB());
                    roboto19.drawXYCenteredStringWithShadow((String) settingFeature.getValue(), textX + (roboto19.getStringWidth((String) settingFeature.getValue()) + 4) / 2f, settingY + height / 2, -1);

                    final float initialModeY = settingY + height / 2 + 14 / 2f;
                    float modeY = initialModeY;

                    if (expanded) {
                        for (String s : modeBuilder.modes) {
                            DrawUtil.drawRectRelative(textX, modeY, roboto19.getStringWidth((String) settingFeature.getValue()) + 4, 14, new Color(0, 0, 0, 80).getRGB());
                            roboto19.drawXYCenteredStringWithShadow(s, textX + (roboto19.getStringWidth((String) settingFeature.getValue()) + 4) / 2f, modeY + 14 / 2f, -1);
                            modeY += 14;
                        }
                    }

                    settingY += modeY - initialModeY;
                }
            }

            settingY += height;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1)
            mc.displayGuiScreen(previousGui);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        final float windowX = this.width / 2f - 160;
        final float windowWidth = 320;
        final float windowY = this.height / 2f - 200;
        final float windowHeight = 400;

        float settingY = windowY + 15 + roboto30.FONT_HEIGHT;

        for (SettingFeature<?> settingFeature : SettingManager.getSingleton().getByOwner(moduleFeature)) {
            final float height = 20;

            final float textX = windowX + 10 + roboto19.getStringWidth(settingFeature.name + ":") + 5,
                    textY = settingY + height / 2 + roboto19.FONT_HEIGHT / 2f;

            switch (settingFeature.type) {
                case "Checkbox" -> {
                    if (Boundings.isInBounds(mouseX, mouseY, textX, settingY + height / 2 - 14 / 2f, 14, 14))
                        ((SettingFeature<Boolean>) settingFeature).setValue(!((Boolean) settingFeature.getValue()));
                }

                case "Slider" -> {
                    settingY += 3;
                }

                case "Mode" -> {
                    final ModeBuilder modeBuilder = (ModeBuilder) settingFeature.getBuilder();
                    final boolean expanded = expandedSettings.contains(settingFeature);

                    String longestString = "";

                    for (String s : modeBuilder.modes) {
                        if (roboto19.getStringWidth(s) > roboto19.getStringWidth(longestString))
                            longestString = s;
                    }

                    final String renderString = expanded ? longestString : (String) settingFeature.getValue();

                    if (Boundings.isInBounds(mouseX, mouseY, textX, settingY + height / 2 - 14 / 2f, roboto19.getStringWidth(renderString) + 4, 14)) {
                        if (expandedSettings.contains(settingFeature))
                            expandedSettings.remove(settingFeature);
                        else
                            expandedSettings.add(settingFeature);
                    }

                    final float initialModeY = settingY + height / 2 + 14 / 2f;
                    float modeY = initialModeY;

                    if (expanded) {
                        for (String s : modeBuilder.modes) {
                            if (Boundings.isInBounds(mouseX, mouseY, textX, modeY, roboto19.getStringWidth((String) settingFeature.getValue()) + 4, 14))
                                ((SettingFeature<String>) settingFeature).setValue(s);

                            modeY += 14;
                        }
                    }

                    settingY += modeY - initialModeY;

                }
            }

            settingY += height;
        }
    }
}
