package lord.daniel.alexander.ui.clickgui;

import lord.daniel.alexander.Modification;
import lord.daniel.alexander.handler.render.ShaderHandler;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.EnumModuleType;
import lord.daniel.alexander.module.impl.design.ClickGuiModule;
import lord.daniel.alexander.settings.*;
import lord.daniel.alexander.settings.impl.bool.*;
import lord.daniel.alexander.settings.impl.mode.MultiSelectValue;
import lord.daniel.alexander.settings.impl.mode.StringModeValue;
import lord.daniel.alexander.settings.impl.number.KeyBindValue;
import lord.daniel.alexander.settings.impl.number.NumberValue;
import lord.daniel.alexander.settings.impl.number.color.ColorValue;
import lord.daniel.alexander.settings.impl.string.StringValue;
import lord.daniel.alexander.storage.impl.FontStorage;
import lord.daniel.alexander.storage.impl.ModuleStorage;
import lord.daniel.alexander.util.animation.MutableAnimation;
import lord.daniel.alexander.util.array.ArrayUtils;
import lord.daniel.alexander.util.math.MathUtil;
import lord.daniel.alexander.util.render.RenderUtil;
import lord.daniel.alexander.util.render.font.CFont;
import lord.daniel.alexander.util.render.font.CFontRenderer;
import lord.daniel.alexander.util.render.gl11.GLUtil;
import lord.daniel.alexander.util.render.rounded.RoundedUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;
import org.lwjgl.openal.EXTEfx;
import org.lwjglx.Sys;
import org.lwjglx.input.Keyboard;
import org.lwjglx.input.Mouse;

import java.awt.*;
import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

/**
 * Written by Daniel. on 25/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
public class ClickGuiScreen extends GuiScreen {

    // Position and size
    private float posX = 50, posY = 50,
        width = -1, height = 300;

    private final float moduleListWidth = 145, categoryBarHeight = 35;

    // Fonts
    private CFont categoryCFont;
    private CFont moduleCFont;
    private CFont sliderCFont;
    private CFont logoCFont;

    // Selected
    private EnumModuleType selectedCategory;
    private AbstractModule selectedModule;
    private final List<KeyBindValue> bindingKeys = new ArrayList<>();
    private final List<StringValue> typingValues = new ArrayList<>();

    // Expanded
    private final ArrayList<StringModeValue> expandedModeValues = new ArrayList<>();
    private final ArrayList<MultiSelectValue> expandedMultiValues = new ArrayList<>();

    // Scrolling
    private float scroll = 0f;
    private MutableAnimation scrollAnimation = new MutableAnimation(0, 1);

    // Expanding
    public boolean expanding;

    // Dragging
    private float draggingX, draggingY;
    private boolean dragging;

    // ClickGui
    private ClickGuiModule clickGuiModule;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Getting click gui module instance
        if(clickGuiModule == null)
            clickGuiModule = ModuleStorage.getModuleStorage().getByClass(ClickGuiModule.class);

        // Fonts
        if(categoryCFont == null || logoCFont == null || moduleCFont == null) {
            categoryCFont = FontStorage.getFontStorage().get("Rubik", "Regular", 20);
            moduleCFont = FontStorage.getFontStorage().get("Rubik", "Regular", 19);
            sliderCFont = FontStorage.getFontStorage().get("Rubik", "Regular", 16);
            logoCFont = FontStorage.getFontStorage().get("Rubik", "Regular", 23);
        }

        final CFontRenderer categoryFont = categoryCFont.getFontRenderer();
        final CFontRenderer moduleFont = moduleCFont.getFontRenderer();
        final CFontRenderer sliderFont = sliderCFont.getFontRenderer();
        final CFontRenderer logoFont = logoCFont.getFontRenderer();

        // Initializing Position and Size
        if(this.posX != -1 && this.posY != -1 && this.width != -1 && this.height != -1) {
            clickGuiModule.x.setValue(this.posX);
            clickGuiModule.y.setValue(this.posY);
            clickGuiModule.width.setValue(this.width);
            clickGuiModule.height.setValue(this.height);
        }

        this.posX = clickGuiModule.x.getValue();
        this.posY = clickGuiModule.y.getValue();
        this.width = clickGuiModule.width.getValue();
        this.height = clickGuiModule.height.getValue();

        // Dragging
        if(!Mouse.isButtonDown(0)) {
            dragging = false;
            expanding = false;
        }

        if(dragging) {
            posX = mouseX - draggingX;
            posY = mouseY - draggingY;
        }

        // Expanding
        if(expanding) {
            float categoryWidth = 20;

            for(EnumModuleType enumModuleType : EnumModuleType.values()) {
                categoryWidth += categoryFont.getStringWidth(enumModuleType.getName()) + 20;
            }

            float deltaY = mouseY - (posY + height);
            height = Math.max(200, height + deltaY);
            float deltaX = mouseX - (posX + width + moduleListWidth);
            width = Math.max(categoryWidth, width + deltaX);
        }

        // Scrolling
        long dWheel = Mouse.getDWheel();
        scroll += dWheel / 10f;
        scroll = Math.min(scroll, 0);
        scrollAnimation.interpolate(scroll);

        // Background and logo
        ShaderHandler.renderAndRunRunnable(List.of(() -> {
            RenderUtil.drawRect(posX, posY, moduleListWidth, height, new Color(0, 0, 0, 180).getRGB());
            RenderUtil.drawRect(posX + moduleListWidth, posY, width, categoryBarHeight, new Color(0, 0, 0, 200).getRGB());
            RenderUtil.drawRect(posX + moduleListWidth, posY + categoryBarHeight, width, height - categoryBarHeight, new Color(0, 0, 0, 155).getRGB());
        }));

        logoFont.drawCenteredString(Modification.NAME, posX + moduleListWidth / 2f, posY + categoryBarHeight / 2f - logoFont.getHeight() / 2f, -1);

        // Category bar
        float categoryX = posX + moduleListWidth + 20;

        for(EnumModuleType enumModuleType : EnumModuleType.values()) {
            float categoryY = posY + categoryBarHeight / 2f - (categoryFont.getHeight() + (selectedCategory == enumModuleType ? 1.5f : 0)) / 2f;

            categoryFont.drawString(enumModuleType.getName(), categoryX, categoryY, -1);
            if(selectedCategory == enumModuleType) {
                RenderUtil.drawRect(categoryX, categoryY + categoryFont.getHeight() + 0.5f, categoryFont.getStringWidth(enumModuleType.getName()), 1, -1);
            }

            categoryX += categoryFont.getStringWidth(enumModuleType.getName()) + 20;
        }

        if(selectedCategory != null) {
            // Module List
            float moduleY = posY + 36;

            for(AbstractModule module : ModuleStorage.getModuleStorage().getByCategory(selectedCategory)) {
                moduleFont.drawString(module.getDisplayName(), posX + 10, moduleY, module.isEnabled() ? -1 : new Color(160, 160, 160).getRGB());
                moduleY += moduleFont.getHeight() + 10;
            }

            // Settings
            if(selectedModule != null) {
                GLUtil.startScissorBox();
                GLUtil.drawScissorBox(posX + moduleListWidth, posY + categoryBarHeight + 0.5f, width, height - categoryBarHeight);

                float baseX = posX + moduleListWidth + 12;

                float valueX = baseX;
                float valueY = (float) (posY + categoryBarHeight + 10 + MathUtil.roundToIncrement(scrollAnimation.getValueF(), 0.5f));

                settingLoop: for(AbstractSetting<?> setting : selectedModule.getSettings()) {
                    if(!setting.isVisible())
                        continue;

                    if(valueX != baseX)
                        valueX = baseX;

                    for(ExpandableValue expandableValue : setting.getExpandableParents()) {
                        if(!expandableValue.getValue())
                            continue settingLoop;
                        valueX += 10;
                    }

                    if(setting instanceof ExpandableValue expandableValue) {
                        moduleFont.drawString(setting.getName(), valueX, valueY, -1);

                        RenderUtil.drawClickGuiArrow(valueX + moduleFont.getStringWidth(setting.getName()) + 7, valueY + 3.5f, 9, expandableValue.getValue() ? 1 : 0, -1);

                        valueY += moduleFont.getHeight() + 10;
                    } else if(setting instanceof StringValue stringValue) {
                        String value = stringValue.getValue();
                        if(typingValues.contains(stringValue))
                            value += System.currentTimeMillis() % 1000 > 500 ? "|" : " ";

                        moduleFont.drawString(setting.getName(), valueX, valueY, -1);

                        float rectWidth = 6 + moduleFont.getStringWidth(value);
                        RoundedUtil.drawRoundedRectangle(valueX + moduleFont.getStringWidth(setting.getName()) + 7, valueY + (moduleFont.getHeight() + 10) / 2f - 11f, rectWidth, 12, 3, new Color(0, 0, 0, 100));

                        moduleFont.drawString(value, valueX + moduleFont.getStringWidth(setting.getName()) + 7 + 2.5f, valueY + 0.5f, -1);

                        valueY += (moduleFont.getHeight() + 10);
                    } else if(setting instanceof KeyBindValue keyBindValue) {
                        final String value = bindingKeys.contains(keyBindValue) ? "..." : Keyboard.getKeyName(keyBindValue.getValue());

                        moduleFont.drawString(setting.getName(), valueX, valueY, -1);

                        float rectWidth = 5.5f + moduleFont.getStringWidth(value);
                        RoundedUtil.drawRoundedRectangle(valueX + moduleFont.getStringWidth(setting.getName()) + 7, valueY + (moduleFont.getHeight() + 10) / 2f - 11f, rectWidth, 12, 3, new Color(0, 0, 0, 100));

                        moduleFont.drawString(value, valueX + moduleFont.getStringWidth(setting.getName()) + 7 + 2.5f, valueY + 0.5f, -1);

                        valueY += (moduleFont.getHeight() + 10);
                    } else if(setting instanceof ColorValue colorValue) {
                        moduleFont.drawString(setting.getName(), valueX, valueY, -1);

                        valueY += moduleFont.getHeight() + 2;

                        colorValue.getColorPicker().draw(valueX, valueY, 100, 100, mouseX, mouseY, colorValue.getValue(), moduleCFont);

                        valueY += 110;
                    } else if(setting instanceof BooleanValue booleanValue) {
                        moduleFont.drawString(setting.getName(), valueX, valueY, -1);
                        RoundedUtil.drawRoundedRectangle(valueX + moduleFont.getStringWidth(setting.getName()) + 7, valueY + (moduleFont.getHeight() + 10) / 2f - 11f, 12, 12, 3, new Color(0, 0, 0, 100));

                        if(booleanValue.getValue())
                            RenderUtil.drawCheckMark(valueX + moduleFont.getStringWidth(setting.getName()) + 13, valueY - 1.5f, 10, -1);

                        valueY += moduleFont.getHeight() + 10;
                    } else if(setting instanceof NumberValue<?> numberValue) {
                        moduleFont.drawString(setting.getName(), valueX, valueY, -1);

                        valueY += moduleFont.getHeight() + 1;

                        final float min = numberValue.getMin().floatValue(),
                                max = numberValue.getMax().floatValue(),
                                curValue = numberValue.getValue().floatValue();

                        final int decimalPlaces = numberValue.getDecimalPlaces();

                        float sliderX = valueX + 5, sliderY = valueY, sliderWidth = 120, sliderHeight = 4,
                                length = MathHelper.floor_double((curValue - min) / (max - min) * sliderWidth);

                        RoundedUtil.drawRoundedRectangle(valueX + length, valueY, sliderWidth - length, 4, 2, new Color(0, 0, 0, 50));
                        RoundedUtil.drawRoundedRectangle(valueX, valueY, length, 4, 2, new Color(0, 0, 0, 100));

                        sliderFont.drawString(String.valueOf(curValue), valueX + sliderWidth + 5, sliderY - 2, new Color(170, 170, 170).getRGB());

                        if(Mouse.isButtonDown(0) && MathUtil.contains(mouseX, mouseY, sliderX, sliderY, sliderWidth, sliderHeight)) {
                            double newValue = MathUtil.round((mouseX - sliderX) * (max - min) / (sliderWidth - 1.0f) + min, decimalPlaces);
                            ((AbstractSetting<Number>) numberValue).setValue(newValue);
                        }

                        valueY += 9;
                    } else if(setting instanceof StringModeValue stringModeValue) {
                        final boolean expanded = expandedModeValues.contains(stringModeValue);

                        float expandedLength = 0;
                        for (String mode : stringModeValue.getModes()) {
                            expandedLength = Math.max(expandedLength, moduleFont.getStringWidth(mode));
                        }

                        moduleFont.drawString(setting.getName(), valueX, valueY, -1);

                        float rectWidth = 5.5f + (expanded ? expandedLength : moduleFont.getStringWidth(stringModeValue.getValueAsString()));
                        RoundedUtil.drawRoundedRectangle(valueX + moduleFont.getStringWidth(setting.getName()) + 7, valueY + (moduleFont.getHeight() + 10) / 2f - 11f, rectWidth, expanded ? 12 * stringModeValue.getModes().size() : 12, 3, new Color(0, 0, 0, 100));

                        moduleFont.drawString(stringModeValue.getValueAsString(), valueX + moduleFont.getStringWidth(setting.getName()) + 7 + 2.5f, valueY + 0.5f, -1);

                        if (expanded) {
                            float modeY = valueY + (moduleFont.getHeight() + 10) / 2f + 1f;
                            for (String mode : stringModeValue.getModes()) {
                                if (stringModeValue.getValue().equals(mode)) {
                                    continue;
                                }

                                moduleFont.drawString(mode, valueX + moduleFont.getStringWidth(setting.getName()) + 7 + 2.5f, modeY + 0.5f, -1);
                                modeY += 12;
                            }
                        }

                        valueY += (moduleFont.getHeight() + 10) - 12 + (expanded ? 12 * stringModeValue.getModes().size() : 12);
                    } else if(setting instanceof MultiSelectValue multiSelectValue) {
                        final boolean expanded = expandedMultiValues.contains(multiSelectValue);
                        final String defaultString = multiSelectValue.getValue().size() + " Enabled";

                        float expandedLength = moduleFont.getStringWidth(defaultString);
                        for (String mode : multiSelectValue.getValue()) {
                            expandedLength = Math.max(expandedLength, moduleFont.getStringWidth(mode) + 12);
                        }

                        moduleFont.drawString(multiSelectValue.getName(), valueX, valueY, -1);

                        float rectWidth = 5.5f + (expanded ? expandedLength : moduleFont.getStringWidth(defaultString));
                        RoundedUtil.drawRoundedRectangle(valueX + moduleFont.getStringWidth(setting.getName()) + 7, valueY + (moduleFont.getHeight() + 10) / 2f - 11f, rectWidth, expanded ? 12 * (multiSelectValue.getValues().size() + 1) : 12, 3, new Color(0, 0, 0, 100));

                        moduleFont.drawString(defaultString, valueX + moduleFont.getStringWidth(setting.getName()) + 7 + 2.5f, valueY + 0.5f, -1);

                        if (expanded) {
                            float modeY = valueY + (moduleFont.getHeight() + 10) / 2f + 1f;
                            for (String mode : multiSelectValue.getValues()) {
                                moduleFont.drawString(mode, valueX + moduleFont.getStringWidth(setting.getName()) + 7 + 2.5f, modeY + 0.5f, -1);
                                RenderUtil.drawCheckMark(valueX + moduleFont.getStringWidth(setting.getName()) + 7 + rectWidth - 6, modeY - 2, 9, multiSelectValue.getValue().contains(mode) ? new Color(0, 200, 0).getRGB() : -1);
                                modeY += 12;
                            }
                        }

                        valueY += (moduleFont.getHeight() + 10) - 12 + (expanded ? 12 * (multiSelectValue.getValues().size() + 1) : 12);
                    }
                }

                GLUtil.endScissorBox();
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        // Dragging
        if(MathUtil.contains(mouseX, mouseY, posX, posY, moduleListWidth, 40)) {
            dragging = true;
            draggingX = mouseX - posX;
            draggingY = mouseY - posY;
        }

        // Expanding
        if(MathUtil.contains(mouseX, mouseY, posX + moduleListWidth + width - 50, posY + height - 50, 50, 50)) {
            expanding = true;
        }

        // Fonts
        if(categoryCFont == null || logoCFont == null || moduleCFont == null) {
            categoryCFont = FontStorage.getFontStorage().get("Rubik", "Regular", 20);
            moduleCFont = FontStorage.getFontStorage().get("Rubik", "Regular", 19);
            logoCFont = FontStorage.getFontStorage().get("Rubik", "Regular", 23);
        }

        final CFontRenderer categoryFont = categoryCFont.getFontRenderer();
        final CFontRenderer moduleFont = moduleCFont.getFontRenderer();
        final CFontRenderer logoFont = logoCFont.getFontRenderer();

        // Category bar
        float categoryX = posX + moduleListWidth + 20;

        for(EnumModuleType enumModuleType : EnumModuleType.values()) {
            if(MathUtil.contains(mouseX, mouseY, categoryX, posY + categoryBarHeight / 2f - categoryFont.getHeight() / 2f, categoryFont.getStringWidth(enumModuleType.getName()), categoryFont.getHeight())) {
                selectedCategory = enumModuleType;
                selectedModule = null;
                scroll = 0L;
                scrollAnimation.setValue(0);
            }

            categoryX += categoryFont.getStringWidth(enumModuleType.getName()) + 20;
        }

        if(selectedCategory != null) {
            // Module list
            float moduleY = posY + 36;

            for(AbstractModule module : ModuleStorage.getModuleStorage().getByCategory(selectedCategory)) {
                if(MathUtil.contains(mouseX, mouseY, posX + 10, moduleY, moduleFont.getStringWidth(module.getDisplayName()), moduleFont.getHeight()))
                    if(mouseButton == 0)
                        module.toggle();
                    else {
                        selectedModule = module;
                        scroll = 0L;
                        scrollAnimation.setValue(0);
                    }
                moduleY += moduleFont.getHeight() + 10;
            }

            // Settings
            if(selectedModule != null && MathUtil.contains(mouseX, mouseY, posX + moduleListWidth, posY + categoryBarHeight + 0.5f, width, height - categoryBarHeight)) {
                float baseX = posX + moduleListWidth + 12;

                float valueX = baseX;
                float valueY = posY + categoryBarHeight + 10 + (int)scrollAnimation.getValueF();

                settingLoop: for(AbstractSetting<?> setting : selectedModule.getSettings()) {
                    if(!setting.isVisible())
                        continue;

                    if(valueX != baseX)
                        valueX = baseX;

                    for(ExpandableValue expandableValue : setting.getExpandableParents()) {
                        if(!expandableValue.getValue())
                            continue settingLoop;
                        valueX += 10;
                    }

                    if(setting instanceof ExpandableValue expandableValue) {
                        if (MathUtil.contains(mouseX, mouseY, valueX, valueY, moduleFont.getStringWidth(setting.getName()) + 25, moduleFont.FONT_HEIGHT)) {
                            expandableValue.setValue(!expandableValue.getValue());
                        }

                        valueY += moduleFont.getHeight() + 10;
                    } else if(setting instanceof BooleanValue booleanValue) {
                        if (MathUtil.contains(mouseX, mouseY, valueX, valueY, moduleFont.getStringWidth(setting.getName()) + 25, moduleFont.FONT_HEIGHT)) {
                            booleanValue.setValue(!booleanValue.getValue());
                        }

                        valueY += moduleFont.getHeight() + 10;
                    } else if(setting instanceof ColorValue colorValue) {
                        colorValue.getColorPicker().handleClick(mouseX, mouseY, mouseButton, moduleCFont);

                        valueY += moduleFont.getHeight() + 2;
                        valueY += 110;
                    } else if(setting instanceof NumberValue<?>) {
                        valueY += moduleFont.getHeight() + 10;
                    } else if(setting instanceof StringValue stringValue) {
                        String value = stringValue.getValue();
                        if(typingValues.contains(stringValue))
                            value += System.currentTimeMillis() % 1000 > 500 ? "|" : " ";

                        float settingNameWidth = moduleFont.getStringWidth(setting.getName());
                        float valueXOffset = valueX + settingNameWidth + 7;
                        float valueYOffset = valueY + (moduleFont.getHeight() + 10) / 2f - 11f;

                        if (MathUtil.contains(mouseX, mouseY, valueXOffset, valueYOffset, 6 + moduleFont.getStringWidth(value), 12)) {
                            if (typingValues.contains(stringValue)) {
                                typingValues.remove(stringValue);
                            } else {
                                typingValues.add(stringValue);
                            }
                        }

                        valueY += (moduleFont.getHeight() + 10);
                    } else if(setting instanceof KeyBindValue keyBindValue) {
                        final String value = bindingKeys.contains(keyBindValue) ? "..." : Keyboard.getKeyName(keyBindValue.getValue());
                        float settingNameWidth = moduleFont.getStringWidth(setting.getName());
                        float valueXOffset = valueX + settingNameWidth + 7;
                        float valueYOffset = valueY + (moduleFont.getHeight() + 10) / 2f - 11f;

                        if (MathUtil.contains(mouseX, mouseY, valueXOffset, valueYOffset, 6 + moduleFont.getStringWidth(value), 12)) {
                            if (bindingKeys.contains(keyBindValue)) {
                                bindingKeys.remove(keyBindValue);
                            } else {
                                bindingKeys.add(keyBindValue);
                            }
                        }

                        valueY += (moduleFont.getHeight() + 10);
                    } else if(setting instanceof StringModeValue stringModeValue) {
                        boolean expanded = expandedModeValues.contains(stringModeValue);

                        float settingNameWidth = moduleFont.getStringWidth(setting.getName());
                        float valueXOffset = valueX + settingNameWidth + 7;
                        float valueYOffset = valueY + (moduleFont.getHeight() + 10) / 2f - 11f;

                        if (MathUtil.contains(mouseX, mouseY, valueXOffset, valueYOffset, 6 + moduleFont.getStringWidth(stringModeValue.getValueAsString()), 12) && !stringModeValue.getModes().isEmpty()) {
                            if (expanded) {
                                expandedModeValues.remove(stringModeValue);
                            } else {
                                expandedModeValues.add(stringModeValue);
                            }
                        }

                        if (expanded) {
                            float modeY = valueY + (moduleFont.getHeight() + 10) / 2f + 1f;

                            for (String mode : stringModeValue.getModes()) {
                                if (stringModeValue.getValue().equals(mode)) {
                                    continue;
                                }

                                float modeXOffset = valueXOffset + 2.5f;

                                if (MathUtil.contains(mouseX, mouseY, modeXOffset, modeY + 0.5f, moduleFont.getStringWidth(mode), moduleFont.getHeight())) {
                                    stringModeValue.setValue(mode);
                                }

                                modeY += 12;
                            }
                        }

                        valueY += (moduleFont.getHeight() + 10) - 12 + (expanded ? 12 * stringModeValue.getModes().size() : 12);

                    } else if(setting instanceof MultiSelectValue multiSelectValue) {
                        final boolean expanded = expandedMultiValues.contains(multiSelectValue);
                        final String defaultString = multiSelectValue.getValue().size() + " Enabled";

                        float expandedLength = moduleFont.getStringWidth(defaultString);
                        for (String mode : multiSelectValue.getValue()) {
                            expandedLength = Math.max(expandedLength, moduleFont.getStringWidth(mode) + 12);
                        }

                        float rectWidth = 6 + (expanded ? expandedLength : moduleFont.getStringWidth(defaultString));
                        if(MathUtil.contains(mouseX, mouseY, valueX + moduleFont.getStringWidth(setting.getName()) + 7, valueY + (moduleFont.getHeight() + 10) / 2f - 11f, rectWidth, 12) && !multiSelectValue.getValues().isEmpty()) {
                            if(expanded) {
                                expandedMultiValues.remove(multiSelectValue);
                            } else {
                                expandedMultiValues.add(multiSelectValue);
                            }
                        }

                        if (expanded) {
                            float modeY = valueY + (moduleFont.getHeight() + 10) / 2f + 1;
                            for (String mode : multiSelectValue.getValues()) {
                                if(MathUtil.contains(mouseX, mouseY, valueX + moduleFont.getStringWidth(setting.getName()) + 7 + 2.5f, modeY + 0.5f, rectWidth, moduleFont.getHeight())) {
                                    multiSelectValue.toggle(mode);
                                }
                                modeY += 12;
                            }
                        }

                        valueY += (moduleFont.getHeight() + 10) - 12 + (expanded ? 12 * (multiSelectValue.getValues().size() + 1) : 12);
                    }
                }
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void keyTyped(char characterTyped, int keyCode) throws IOException {
        if(selectedModule != null) {
            for(KeyBindValue keyBindValue : bindingKeys) {
                keyBindValue.setValue(keyCode);
            }
            bindingKeys.clear();

            for(AbstractSetting<?> abstractSetting : selectedModule.getSettings()) {
                if(abstractSetting instanceof ColorValue colorValue) {
                    colorValue.getColorPicker().handleInput(characterTyped, keyCode);
                } else if(abstractSetting instanceof StringValue stringValue) {
                    if(typingValues.contains(stringValue)) {
                        if(keyCode == Keyboard.KEY_ESCAPE || keyCode == Keyboard.KEY_RETURN) {
                            typingValues.remove(stringValue);
                            break;
                        } else if (keyCode == Keyboard.KEY_BACK) {
                            if (!stringValue.getValue().isEmpty()) {
                                stringValue.setValue(stringValue.getValue().substring(0, stringValue.getValue().length() - 1));
                            }
                        } else if (ChatAllowedCharacters.isAllowedCharacter(characterTyped)) {
                            stringValue.setValue(stringValue.getValue() + characterTyped);
                        }
                    }
                }
            }
        } else {
            bindingKeys.clear();
        }

        super.keyTyped(characterTyped, keyCode);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

}
