package ja.tabio.argon.screen.clickgui.simple;

import ja.tabio.argon.Argon;
import ja.tabio.argon.module.Module;
import ja.tabio.argon.setting.Setting;
import ja.tabio.argon.setting.impl.BooleanSetting;
import ja.tabio.argon.setting.impl.ModeSetting;
import ja.tabio.argon.setting.impl.MultiSetting;
import ja.tabio.argon.setting.impl.NumberSetting;
import kotlin.jvm.internal.Ref;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlider;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.List;

public class ModuleScreen extends GuiScreen implements Argon.IArgonAccess {

    private final Module module;
    private final GuiScreen lastScreen;

    private final List<Setting<?>> settings;

    public ModuleScreen(final Module module, final GuiScreen lastScreen) {
        this.module = module;
        this.lastScreen = lastScreen;

        this.settings = getSettingManager().settingMap.get(module.getSettingIdentifier());
    }

    @Override
    public void initGui() {
        this.buttonList.clear();

        buttonList.add(new GuiButton(-1, 5, 5, "Enabled: " + module.isEnabled()));

        int yCounter = 0;
        for (int i = 0; i < settings.size(); i++) {
            final Setting<?> setting = settings.get(i);

            switch (setting) {
                case BooleanSetting booleanSetting -> {
                    buttonList.add(new GuiButton(i, 5, 5 + 25 + yCounter * 25, booleanSetting.getName() + ": " + booleanSetting.getValue()));
                }

                case NumberSetting numberSetting -> {
                    buttonList.add(new GuiSlider(new GuiPageButtonList.GuiResponder() {
                        @Override
                        public void func_175321_a(int p_175321_1_, boolean p_175321_2_) { }

                        @Override
                        public void func_175319_a(int p_175319_1_, String p_175319_2_) { }

                        @Override
                        public void onTick(int id, float value) {
                            numberSetting.setValue(value);
                        }
                    }, i, 5, 5 + 25 + i * 25, 200, 20,
                            numberSetting.getName(), numberSetting.minimum, numberSetting.maximum, numberSetting.value,
                            (id, name, value) -> name + ": " + numberSetting.getValue()));
                }

                case ModeSetting modeSetting -> {
                    buttonList.add(new GuiButton(i, 5, 5 + 25 + yCounter * 25, modeSetting.getName() + ": " + modeSetting.getValue()));
                }

                case MultiSetting multiSetting -> {
                    for (int c = 0; c < multiSetting.settings.size(); c++) {
                        final BooleanSetting booleanSetting = multiSetting.settings.get(c);
                        buttonList.add(new GuiButton(Integer.parseInt(Integer.toString(i) + c), 5, 5 + 25 + yCounter * 25, multiSetting.getName() + ": " + booleanSetting.getName() + ": " + booleanSetting.getValue()));
                        yCounter++;
                    }
                    yCounter--;
                }

                default -> throw new UnsupportedOperationException("Unknown setting type");
            }

            yCounter++;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void actionPerformed(GuiButton guiButton) {
        if (guiButton.id == -1) {
            module.setEnabled(!module.isEnabled());
            guiButton.displayString = "Enabled: " + module.isEnabled();

            return;
        }

        for (int i = 0; i < settings.size(); i++) {
            final Setting<?> setting = settings.get(i);

            switch (setting) {
                case BooleanSetting booleanSetting -> {
                    if (guiButton.id == i) {
                        booleanSetting.setValue(!booleanSetting.getValue());
                        guiButton.displayString = booleanSetting.getDisplayName() + ": " + booleanSetting.getValue();
                    }
                }

                case ModeSetting modeSetting -> {
                    if (guiButton.id == i) {
                        int nextModeIndex = ArrayUtils.indexOf(modeSetting.modes, modeSetting.getValue()) + 1;
                        if (nextModeIndex > modeSetting.modes.length - 1)
                            nextModeIndex = 0;

                        modeSetting.setValue(modeSetting.modes[nextModeIndex]);
                        guiButton.displayString = modeSetting.getDisplayName() + ": " + modeSetting.getValue();
                    }
                }

                case MultiSetting multiSetting -> {
                    for (int c = 0; c < multiSetting.settings.size(); c++) {
                        final BooleanSetting booleanSetting = multiSetting.settings.get(c);

                        if (guiButton.id == Integer.parseInt(Integer.toString(i) + c)) {
                            booleanSetting.setValue(!booleanSetting.getValue());
                            guiButton.displayString = multiSetting.getDisplayName() + ": " + booleanSetting.getDisplayName() + ": " + booleanSetting.getValue();
                        }
                    }
                }

                case NumberSetting numberSetting -> {
                    // Handled by gui slider
                }

                default -> throw new UnsupportedOperationException("Unsupported setting type, please report this to the developers.");
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void keyTyped(char key, int code) throws IOException {
        if (key == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(lastScreen);

            return;
        }

        super.keyTyped(key, code);
    }
}
