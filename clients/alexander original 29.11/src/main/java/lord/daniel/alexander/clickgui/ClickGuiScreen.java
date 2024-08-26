package lord.daniel.alexander.clickgui;

import lord.daniel.alexander.Modification;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.module.impl.hud.ClickGuiModule;
import lord.daniel.alexander.module.impl.hud.ColorModule;
import lord.daniel.alexander.settings.AbstractSetting;
import lord.daniel.alexander.settings.impl.bool.BooleanValue;
import lord.daniel.alexander.settings.impl.bool.ExpandableValue;
import lord.daniel.alexander.settings.impl.mode.MultiSelectValue;
import lord.daniel.alexander.settings.impl.mode.StringModeValue;
import lord.daniel.alexander.settings.impl.number.KeyBindValue;
import lord.daniel.alexander.settings.impl.number.NumberValue;
import lord.daniel.alexander.settings.impl.number.color.ClientColorValue;
import lord.daniel.alexander.settings.impl.number.color.ColorValue;
import lord.daniel.alexander.settings.impl.string.StringValue;
import lord.daniel.alexander.storage.impl.FontStorage;
import lord.daniel.alexander.storage.impl.ModuleStorage;
import lord.daniel.alexander.util.math.MathUtil;
import lord.daniel.alexander.util.render.RenderUtil;
import lord.daniel.alexander.util.render.color.ColorUtil;
import lord.daniel.alexander.util.render.shader.render.ingame.ShaderRenderer;
import lord.daniel.alexander.util.render.shader.shaders.GradientShader;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.*;
import java.io.IOException;
import java.util.List;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
public class ClickGuiScreen extends GuiScreen {

    // Fonts
    private final FontRenderer textFont = FontStorage.getFontStorage().get("Consolas", 16);
    private final FontRenderer categoryFont = FontStorage.getFontStorage().get("Consolas", 17);
    private final FontRenderer titleFont = FontStorage.getFontStorage().get("Consolas", 21);
    private final FontRenderer moduleTitleFont = FontStorage.getFontStorage().get("Consolas", 20);

    // Colours
    private final Color backColor = new Color(30, 30, 30, 200), secondBackColor = new Color(30, 30, 30, 100), textColor = new Color(200, 200, 200);

    // Pre-set size
    private final float modulesWidth = 101;
    private final float categoryHeight = 26;

    // Position and Size
    private float posX = -1, posY = -1;
    private float width = -1, height = -1;

    // Categories
    private final List<EnumModuleType> enabledCategories = new ArrayList<>();
    private boolean filled = false; // Can't just do if enabledCategories.isEmpty() since the user would not be able to unselect all categories
    private boolean categorySelectionOpened = false;

    // Dragging
    private float draggingX, draggingY;
    private boolean dragging;

    // Selected
    private EnumModuleType selectedCategory = null;
    private AbstractModule selectedModule = null;
    private KeyBindValue currentlyBinding = null;
    private StringValue currentlyTyping = null;

    // Scrolling
    float modScroll = 0;
    float elementScroll = 0;

    // Expanding
    public boolean expanding;

    // Initializing
    private final ClickGuiModule clickGuiModule;

    public ClickGuiScreen() {
        clickGuiModule = ModuleStorage.getModuleStorage().getByClass(ClickGuiModule.class);
    }

    // Added reset gui button
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, super.width - 60, super.height - 25, 55, 20, "Reset Gui"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Initializing categories
        if(!filled) {
            for(EnumModuleType enumModuleType : EnumModuleType.values()) {
                if(enumModuleType.isEnabledByDefault()) {
                    enabledCategories.add(enumModuleType);
                }
            }
            filled = true;
        }

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
            float categoryWidth = modulesWidth + 15;

            for(EnumModuleType category : enabledCategories){
                String name = category.getName().toUpperCase();
                categoryWidth += categoryFont.getStringWidth(name) + 20;
            }

            categoryWidth += categoryFont.getStringWidth("+") + 15;

            float deltaY = mouseY - (posY + height);
            height = Math.max(200, height + deltaY);
            float deltaX = mouseX - (posX + width);
            width = Math.max(categoryWidth, width + deltaX);
        }

        // Scroll
        int scroll = Mouse.getDWheel(); // Creating the scroll variable, because you can't use the getDWheel method multiple times per render

        if(RenderUtil.isHovered(mouseX, mouseY, posX, posY + categoryHeight, modulesWidth, height - categoryHeight)) {
            modScroll += scroll / 10F;
        }
        if(RenderUtil.isHovered(mouseX, mouseY, posX + modulesWidth, posY + categoryHeight + 0.5f, width - modulesWidth, height - categoryHeight)) {
            elementScroll += scroll / 10F;
        }
        elementScroll = Math.min(elementScroll, 0);
        modScroll = Math.min(modScroll, 0);

        // Rendering
        ShaderRenderer.render((shaders) -> {
            // Rendering background rectangles
            RenderUtil.drawRect(posX, posY, width, height, backColor);
            RenderUtil.drawRect(posX, posY, modulesWidth, height, secondBackColor);
            RenderUtil.drawRect(posX + modulesWidth, posY, width - modulesWidth, categoryHeight, secondBackColor);

            // Rendering title
            titleFont.drawString(Modification.NAME.toUpperCase(), posX + modulesWidth / 2f - titleFont.getStringWidth(Modification.NAME.toUpperCase()) / 2f, posY + categoryHeight / 2f - titleFont.FONT_HEIGHT / 2f, ColorModule.getClientColor().getRGB());

            // Rendering category names
            float categoryTextX = posX + modulesWidth + 15;
            final float categoryTextY = posY + categoryHeight / 2f - categoryFont.FONT_HEIGHT / 2f - 0.5f;

            for(EnumModuleType category : enabledCategories){
                String name = category.getName().toUpperCase();

                categoryFont.drawString(name, categoryTextX, categoryTextY, category == selectedCategory ? ColorModule.getClientColor().getRGB() : textColor.getRGB());
                categoryTextX += categoryFont.getStringWidth(name) + 20;
            }

            categoryFont.drawString("+", categoryTextX, categoryTextY, textColor.getRGB());

            categoryTextX += categoryFont.getStringWidth("+") + 10;

            // Rendering category selection window
            if(categorySelectionOpened) {
                final float categoryHeight = textFont.FONT_HEIGHT + 3;

                float categoryY = categoryTextY;

                for(EnumModuleType enumModuleType : EnumModuleType.values()) {
                    RenderUtil.drawRect(categoryTextX, categoryY, 60, categoryHeight, backColor);
                    textFont.drawString(enumModuleType.getName(), categoryTextX + 3, categoryY + categoryHeight / 2 - textFont.FONT_HEIGHT / 2f, enabledCategories.contains(enumModuleType) ? ColorModule.getClientColor().getRGB() : textColor.getRGB());
                    categoryY += categoryHeight;
                }
            }

            if(selectedCategory != null) {
                // Rendering modules
                RenderUtil.startScissorBox();
                RenderUtil.drawScissorBox(posX, posY + categoryHeight, modulesWidth, height - categoryHeight);

                final float moduleTextX = posX + 7;
                float moduleTextY = posY + categoryHeight + modScroll + 5;

                List<AbstractModule> modules = new ArrayList<>(ModuleStorage.getModuleStorage().getModules(selectedCategory));
                modules.sort(Comparator.comparing(AbstractModule::getDisplayName));

                for(AbstractModule abstractModule : modules) {
                    textFont.drawString((selectedModule == abstractModule ? "> " : "") + abstractModule.getDisplayName(), moduleTextX, moduleTextY, abstractModule.isEnabled() ? ColorModule.getClientColor().getRGB() : textColor.getRGB());
                    moduleTextY += textFont.FONT_HEIGHT + 3;
                }

                RenderUtil.endScissorBox();
                RenderUtil.startScissorBox();

                // Rendering current module
                RenderUtil.drawScissorBox(posX + modulesWidth, posY + categoryHeight + 0.5f, width - modulesWidth, height - categoryHeight);

                if(selectedModule != null) {
                    float valueX = posX + modulesWidth + 15;
                    float valueY = posY + categoryHeight + 5 + elementScroll;

                    moduleTitleFont.drawString(selectedModule.getDisplayName().toUpperCase() + ":", valueX, valueY, ColorModule.getClientColor().getRGB());

                    valueY += moduleTitleFont.FONT_HEIGHT + 4;

                    textFont.drawString("Category: " + String.join("/", Arrays.stream(selectedModule.getCategories()).map(EnumModuleType::getName).
                            toArray(String[]::new)), valueX, valueY, ColorModule.getClientColor().getRGB());

                    valueY += textFont.FONT_HEIGHT + 3;

                    moduleLoop: for(AbstractSetting<?> abstractSetting : selectedModule.getSettings()) {
                        if(!abstractSetting.isVisible())
                            continue;

                        valueX = posX + modulesWidth + 15;

                        for(ExpandableValue expandableValue : abstractSetting.getExpandableParents()) {
                            if(!expandableValue.getValue())
                                continue moduleLoop;
                            valueX += 10;
                        }

                        final float startY = valueY;

                        if(abstractSetting instanceof ExpandableValue) {
                            final ExpandableValue expandableValue = (ExpandableValue) abstractSetting;
                            final String name = expandableValue.getName() + " " + (expandableValue.getValue() ? ">" : "<");

                            textFont.drawString(name, valueX, valueY, ColorModule.getClientColor().getRGB());

                            valueY += textFont.FONT_HEIGHT + 3;
                        } else if(abstractSetting instanceof BooleanValue) {
                            final BooleanValue checkBoxValue = (BooleanValue) abstractSetting;
                            final String name = checkBoxValue.getName() + ": ";
                            final String value = "X";

                            textFont.drawString(name, valueX, valueY, textColor.getRGB());
                            textFont.drawString(value, valueX + textFont.getStringWidth(name), valueY, checkBoxValue.getValue() ? ColorModule.getClientColor().getRGB() : textColor.getRGB());

                            valueY += textFont.FONT_HEIGHT + 3;
                        } else if(abstractSetting instanceof ClientColorValue) {
                            final ClientColorValue clientColorValue = (ClientColorValue) abstractSetting;
                            if(clientColorValue.isAllowSync()) {
                                final String name = clientColorValue.getName() + "Sync: ";
                                final String value = "X";

                                textFont.drawString(name, valueX, valueY, textColor.getRGB());
                                textFont.drawString(value, valueX + textFont.getStringWidth(name), valueY, clientColorValue.isSync() ? ColorModule.getClientColor().getRGB() : textColor.getRGB());

                                valueY += textFont.FONT_HEIGHT + 3;
                            }
                        } else if(abstractSetting instanceof StringValue) {
                            final StringValue stringValue = (StringValue) abstractSetting;
                            final String name = stringValue.getName() + ": ";
                            final String value = stringValue.getValue();

                            textFont.drawString(name, valueX, valueY, textColor.getRGB());
                            textFont.drawString(value + (currentlyTyping == stringValue ? "_" : ""), valueX + textFont.getStringWidth(name + (currentlyTyping == stringValue ? "_" : "")), valueY, ColorModule.getClientColor().getRGB());

                            valueY += textFont.FONT_HEIGHT + 3;
                        } else if(abstractSetting instanceof KeyBindValue) {
                            final KeyBindValue keyValue = (KeyBindValue) abstractSetting;
                            final int key = keyValue.getValue();
                            final String name = abstractSetting.getName() + ": ";
                            final String value = currentlyBinding == abstractSetting ? "Binding" : String.format("%s (%d)", Keyboard.getKeyName(key), key);

                            textFont.drawString(name, valueX, valueY, textColor.getRGB());
                            textFont.drawString(value, valueX + textFont.getStringWidth(name), valueY, ColorModule.getClientColor().getRGB());

                            valueY += textFont.FONT_HEIGHT + 3;
                        } else if(abstractSetting instanceof StringModeValue) {
                            final StringModeValue modeBoxValue = (StringModeValue) abstractSetting;
                            final String name = modeBoxValue.getName() + ": ";

                            textFont.drawString(name, valueX, valueY, textColor.getRGB());

                            float modeTextX = valueX + textFont.getStringWidth(name);

                            for(String mode : modeBoxValue.getModes()) {
                                if((modeTextX + textFont.getStringWidth(mode + " ")) >= posX + width) {
                                    modeTextX = valueX + textFont.getStringWidth(name);
                                    valueY += textFont.FONT_HEIGHT + 3;
                                }

                                textFont.drawString(mode, modeTextX, valueY, modeBoxValue.getValue().equalsIgnoreCase(mode) ? ColorModule.getClientColor().getRGB() : textColor.getRGB());

                                modeTextX += textFont.getStringWidth(mode + " ");
                            }

                            valueY += textFont.FONT_HEIGHT + 3;
                        } else if(abstractSetting instanceof MultiSelectValue) {
                            final MultiSelectValue modeBoxValue = (MultiSelectValue) abstractSetting;
                            final String name = modeBoxValue.getName() + ": ";

                            textFont.drawString(name, valueX, valueY, textColor.getRGB());

                            float modeTextX = valueX + textFont.getStringWidth(name);

                            for(String mode : modeBoxValue.getValues()) {
                                if((modeTextX + textFont.getStringWidth(mode + " ")) >= posX + width) {
                                    modeTextX = valueX + textFont.getStringWidth(name);
                                    valueY += textFont.FONT_HEIGHT + 3;
                                }

                                textFont.drawString(mode, modeTextX, valueY, modeBoxValue.is(mode) ? ColorModule.getClientColor().getRGB() : textColor.getRGB());

                                modeTextX += textFont.getStringWidth(mode + " ");
                            }

                            valueY += textFont.FONT_HEIGHT + 3;
                        } else if(abstractSetting instanceof NumberValue) {
                            final NumberValue<?> numberValue = (NumberValue<?>) abstractSetting;
                            final String name = abstractSetting.getName() + ": ";

                            final float min = numberValue.getMin().floatValue(),
                                    max = numberValue.getMax().floatValue(),
                                    curValue = numberValue.getValue().floatValue(),
                                    clampedValue = MathHelper.clamp_float(curValue, min, max);

                            final int decimalPlaces = numberValue.getDecimalPlaces();

                            final float sliderX = valueX + textFont.getStringWidth(name) + 1,
                                    sliderY = valueY - 0.5f + 1,
                                    sliderWidth = 120 - 2,
                                    sliderHeight = 10 - 2,
                                    length = MathHelper.floor_double((clampedValue - min) / (max - min) * sliderWidth);

                            textFont.drawString(name, valueX, valueY, textColor.getRGB());

                            RenderUtil.drawBorderedRect(sliderX, sliderY, sliderX + sliderWidth, sliderY + sliderHeight, 1, 0, Color.BLACK.getRGB(), false);
                            RenderUtil.drawBorderedRect(sliderX, sliderY, sliderX + length, sliderY + sliderHeight, 1, ColorModule.getClientColor().getRGB(), 0, false);

                            if(Mouse.isButtonDown(0) && RenderUtil.isHovered(mouseX, mouseY, sliderX, sliderY, sliderWidth, sliderHeight)) {
                                double newValue = MathUtil.round((mouseX - sliderX) * (max - min) / (sliderWidth - 1.0f) + min, decimalPlaces);
                                ((AbstractSetting<Number>) abstractSetting).setValue(newValue);
                            }

                            textFont.drawString(curValue + " + - ", sliderX + sliderWidth + 5, valueY, ColorModule.getClientColor().getRGB());

                            valueY += textFont.FONT_HEIGHT + 3;
                        } else if(abstractSetting instanceof ColorValue) {
                            final ColorValue colorValue = (ColorValue) abstractSetting;
                            final String name = abstractSetting.getName() + ": ";

                            textFont.drawString(name, valueX, valueY, textColor.getRGB());

                            colorSlider: {
                                final float sliderX = valueX + textFont.getStringWidth(name) + 1,
                                        sliderY = valueY - 0.5f + 1,
                                        sliderWidth = 120 - 2,
                                        sliderHeight = 10 - 2;


                                RenderUtil.drawBorderedRect(sliderX, sliderY, sliderX + sliderWidth, sliderY + sliderHeight, 1, 0, Color.BLACK.getRGB(), false);

                                for (float i = 0; i < sliderWidth; i++) {
                                    Color color = Color.getHSBColor(i / (sliderWidth), colorValue.getSaturation(), colorValue.getBrightness());
                                    color = ColorUtil.setAlpha(color, colorValue.getAlpha());
                                    RenderUtil.drawRect(sliderX + i, sliderY, 1, sliderHeight, color.getRGB());

                                    if(color.equals(colorValue.getValue()))
                                        RenderUtil.drawRect(sliderX + i, sliderY, 1, sliderHeight, Color.BLACK.getRGB());

                                    if(Mouse.isButtonDown(0) && RenderUtil.isHovered(mouseX, mouseY, sliderX, sliderY, sliderWidth, sliderHeight)) {
                                        if(mouseX == sliderX + i) {
                                            colorValue.setValue(color);
                                        }
                                    }
                                }

                                String displayValue = "Color: " + colorValue.getValue().getRGB();

                                textFont.drawString(displayValue, sliderX + sliderWidth + 5, valueY, ColorModule.getClientColor().getRGB());
                                valueY += textFont.FONT_HEIGHT + 3;
                            }

                            saturationSlider: {
                                final float min = 0.01f,
                                        max = 1,
                                        curValue = colorValue.getSaturation(),
                                        clampedValue = MathHelper.clamp_float(curValue, min, max);

                                final int decimalPlaces = 2;

                                final float sliderX = valueX + textFont.getStringWidth(name) + 1,
                                        sliderY = valueY - 0.5f + 1,
                                        sliderWidth = 120 - 2,
                                        sliderHeight = 10 - 2,
                                        length = MathHelper.floor_double((clampedValue - min) / (max - min) * sliderWidth);

                                RenderUtil.drawBorderedRect(sliderX, sliderY, sliderX + sliderWidth, sliderY + sliderHeight, 1, 0, Color.BLACK.getRGB(), false);

                                GradientShader.drawGradientLR(sliderX, sliderY, sliderWidth, sliderHeight, 1, Color.WHITE, ColorModule.getClientColor());

                                RenderUtil.drawRect(sliderX + length - 0.5f, sliderY, 1, sliderHeight, Color.BLACK.getRGB());

                                if(Mouse.isButtonDown(0) && RenderUtil.isHovered(mouseX, mouseY, sliderX, sliderY, sliderWidth, sliderHeight)) {
                                    double newValue = MathUtil.round((mouseX - sliderX) * (max - min) / (sliderWidth - 1.0f) + min, decimalPlaces);
                                    colorValue.setSaturation((float) newValue);
                                }

                                textFont.drawString("Saturation: " + colorValue.getSaturation(), sliderX + sliderWidth + 5, valueY, ColorModule.getClientColor().getRGB());

                                valueY += textFont.FONT_HEIGHT + 3;
                            }

                            brightnessSlider: {
                                final float min = 0,
                                        max = 1,
                                        curValue = colorValue.getBrightness(),
                                        clampedValue = MathHelper.clamp_float(curValue, min, max);

                                final int decimalPlaces = 2;

                                final float sliderX = valueX + textFont.getStringWidth(name) + 1,
                                        sliderY = valueY - 0.5f + 1,
                                        sliderWidth = 120 - 2,
                                        sliderHeight = 10 - 2,
                                        length = MathHelper.floor_double((clampedValue - min) * sliderWidth);

                                RenderUtil.drawBorderedRect(sliderX, sliderY, sliderX + sliderWidth, sliderY + sliderHeight, 1, 0, Color.BLACK.getRGB(), false);

                                GradientShader.drawGradientLR(sliderX, sliderY, sliderWidth, sliderHeight, 1, Color.BLACK, Color.WHITE);

                                RenderUtil.drawRect(sliderX + length - 0.5f, sliderY, 1, sliderHeight, Color.BLACK.getRGB());

                                if(Mouse.isButtonDown(0) && RenderUtil.isHovered(mouseX, mouseY, sliderX, sliderY, sliderWidth, sliderHeight)) {
                                    double newValue = MathUtil.round((mouseX - sliderX) / (sliderWidth - 1.0f) + min, decimalPlaces);
                                    colorValue.setBrightness((float) newValue);
                                }

                                textFont.drawString("Brightness: " + colorValue.getBrightness(), sliderX + sliderWidth + 5, valueY, ColorModule.getClientColor().getRGB());

                                valueY += textFont.FONT_HEIGHT + 3;
                            }

                            alphaSlider: {
                                final float min = 0,
                                        max = 1,
                                        curValue = colorValue.getAlpha(),
                                        clampedValue = MathHelper.clamp_float(curValue, min, max);

                                final int decimalPlaces = 2;

                                final float sliderX = valueX + textFont.getStringWidth(name) + 1,
                                        sliderY = valueY - 0.5f + 1,
                                        sliderWidth = 120 - 2,
                                        sliderHeight = 10 - 2,
                                        length = MathHelper.floor_double((clampedValue - min) * sliderWidth);

                                RenderUtil.drawBorderedRect(sliderX, sliderY, sliderX + sliderWidth, sliderY + sliderHeight, 1, 0, Color.BLACK.getRGB(), false);

                                GradientShader.drawGradientLR(sliderX, sliderY, sliderWidth, sliderHeight, 1, new Color(0, 0, 0, 0), ColorModule.getClientColor());

                                RenderUtil.drawRect(sliderX + length - 0.5f, sliderY, 1, sliderHeight, Color.BLACK.getRGB());

                                if(Mouse.isButtonDown(0) && RenderUtil.isHovered(mouseX, mouseY, sliderX, sliderY, sliderWidth, sliderHeight)) {
                                    double newValue = MathUtil.round((mouseX - sliderX) / (sliderWidth - 1.0f) + min, decimalPlaces);
                                    colorValue.setAlpha((float) newValue);
                                }

                                textFont.drawString("Alpha: " + colorValue.getAlpha(), sliderX + sliderWidth + 5, valueY, ColorModule.getClientColor().getRGB());

                                valueY += textFont.FONT_HEIGHT + 3;
                            }

                            redSlider: {
                                final float min = 0,
                                        max = 255,
                                        curValue = colorValue.getValue().getRed(),
                                        clampedValue = MathHelper.clamp_float(curValue, min, max);

                                final int decimalPlaces = 0;

                                final float sliderX = valueX + textFont.getStringWidth(name) + 1,
                                        sliderY = valueY - 0.5f + 1,
                                        sliderWidth = 120 - 2,
                                        sliderHeight = 10 - 2,
                                        length = MathHelper.floor_double((clampedValue - min) / (max - min) * sliderWidth);

                                RenderUtil.drawBorderedRect(sliderX, sliderY, sliderX + sliderWidth, sliderY + sliderHeight, 1, 0, Color.BLACK.getRGB(), false);
                                RenderUtil.drawBorderedRect(sliderX, sliderY, sliderX + length, sliderY + sliderHeight, 1, ColorModule.getClientColor().getRGB(), 0, false);

                                if(Mouse.isButtonDown(0) && RenderUtil.isHovered(mouseX, mouseY, sliderX, sliderY, sliderWidth, sliderHeight)) {
                                    double newValue = MathUtil.round((mouseX - sliderX) * (max - min) / (sliderWidth - 1.0f) + min, decimalPlaces);
                                    colorValue.setValue(new Color((int) newValue, colorValue.getValue().getGreen(), colorValue.getValue().getBlue(), colorValue.getValue().getAlpha()));
                                }

                                textFont.drawString("RED: " + colorValue.getValue().getRed(), sliderX + sliderWidth + 5, valueY, ColorModule.getClientColor().getRGB());

                                valueY += textFont.FONT_HEIGHT + 3;
                            }

                            greenSlider: {
                                final float min = 0,
                                        max = 255,
                                        curValue = colorValue.getValue().getGreen(),
                                        clampedValue = MathHelper.clamp_float(curValue, min, max);

                                final int decimalPlaces = 0;

                                final float sliderX = valueX + textFont.getStringWidth(name) + 1,
                                        sliderY = valueY - 0.5f + 1,
                                        sliderWidth = 120 - 2,
                                        sliderHeight = 10 - 2,
                                        length = MathHelper.floor_double((clampedValue - min) / (max - min) * sliderWidth);

                                RenderUtil.drawBorderedRect(sliderX, sliderY, sliderX + sliderWidth, sliderY + sliderHeight, 1, 0, Color.BLACK.getRGB(), false);
                                RenderUtil.drawBorderedRect(sliderX, sliderY, sliderX + length, sliderY + sliderHeight, 1, ColorModule.getClientColor().getRGB(), 0, false);

                                if(Mouse.isButtonDown(0) && RenderUtil.isHovered(mouseX, mouseY, sliderX, sliderY, sliderWidth, sliderHeight)) {
                                    double newValue = MathUtil.round((mouseX - sliderX) * (max - min) / (sliderWidth - 1.0f) + min, decimalPlaces);
                                    colorValue.setValue(new Color(colorValue.getValue().getRed(), (int) newValue, colorValue.getValue().getBlue(), colorValue.getValue().getAlpha()));
                                }

                                textFont.drawString("GREEN: " + colorValue.getValue().getGreen(), sliderX + sliderWidth + 5, valueY, ColorModule.getClientColor().getRGB());

                                valueY += textFont.FONT_HEIGHT + 3;
                            }

                            blueSlider: {
                                final float min = 0,
                                        max = 255,
                                        curValue = colorValue.getValue().getBlue(),
                                        clampedValue = MathHelper.clamp_float(curValue, min, max);

                                final int decimalPlaces = 0;

                                final float sliderX = valueX + textFont.getStringWidth(name) + 1,
                                        sliderY = valueY - 0.5f + 1,
                                        sliderWidth = 120 - 2,
                                        sliderHeight = 10 - 2,
                                        length = MathHelper.floor_double((clampedValue - min) / (max - min) * sliderWidth);

                                RenderUtil.drawBorderedRect(sliderX, sliderY, sliderX + sliderWidth, sliderY + sliderHeight, 1, 0, Color.BLACK.getRGB(), false);
                                RenderUtil.drawBorderedRect(sliderX, sliderY, sliderX + length, sliderY + sliderHeight, 1, ColorModule.getClientColor().getRGB(), 0, false);

                                if(Mouse.isButtonDown(0) && RenderUtil.isHovered(mouseX, mouseY, sliderX, sliderY, sliderWidth, sliderHeight)) {
                                    double newValue = MathUtil.round((mouseX - sliderX) * (max - min) / (sliderWidth - 1.0f) + min, decimalPlaces);
                                    colorValue.setValue(new Color(colorValue.getValue().getRed(), colorValue.getValue().getGreen(), (int) newValue, colorValue.getValue().getAlpha()));
                                }

                                textFont.drawString("BLUE: " + colorValue.getValue().getBlue(), sliderX + sliderWidth + 5, valueY, ColorModule.getClientColor().getRGB());

                                valueY += textFont.FONT_HEIGHT + 3;
                            }

                            alphaSlider: {
                                final float min = 0,
                                        max = 255,
                                        curValue = colorValue.getValue().getAlpha(),
                                        clampedValue = MathHelper.clamp_float(curValue, min, max);

                                final int decimalPlaces = 0;

                                final float sliderX = valueX + textFont.getStringWidth(name) + 1,
                                        sliderY = valueY - 0.5f + 1,
                                        sliderWidth = 120 - 2,
                                        sliderHeight = 10 - 2,
                                        length = MathHelper.floor_double((clampedValue - min) / (max - min) * sliderWidth);

                                RenderUtil.drawBorderedRect(sliderX, sliderY, sliderX + sliderWidth, sliderY + sliderHeight, 1, 0, Color.BLACK.getRGB(), false);
                                RenderUtil.drawBorderedRect(sliderX, sliderY, sliderX + length, sliderY + sliderHeight, 1, ColorModule.getClientColor().getRGB(), 0, false);

                                if(Mouse.isButtonDown(0) && RenderUtil.isHovered(mouseX, mouseY, sliderX, sliderY, sliderWidth, sliderHeight)) {
                                    double newValue = MathUtil.round((mouseX - sliderX) * (max - min) / (sliderWidth - 1.0f) + min, decimalPlaces);
                                    colorValue.setValue(new Color(colorValue.getValue().getRed(), colorValue.getValue().getBlue(), colorValue.getValue().getBlue(), (int) newValue));
                                }

                                textFont.drawString("ALPHA: " + colorValue.getValue().getAlpha(), sliderX + sliderWidth + 5, valueY, ColorModule.getClientColor().getRGB());

                                valueY += textFont.FONT_HEIGHT + 3;
                            }

                            final float rectX = valueX + textFont.getStringWidth(name),
                                    rectY = valueY - 0.5f,
                                    rectWidth = 120,
                                    rectHeight = 10;

                            RenderUtil.drawBorderedRect(rectX, rectY, rectX + rectWidth, rectY + rectHeight, 1f, colorValue.getValue().getRGB(), Color.BLACK.getRGB(), true);

                            valueY += textFont.FONT_HEIGHT + 3;

                        }

                        float renderX = posX + modulesWidth + 15;

                        float optimalHeight = textFont.FONT_HEIGHT + 3;

                        for(ExpandableValue expandableValue : abstractSetting.getExpandableParents()) {
                            if(!expandableValue.getValue())
                                continue;

                            renderX += 10;

                            List<AbstractSetting<?>> expandableValueList = expandableValue.getValues().stream().filter(value -> {
                                if(!value.isVisible())
                                    return false;
                                for(ExpandableValue expandableValueFromStream : value.getExpandableParents())
                                    if(!expandableValueFromStream.getValue())
                                        return false;
                                return true;
                            }).toList();

                            RenderUtil.drawRect(renderX - 10 + 3.5f, startY - 1f, 1f, expandableValueList.indexOf(abstractSetting) == expandableValueList.size() - 1 ? (valueY - startY) / 2 : (valueY - startY), ColorModule.getClientColor().getRGB());

                            if(abstractSetting.getExpandableParents().indexOf(expandableValue) == abstractSetting.getExpandableParents().size() - 1)
                                RenderUtil.drawRect(renderX - 10 + 4.5f, startY + optimalHeight / 2f - 2f, 3f, 1, ColorModule.getClientColor().getRGB());
                        }
                    }
                }

                RenderUtil.endScissorBox();
            }
        });

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        // Dragging
        if(RenderUtil.isHovered(mouseX, mouseY, posX, posY, modulesWidth, categoryHeight)) {
            dragging = true;
            draggingX = mouseX - posX;
            draggingY = mouseY - posY;
        }

        // Expanding
        if(RenderUtil.isHovered(mouseX, mouseY, posX + width - 50, posY + height - 50, 50, 50)) {
            expanding = true;
        }

        // Categories
        float categoryTextX = posX + modulesWidth + 15;
        final float categoryTextY = posY + categoryHeight / 2f - categoryFont.FONT_HEIGHT / 2f - 0.5f;

        for(EnumModuleType enumModuleType : enabledCategories){
            final String name = enumModuleType.getName().toUpperCase();

            if(RenderUtil.isHovered(mouseX, mouseY, categoryTextX, categoryTextY, textFont.getStringWidth(name), textFont.FONT_HEIGHT)) {
                selectedCategory = enumModuleType;
                selectedModule = null;
                modScroll = 0F;
                elementScroll = 0F;
            }

            categoryTextX += textFont.getStringWidth(name) + 20;
        }
        if(RenderUtil.isHovered(mouseX, mouseY, categoryTextX - 2, categoryTextY - 2, categoryFont.getStringWidth("+") + 4, categoryFont.FONT_HEIGHT + 4)) {
            categorySelectionOpened = !categorySelectionOpened;
        }

        categoryTextX += categoryFont.getStringWidth("+") + 10;

        // Rendering category selection window
        if(categorySelectionOpened) {
            final float categoryHeight = textFont.FONT_HEIGHT + 3;

            float categoryY = categoryTextY;

            for(EnumModuleType enumModuleType : EnumModuleType.values()) {
                if(RenderUtil.isHovered(mouseX, mouseY, categoryTextX, categoryY, 60, categoryHeight)) {
                    if(enabledCategories.contains(enumModuleType))
                        enabledCategories.remove(enumModuleType);
                    else
                        enabledCategories.add(enumModuleType);

                    enabledCategories.sort(Comparator.comparingInt(Enum::ordinal));
                }

                categoryY += categoryHeight;
            }
        }

        // Modules
        float moduleTextY = posY + categoryHeight + 5 + modScroll;

        List<AbstractModule> modules = new ArrayList<>(ModuleStorage.getModuleStorage().getModules(selectedCategory));
        modules.sort(Comparator.comparing(AbstractModule::getDisplayName));

        for(AbstractModule abstractModule : modules) {
            if(RenderUtil.isHovered(mouseX, mouseY, posX + 7, moduleTextY, textFont.getStringWidth((selectedModule == abstractModule ? "> " : "") + abstractModule.getDisplayName()), textFont.FONT_HEIGHT)) {
                if(button == 0)
                    abstractModule.toggle();
                else if(button == 1) {
                    selectedModule = abstractModule;
                    elementScroll = 0F;
                }
            }

            moduleTextY += textFont.FONT_HEIGHT + 3;
        }

        // Current Module
        float valueX = posX + modulesWidth + 15;
        float valueY = posY + categoryHeight + 5 + elementScroll + moduleTitleFont.FONT_HEIGHT + 4;

        if(selectedModule != null) {
            valueY += textFont.FONT_HEIGHT + 3;

            moduleLoop: for(AbstractSetting<?> value : selectedModule.getSettings()) {
                if(!value.isVisible())
                    continue;

                valueX = posX + modulesWidth + 15;

                for(ExpandableValue expandableValue : value.getExpandableParents()) {
                    if(!expandableValue.getValue())
                        continue moduleLoop;
                    valueX += 10;
                }

                if(value instanceof ExpandableValue) {
                    final ExpandableValue expandableValue = (ExpandableValue) value;

                    if (RenderUtil.isHovered(mouseX, mouseY, valueX, valueY, textFont.getStringWidth(String.format(expandableValue.getName() + " " + (expandableValue.getValue() ? ">" : "<"), value.getName())), textFont.FONT_HEIGHT))
                        expandableValue.setValue(!expandableValue.getValue());

                    valueY += textFont.FONT_HEIGHT + 3;
                } else if(value instanceof BooleanValue) {
                    final BooleanValue checkBoxValue = (BooleanValue) value;

                    if (RenderUtil.isHovered(mouseX, mouseY, valueX, valueY, textFont.getStringWidth(String.format("%s: X", value.getName())), textFont.FONT_HEIGHT))
                        checkBoxValue.setValue(!checkBoxValue.getValue());

                    valueY += textFont.FONT_HEIGHT + 3;
                } else if(value instanceof ClientColorValue) {
                    final ClientColorValue clientColorValue = (ClientColorValue) value;

                    if(clientColorValue.isAllowSync()) {
                        if (RenderUtil.isHovered(mouseX, mouseY, valueX, valueY, textFont.getStringWidth(String.format("%s: X", value.getName() + "Sync")), textFont.FONT_HEIGHT))
                            clientColorValue.setSync(!clientColorValue.isSync());

                        valueY += textFont.FONT_HEIGHT + 3;
                    }
                } else if(value instanceof StringValue) {
                    final StringValue stringValue = (StringValue) value;

                    if(RenderUtil.isHovered(mouseX, mouseY, valueX, valueY, textFont.getStringWidth(value.getName() + ": " + value.getValue() + (currentlyTyping == stringValue ? "_" : "")), textFont.FONT_HEIGHT))
                        currentlyTyping = stringValue;

                    valueY += textFont.FONT_HEIGHT + 3;
                } else if(value instanceof KeyBindValue) {
                    final KeyBindValue keyValue = (KeyBindValue) value;
                    final int key = keyValue.getValue();

                    if(RenderUtil.isHovered(mouseX, mouseY, valueX, valueY, textFont.getStringWidth(String.format("%s: %s", value.getName(), currentlyBinding == value ? "Binding" : String.format("%s (%d)", Keyboard.getKeyName(key), key))), textFont.FONT_HEIGHT))
                        currentlyBinding = keyValue;

                    valueY += textFont.FONT_HEIGHT + 3;
                } else if(value instanceof StringModeValue) {
                    final StringModeValue modeBoxValue = (StringModeValue) value;
                    final String name = modeBoxValue.getName() + ": ";

                    float modeTextX = valueX + textFont.getStringWidth(name);

                    for(String mode : modeBoxValue.getModes()) {
                        if((modeTextX + textFont.getStringWidth(mode + " ")) >= posX + width) {
                            modeTextX = valueX + textFont.getStringWidth(name);
                            valueY += textFont.FONT_HEIGHT + 3;
                        }

                        if(RenderUtil.isHovered(mouseX, mouseY, modeTextX, valueY, textFont.getStringWidth(mode), textFont.FONT_HEIGHT))
                            modeBoxValue.setValue(mode);

                        modeTextX += textFont.getStringWidth(mode + " ");
                    }
                    valueY += textFont.FONT_HEIGHT + 3;
                } else if(value instanceof MultiSelectValue) {
                    final MultiSelectValue modeBoxValue = (MultiSelectValue) value;
                    final String name = modeBoxValue.getName() + ": ";

                    float modeTextX = valueX + textFont.getStringWidth(name);

                    for(String mode : modeBoxValue.getValues()) {
                        if((modeTextX + textFont.getStringWidth(mode + " ")) >= posX + width) {
                            modeTextX = valueX + textFont.getStringWidth(name);
                            valueY += textFont.FONT_HEIGHT + 3;
                        }

                        if(RenderUtil.isHovered(mouseX, mouseY, modeTextX, valueY, textFont.getStringWidth(mode), textFont.FONT_HEIGHT)) {
                            modeBoxValue.toggle(mode);
                        }

                        modeTextX += textFont.getStringWidth(mode + " ");
                    }
                    valueY += textFont.FONT_HEIGHT + 3;
                } else if(value instanceof NumberValue) {
                    final NumberValue<?> numberValue = (NumberValue<?>) value;
                    final String name = value.getName() + ": ";

                    final float min = numberValue.getMin().floatValue(),
                            max = numberValue.getMax().floatValue(),
                            curValue = numberValue.getValue().floatValue();

                    final int decimalPlaces = numberValue.getDecimalPlaces();

                    final float sliderX = valueX + textFont.getStringWidth(name) + 1,
                            sliderY = valueY - 0.5f + 1,
                            sliderWidth = 120 - 2,
                            sliderHeight = 10 - 2,
                            length = MathHelper.floor_double((curValue - min) / (max - min) * sliderWidth);

                    textFont.drawString(curValue + " + - ", sliderX + sliderWidth + 5, valueY, ColorModule.getClientColor().getRGB());

                    if(RenderUtil.isHovered(mouseX, mouseY, sliderX + sliderWidth + 5 + textFont.getStringWidth(curValue + " "), valueY, textFont.getStringWidth("+"), textFont.FONT_HEIGHT)) {
                        ((AbstractSetting<Number>) value).setValue(numberValue.getValue().doubleValue() + Math.pow(10, -decimalPlaces));
                    } else if(RenderUtil.isHovered(mouseX, mouseY, sliderX + sliderWidth + 5 + textFont.getStringWidth(curValue + " + "), valueY, textFont.getStringWidth("-"), textFont.FONT_HEIGHT)) {
                        ((AbstractSetting<Number>) value).setValue(numberValue.getValue().doubleValue() - Math.pow(10, -decimalPlaces));
                    }

                    valueY += textFont.FONT_HEIGHT + 3;
                } else if(value instanceof ColorValue) {
                    valueY += (textFont.FONT_HEIGHT + 3) * 9;
                }
            }
        }

        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void keyTyped(char key, int keyCode) throws IOException {
        if(this.currentlyBinding != null) {
            this.currentlyBinding.setValue(keyCode);
            this.currentlyBinding = null;
        } else if(this.currentlyTyping != null) {
            if(keyCode == Keyboard.KEY_ESCAPE)
                currentlyTyping = null;
            if (keyCode == Keyboard.KEY_BACK) {
                if (!currentlyTyping.getValue().equals("")) {
                    currentlyTyping.setValue(currentlyTyping.getValue().substring(0, currentlyTyping.getValue().length() - 1));
                }
            } else if (ChatAllowedCharacters.isAllowedCharacter(key)) {
                currentlyTyping.setValue(currentlyTyping.getValue() + key);
            }
        }
        super.keyTyped(key, keyCode);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                ClickGuiModule clickGuiModule = ModuleStorage.getModuleStorage().getByClass(ClickGuiModule.class);
                clickGuiModule.clickGuiScreen = null;
                clickGuiModule.setEnabled(false);
                mc.displayGuiScreen(null);
                clickGuiModule.setEnabled(true);
                break;
        }
    }

    public boolean doesGuiPauseGame()
    {
        return false;
    }

}
