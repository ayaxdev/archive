package com.daniel.datsuzei.screen.clickgui.frame;

import com.daniel.datsuzei.font.ClientFontRenderer;
import com.daniel.datsuzei.font.FontManager;
import com.daniel.datsuzei.module.ModuleCategory;
import com.daniel.datsuzei.module.ModuleFeature;
import com.daniel.datsuzei.module.ModuleManager;
import com.daniel.datsuzei.module.impl.ModuleScreenModule;
import com.daniel.datsuzei.screen.clickgui.ClickGui;
import com.daniel.datsuzei.settings.SettingFeature;
import com.daniel.datsuzei.settings.SettingManager;
import com.daniel.datsuzei.settings.impl.BooleanSetting;
import com.daniel.datsuzei.settings.impl.ModeSetting;
import com.daniel.datsuzei.settings.impl.NumberSetting;
import com.daniel.datsuzei.util.render.DrawUtil;
import de.florianmichael.rclasses.math.integration.Boundings;
import net.minecraft.util.MathHelper;
import org.lwjglx.input.Mouse;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public class Frame {

    private final ModuleScreenModule clickGuiModule;
    private final ModuleCategory category;
    private final float width, height;
    private float x, y;

    private ModuleFeature selectedModule;

    private boolean expanded;

    private boolean dragging;
    private float draggingX, draggingY;

    private final ArrayList<ModuleFeature> modules = new ArrayList<>();
    private final ArrayList<SettingFeature<?>> expandedSettings = new ArrayList<>();

    public Frame(ClickGui parent, ModuleCategory category, float x, float y, float width, float height) {
        this.clickGuiModule = parent.moduleScreenModule;
        this.category = category;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        modules.addAll(ModuleManager.getSingleton().getByCategory(category));
    }

    public void draw(int mouseX, int mouseY) {
        if(dragging) {
            x = mouseX - draggingX;
            y = mouseY - draggingY;
        }

        final ClientFontRenderer nameFont = FontManager.getSingleton().get("Arial", 20);

        final boolean inCategoryBounds = Boundings.isInBounds(mouseX, mouseY, x, y, width, height);

        DrawUtil.drawRectRelative(x, y, width, height, getColor(false, false, inCategoryBounds).getRGB());
        DrawUtil.drawBorderRelative(x, y, width, height, 1, Color.BLACK.getRGB(), true);

        if(selectedModule != null) {
            final float buttonX = x + width - height + 2,
                    buttonY = y + 2,
                    buttonSize = height - 4;
            final boolean inButtonBounds = Boundings.isInBounds(mouseX, mouseY, buttonX, buttonY, buttonSize, buttonSize);

            DrawUtil.drawRectRelative(buttonX, buttonY, buttonSize, buttonSize, getColor(true, false, inButtonBounds).getRGB());
            DrawUtil.drawBorderRelative(buttonX, buttonY, buttonSize, buttonSize, 1, Color.BLACK.getRGB(), true);

            nameFont.drawTotalCenteredStringWithShadow("<", buttonX + buttonSize / 2, buttonY + buttonSize / 2, -1);
        }

        nameFont.drawTotalCenteredStringWithShadow(selectedModule == null ? category.getName() : selectedModule.getName(), x + width / 2, y + height / 2, -1);

        if(expanded) {
            if(!modules.isEmpty()) {
                float predictedY = 0;

                if(selectedModule == null) {
                    predictedY = modules.size() * (height - 2) + 2;
                } else {
                    final Collection<SettingFeature<?>> settings = SettingManager.getSingleton().getByOwner(selectedModule);

                    for(SettingFeature<?> settingFeature : settings) {
                        predictedY += height - 2;

                        if(settingFeature instanceof ModeSetting modeSetting && expandedSettings.contains(modeSetting)) {
                            predictedY += modeSetting.getModes().size() * (height - 4);
                        }
                    }

                    predictedY += 2;
                }

                float featureY = y + height + 2;
                final float initialY = featureY;

                DrawUtil.drawRectRelative(x, initialY, width, predictedY, getColor(false, false, false).getRGB());

                featureY += 2;

                final float featureRectangleX = x + 2, featureRectangleWidth = width - 4, featureRectangleHeight = height - 4;

                if(selectedModule != null) {
                    final Collection<SettingFeature<?>> settings = SettingManager.getSingleton().getByOwner(selectedModule);

                    for(SettingFeature<?> settingFeature : settings) {
                        final boolean settingHovered = Boundings.isInBounds(mouseX, mouseY, featureRectangleX, featureY, featureRectangleWidth, featureRectangleHeight);

                        if(settingFeature instanceof BooleanSetting booleanSetting) {
                            DrawUtil.drawRectRelative(featureRectangleX, featureY, featureRectangleWidth, featureRectangleHeight, getColor(true, booleanSetting.getValue(), settingHovered).getRGB());
                            DrawUtil.drawBorderRelative(featureRectangleX, featureY, featureRectangleWidth, featureRectangleHeight, 1, Color.BLACK.getRGB(), true);

                            nameFont.drawTotalCenteredStringWithShadow(settingFeature.getName(), x + width / 2, featureY - 2 + height / 2, -1);

                            featureY += height - 2;
                        } else if(settingFeature instanceof ModeSetting modeSetting) {
                            final boolean expanded = expandedSettings.contains(modeSetting);
                            final float initialModeY = featureY;

                            DrawUtil.drawRectRelative(featureRectangleX, featureY, featureRectangleWidth, featureRectangleHeight, getColor(true, false, settingHovered).getRGB());

                            nameFont.drawTotalCenteredStringWithShadow(modeSetting.getName(), x + width / 2, featureY - 2 + height / 2, -1);

                            if(expanded) {
                                for(String s : modeSetting.getModes()) {
                                    final float modeY = featureY + (height - 4);
                                    final boolean modeHovered = Boundings.isInBounds(mouseX, mouseY, featureRectangleX, modeY, featureRectangleWidth, featureRectangleHeight);

                                    DrawUtil.drawRectRelative(featureRectangleX, modeY, featureRectangleWidth, featureRectangleHeight, getColor(true, false, modeHovered).getRGB());

                                    if(modeSetting.getValue().equals(s))
                                        nameFont.drawCenteredStringWithShadow(s, x + width / 2, featureY + height / 2 + nameFont.getHeight() / 2 + 2.5f, -1);
                                    else
                                        nameFont.drawStringWithShadow(s, x + 10, featureY + height / 2 + nameFont.getHeight() / 2 + 2.5f, -1);

                                    featureY += height - 4;
                                }
                            }

                            DrawUtil.drawBorderRelative(featureRectangleX, initialModeY, featureRectangleWidth, featureRectangleHeight + (expanded ? modeSetting.getModes().size() * (height - 4) : 0), 1, Color.BLACK.getRGB(), true);

                            featureY += height - 2;
                        } else if(settingFeature instanceof NumberSetting<? extends Number> numberSetting) {
                            DrawUtil.drawRectRelative(featureRectangleX, featureY, featureRectangleWidth, featureRectangleHeight, getColor(true, false, settingHovered).getRGB());

                            final float min = numberSetting.getMinimum().floatValue(),
                                    max = numberSetting.getMaximum().floatValue(),
                                    curValue = numberSetting.getValue().floatValue();

                            final float sliderWidth = MathHelper.floor_double((curValue - min) / (max - min) * featureRectangleWidth);

                            DrawUtil.drawRectRelative(featureRectangleX, featureY, sliderWidth, featureRectangleHeight, getColor(true, true, settingHovered).getRGB());

                            DrawUtil.drawBorderRelative(featureRectangleX, featureY, featureRectangleWidth, featureRectangleHeight, 1, Color.BLACK.getRGB(), true);

                            nameFont.drawTotalCenteredStringWithShadow(settingFeature.getName() + ":" + numberSetting.getValue(), x + width / 2, featureY - 2 + height / 2, -1);

                            if(Mouse.isButtonDown(0) && settingHovered) {
                                double length = featureRectangleX + featureRectangleWidth - mouseX;
                                double value = max - ((featureRectangleWidth * min) - (length * min) + (length * max)) / featureRectangleWidth + min;
                                if(numberSetting.getMinimum() instanceof Integer) {
                                    ((NumberSetting<Integer>) numberSetting).setValue((int) value);
                                } else if(numberSetting.getMinimum() instanceof Float) {
                                    ((NumberSetting<Float>) numberSetting).setValue((float) value);
                                }
                            }

                            featureY += height - 2;
                        }
                    }
                } else {
                    for(ModuleFeature module : modules) {
                        final boolean moduleHovered = Boundings.isInBounds(mouseX, mouseY, featureRectangleX, featureY, featureRectangleWidth, featureRectangleHeight);

                        DrawUtil.drawRectRelative(featureRectangleX, featureY, featureRectangleWidth, featureRectangleHeight, getColor(true, module.isEnabled(), moduleHovered).getRGB());
                        DrawUtil.drawBorderRelative(featureRectangleX, featureY, featureRectangleWidth, featureRectangleHeight, 1, Color.BLACK.getRGB(), true);

                        nameFont.drawTotalCenteredStringWithShadow(module.getName(), x + width / 2, featureY - 2 + height / 2, -1);

                        featureY += height - 2;
                    }
                }

                DrawUtil.drawBorderRelative(x, initialY, width, predictedY, 1, Color.BLACK.getRGB(), true);
            }
        }
    }

    private Color getColor(boolean secondLayer, boolean enabled, boolean hovered) {
        if(secondLayer) {
            return enabled ? new Color(this.clickGuiModule.red.getValue(), this.clickGuiModule.green.getValue(), this.clickGuiModule.blue.getValue()) : new Color(hovered ? 50 : 30, hovered ? 50 : 30, hovered ? 50 : 30, 100);
        } else {
            return enabled ? new Color(this.clickGuiModule.red.getValue(), this.clickGuiModule.green.getValue(), this.clickGuiModule.blue.getValue()) : new Color(hovered ? 100 : 80, hovered ? 100 : 80, hovered ? 100 : 80, 180);
        }
    }

    public void keyTyped(char character, int key) {

    }

    public void mouseClicked(int mouseX, int mouseY, int button) {
        final float backButtonX = x + width - height + 2,
                backButtonY = y + 2,
                backButtonSize = height - 4;
        final boolean backButtonHovered = Boundings.isInBounds(mouseX, mouseY, backButtonX, backButtonY, backButtonSize, backButtonSize);
        if(backButtonHovered && button == 0) {
            selectedModule = null;
            return;
        }

        if(Boundings.isInBounds(mouseX, mouseY, x, y, width, height)) {
            switch (button) {
                case 0 -> {
                    dragging = true;

                    draggingX = mouseX - x;
                    draggingY = mouseY - y;
                }
                case 1 -> expanded = !expanded;
            }

            return;
        }

        if(expanded && !modules.isEmpty()) {
            float featureY = y + height + 4;

            final float featureRectangleX = x + 2, featureRectangleWidth = width - 4, featureRectangleHeight = height - 4;

            if(selectedModule != null) {
                final Collection<SettingFeature<?>> settings = SettingManager.getSingleton().getByOwner(selectedModule);

                for(SettingFeature<?> settingFeature : settings) {
                    if(settingFeature instanceof BooleanSetting booleanSetting) {
                        if(Boundings.isInBounds(mouseX, mouseY, featureRectangleX, featureY, featureRectangleWidth, featureRectangleHeight) && button == 0)
                            booleanSetting.setValue(!booleanSetting.getValue());
                        featureY += height - 2;
                    } else if(settingFeature instanceof ModeSetting modeSetting) {
                        if(Boundings.isInBounds(mouseX, mouseY, featureRectangleX, featureY, featureRectangleWidth, featureRectangleHeight) && (button == 0 || button == 1))
                            if(expandedSettings.contains(modeSetting)) {
                                expandedSettings.remove(modeSetting);
                            } else {
                                expandedSettings.add(modeSetting);
                            }

                        if(expandedSettings.contains(modeSetting)) {
                            for(String s : modeSetting.getModes()) {
                                final float modeY = featureY + (height - 4);
                                final boolean modeHovered = Boundings.isInBounds(mouseX, mouseY, featureRectangleX, modeY, featureRectangleWidth, featureRectangleHeight);

                                if(modeHovered) {
                                    modeSetting.setValue(s);
                                }

                                featureY += height - 4;
                            }
                        }

                        featureY += height - 2;
                    } else if(settingFeature instanceof NumberSetting<?>) {
                        featureY += height - 2;
                    }
                }
            } else {
                for (ModuleFeature module : modules) {
                    final boolean moduleHovered = Boundings.isInBounds(mouseX, mouseY, featureRectangleX, featureY, featureRectangleWidth, featureRectangleHeight);

                    if(moduleHovered) {
                        switch (button) {
                            case 0 -> module.toggleEnabled();
                            case 1 -> selectedModule = module;
                        }

                        return;
                    }

                    featureY += height - 2;
                }
            }
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int button) {
        if(button == 0) {
            dragging = false;
        }
    }
}
