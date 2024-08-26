package ja.tabio.argon.screen.clickgui.hero.window;

import de.florianmichael.rclasses.math.MathUtils;
import de.florianmichael.rclasses.math.integration.Boundings;
import ja.tabio.argon.interfaces.IMinecraft;
import ja.tabio.argon.setting.Setting;
import ja.tabio.argon.setting.impl.BooleanSetting;
import ja.tabio.argon.setting.impl.ModeSetting;
import ja.tabio.argon.setting.impl.MultiSetting;
import ja.tabio.argon.setting.impl.NumberSetting;
import ja.tabio.argon.setting.interfaces.ISettings;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class HeroWindow implements IMinecraft {

    public final ISettings settingsObject;

    private final List<ModeSetting> expandedModes = new LinkedList<>();
    private final List<MultiSetting> expandedMultis = new LinkedList<>();

    public float x, y;
    public float width, height;

    public HeroWindow(final ISettings settingsObject, float x, float y) {
        this.settingsObject = settingsObject;

        this.x = x;
        this.y = y;
    }

    public void draw(int mouseX, int mouseY) {
        width = 50;
        height = 0;

        for (Setting<?> setting : settingsObject.getSettings()) {
            if (!setting.visibility.get())
                continue;

            switch (setting) {
                case BooleanSetting booleanSetting -> {
                    final float settingWidth = mc.fontRendererObj.getStringWidth(booleanSetting.getDisplayName()) + 13 + 2;

                    width = Math.max(width, settingWidth);
                    height += 15;
                }

                case NumberSetting numberSetting -> {
                    final float settingWidth = mc.fontRendererObj.getStringWidth(numberSetting.getDisplayName())
                            + mc.fontRendererObj.getStringWidth(numberSetting.getRenderValue())
                            + 3;

                    width = Math.max(width, settingWidth);
                    height += 15;
                }

                case ModeSetting modeSetting -> {
                    float settingWidth = mc.fontRendererObj.getStringWidth(modeSetting.getDisplayName()) + 5;

                    if (expandedModes.contains(modeSetting)) {
                        height += 15 * modeSetting.modes.length;

                        for (String s : modeSetting.modes) {
                            settingWidth = Math.max(mc.fontRendererObj.getStringWidth(s) + 5, settingWidth);
                        }
                    }

                    width = Math.max(width, settingWidth);
                    height += 15;
                }

                case MultiSetting multiSetting-> {
                    float settingWidth = mc.fontRendererObj.getStringWidth(multiSetting.getDisplayName()) + 5;

                    if (expandedMultis.contains(multiSetting)) {
                        height += 15 * multiSetting.settings.size();

                        for (BooleanSetting s : multiSetting.settings) {
                            settingWidth = Math.max(mc.fontRendererObj.getStringWidth(s.getDisplayName()) + 5, settingWidth);
                        }
                    }

                    width = Math.max(width, settingWidth);
                    height += 15;
                }

                default ->
                        throw new UnsupportedOperationException("Unsupported setting type, please report this to the developers.");
            }
        }

        Gui.drawRect(x, y, x + width, y + height, -15066598);

        float settingY = y;

        for (Setting<?> setting : settingsObject.getSettings()) {
            if (!setting.visibility.get())
                continue;

            switch (setting) {
                case BooleanSetting booleanSetting -> {
                    final String text = booleanSetting.getDisplayName();
                    mc.fontRendererObj.drawString(text, x + width - mc.fontRendererObj.getStringWidth(text), settingY + 15 / 2f - mc.fontRendererObj.FONT_HEIGHT / 2f, -1);

                    Gui.drawRect(x + 1, settingY + 2, x + 12, settingY + 13, booleanSetting.getValue() ? new Color(255, 26, 42).getRGB() : -16777216);

                    settingY += 15;
                }

                case NumberSetting numberSetting -> {
                    mc.fontRendererObj.drawString(numberSetting.getDisplayName(), x + 1, settingY + 15 / 2f - mc.fontRendererObj.FONT_HEIGHT / 2f, -1);
                    mc.fontRendererObj.drawString(numberSetting.getRenderValue(), x + width - mc.fontRendererObj.getStringWidth(numberSetting.getRenderValue()), settingY + 15 / 2f - mc.fontRendererObj.FONT_HEIGHT / 2f, -1);
                    Gui.drawRect(x, settingY + 15 - 2, x + MathHelper.floor_double((numberSetting.getValue() - numberSetting.minimum) / (numberSetting.maximum - numberSetting.minimum) * width),
                            settingY + 15, new Color(255, 26, 42, Boundings.isInBoundsAbsolute(mouseX, mouseY, x, settingY + 15, x + width, settingY + 15) ? 250 : 200).getRGB());

                    if (Mouse.isButtonDown(0) && Boundings.isInBoundsAbsolute(mouseX, mouseY, x - 5, settingY + 15 - 5, x + width + 5, settingY + 15)) {
                        float percentage = mouseX - x - 1f;
                        percentage /= width;
                        percentage = Math.max(Math.min(percentage, 1), 0);

                        numberSetting.setValue((numberSetting.minimum + (numberSetting.maximum - numberSetting.minimum) * percentage));
                        numberSetting.setValue(MathUtils.roundAvoid(numberSetting.getValue(), numberSetting.decimals));
                    }

                    settingY += 15;
                }

                case MultiSetting multiSetting -> {
                    mc.fontRendererObj.drawString(multiSetting.getDisplayName(), x + width / 2 - mc.fontRendererObj.getStringWidth(multiSetting.getDisplayName()) / 2f, settingY + 15 / 2f - mc.fontRendererObj.FONT_HEIGHT / 2f, -1);
                    Gui.drawRect(x, settingY + 15 - 1.5f, x + width, settingY + 15, 1996488704);

                    settingY += 15;

                    if (expandedMultis.contains(multiSetting)) {
                        for (BooleanSetting entry : multiSetting.settings) {
                            Gui.drawRect(x, settingY, x + width, settingY + 15, -1441656302);
                            mc.fontRendererObj.drawString(entry.getDisplayName(), x + width / 2 - mc.fontRendererObj.getStringWidth(entry.getDisplayName()) / 2f, settingY + 15 / 2f - mc.fontRendererObj.FONT_HEIGHT / 2f, -1);

                            if (entry.getValue()) {
                                Gui.drawRect(x, settingY + 2, x + 1.5f, settingY + 15 - 2, new Color(255, 26, 42).getRGB());
                            }

                            if(Boundings.isInBounds(mouseX, mouseY, x, settingY, width, 15)) {
                                Gui.drawRect(x + width - 1.5f, settingY + 2, x + width, settingY + 15 - 2, new Color(255, 26, 42, 150).getRGB());
                            }

                            settingY += 15;
                        }
                    }
                }

                case ModeSetting modeSetting -> {
                    mc.fontRendererObj.drawString(modeSetting.getDisplayName(), x + width / 2 - mc.fontRendererObj.getStringWidth(modeSetting.getDisplayName()) / 2f, settingY + 15 / 2f - mc.fontRendererObj.FONT_HEIGHT / 2f, -1);
                    Gui.drawRect(x, settingY + 15 - 1.5f, x + width, settingY + 15, 1996488704);

                    settingY += 15;

                    if (expandedModes.contains(modeSetting)) {
                        for (String mode : modeSetting.modes) {
                            Gui.drawRect(x, settingY, x + width, settingY + 15, -1441656302);
                            mc.fontRendererObj.drawString(mode, x + width / 2 - mc.fontRendererObj.getStringWidth(mode) / 2f, settingY + 15 / 2f - mc.fontRendererObj.FONT_HEIGHT / 2f, -1);

                            if (modeSetting.getValue().equalsIgnoreCase(mode)) {
                                Gui.drawRect(x, settingY + 2, x + 1.5f, settingY + 15 - 2, new Color(255, 26, 42).getRGB());
                            }

                            if(Boundings.isInBounds(mouseX, mouseY, x, settingY, width, 15)) {
                                Gui.drawRect(x + width - 1.5f, settingY + 2, x + width, settingY + 15 - 2, new Color(255, 26, 42, 150).getRGB());
                            }

                            settingY += 15;
                        }
                    }
                }

                default ->
                        throw new UnsupportedOperationException("Unsupported setting type, please report this to the developers.");
            }
        }
    }

    public void mouseClick(int mouseX, int mouseY, int button) {
        float settingY = y;

        for (Setting<?> setting : settingsObject.getSettings()) {
            if (!setting.visibility.get())
                continue;

            switch (setting) {
                case BooleanSetting booleanSetting -> {
                    if (Boundings.isInBoundsAbsolute(mouseX, mouseY, x + 1, settingY + 2, x + 12, settingY + 13))
                        booleanSetting.setValue(!booleanSetting.getValue());

                    settingY += 15;
                }

                case NumberSetting numberSetting -> {
                    settingY += 15;
                }

                case ModeSetting modeSetting -> {
                    if (Boundings.isInBoundsAbsolute(mouseX, mouseY, x, settingY, x + width, settingY + 15))
                        if (expandedModes.contains(modeSetting))
                            expandedModes.remove(modeSetting);
                        else
                            expandedModes.add(modeSetting);

                    settingY += 15;

                    if (expandedModes.contains(modeSetting)) {
                        for (String mode : modeSetting.modes) {
                            if(Boundings.isInBounds(mouseX, mouseY, x, settingY, width, 15)) {
                                modeSetting.setValue(mode);
                                break;
                            }

                            settingY += 15;
                        }
                    }
                }

                case MultiSetting multiSetting -> {
                    if (Boundings.isInBoundsAbsolute(mouseX, mouseY, x, settingY, x + width, settingY + 15))
                        if (expandedMultis.contains(multiSetting))
                            expandedMultis.remove(multiSetting);
                        else
                            expandedMultis.add(multiSetting);

                    settingY += 15;

                    if (expandedMultis.contains(multiSetting)) {
                        for (BooleanSetting booleanSetting : multiSetting.settings) {
                            if(Boundings.isInBounds(mouseX, mouseY, x, settingY, width, 15)) {
                                booleanSetting.setValue(!booleanSetting.getValue());
                                break;
                            }

                            settingY += 15;
                        }
                    }
                }

                default ->
                        throw new UnsupportedOperationException("Unsupported setting type, please report this to the developers.");
            }
        }
    }


}
