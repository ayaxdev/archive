package ja.tabio.argon.screen.frames.impl;

import de.florianmichael.rclasses.common.array.ArrayUtils;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import imgui.type.ImString;
import ja.tabio.argon.interfaces.Category;
import ja.tabio.argon.interfaces.Identifiable;
import ja.tabio.argon.interfaces.Nameable;
import ja.tabio.argon.interfaces.Toggleable;
import ja.tabio.argon.setting.Setting;
import ja.tabio.argon.setting.impl.*;
import ja.tabio.argon.setting.interfaces.Settings;
import net.minecraft.client.MinecraftClient;

public class ImGuiFrame {

    private final Category category;
    private final float x;

    public Object expanded = null;
    public boolean closeOnNextFrame = false;

    public ImGuiFrame(Category category, float x) {
        this.category = category;
        this.x = x;
    }

    public void render() {
        final String categoryId = String.format("%s###%s-Window",
                category.getDisplayName(), category.getUniqueIdentifier());

        ImGui.setNextWindowPos(x, 23);
        ImGui.setNextWindowSize(200, MinecraftClient.getInstance().getWindow().getHeight() - 23 - 5);

        if (ImGui.begin(categoryId, ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove)) {
            for (Object object : category.get()) {
                if (!(object instanceof Nameable nameable))
                    continue;

                if (!(object instanceof Identifiable identifiable))
                    continue;

                final String menuItemId = String.format("%s###%s-MenuItem",
                        nameable.getDisplayName(), identifiable.getUniqueIdentifier());
                final String modalId = String.format("%s###%s-PopUpModal",
                        nameable.getDisplayName(), identifiable.getUniqueIdentifier());

                if (object instanceof Toggleable toggleable) {
                    final String enabledCheckId = String.format("###%s-EnabledCheck", identifiable.getUniqueIdentifier());
                    final ImBoolean enabledCheckBoolean = new ImBoolean(toggleable.isEnabled());

                    ImGui.checkbox(enabledCheckId, enabledCheckBoolean);
                    toggleable.setEnabled(enabledCheckBoolean.get());

                    ImGui.sameLine();
                }

                if (ImGui.button(menuItemId, ImGui.getContentRegionAvailX(), 20))
                    ImGui.openPopup(modalId);

                final ImVec2 center = ImGui.getMainViewport().getCenter();
                ImGui.setNextWindowPos(center.x - 300, center.y - 400);
                ImGui.setNextWindowSize(600, 800);

                modal: if (ImGui.beginPopupModal(modalId, ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoResize)) {
                    if (closeOnNextFrame) {
                        closeOnNextFrame = false;
                        ImGui.closeCurrentPopup();
                        expanded = null;
                        ImGui.endPopup();

                        break modal;
                    }

                    expanded = object;

                    final String childId = String.format("%s###%s-PopUpChild",
                            nameable.getDisplayName(), identifiable.getUniqueIdentifier());

                    if (ImGui.beginChild(childId, 600, 737)) {
                        if (object instanceof Toggleable toggleable) {
                            final String enabledButtonId = String.format("Enabled###%s-EnabledButton", identifiable.getUniqueIdentifier());

                            final ImBoolean enabled = new ImBoolean(toggleable.isEnabled());
                            ImGui.checkbox(enabledButtonId, enabled);
                            toggleable.setEnabled(enabled.get());

                            if (object instanceof Settings settings) {
                                for (Setting<?> setting : settings.getSettings()) {
                                    if (!setting.visibility())
                                        continue ;

                                    switch (setting) {
                                        case BooleanSetting booleanSetting -> {
                                            final ImBoolean value = new ImBoolean(booleanSetting.getValue());
                                            final String booleanSettingId = String.format("%s###%s-ValueButton",
                                                    booleanSetting.getDisplayName(), booleanSetting.getUniqueIdentifier());
                                            if (ImGui.checkbox(booleanSettingId, value))
                                                booleanSetting.setValue(value.get());
                                        }

                                        case NumberSetting numberSetting -> {
                                            final float[] value = new float[] {numberSetting.getValue()};
                                            ImGui.text(numberSetting.getDisplayName());
                                            final String numberSettingId = String.format("###%s-ValueSlider", numberSetting.getUniqueIdentifier());
                                            if (ImGui.sliderFloat(numberSettingId, value, numberSetting.minimum, numberSetting.maximum)) {
                                                numberSetting.setValue(value[0]);
                                            }
                                        }

                                        case ModeSetting modeSetting -> {
                                            final ImInt value = new ImInt(ArrayUtils.indexOf(modeSetting.modes, modeSetting.getValue()));
                                            ImGui.text(modeSetting.getDisplayName());
                                            final String modeSettingId = String.format("###%s-ValueCombo", modeSetting.getUniqueIdentifier());
                                            if (ImGui.combo(modeSettingId, value, modeSetting.modes)) {
                                                modeSetting.setValue(modeSetting.modes[value.get()]);
                                            }
                                        }

                                        case ColorSetting colorSetting -> {
                                            final String colorSettingId = String.format("%s###%s-ValueColor",
                                                    colorSetting.getDisplayName(), colorSetting.getUniqueIdentifier());

                                            final float[] data = new float[] {colorSetting.getValue().red() / 255f,
                                                    colorSetting.getValue().green() / 255f,
                                                    colorSetting.getValue().blue() / 255f,
                                                    colorSetting.getValue().alpha() / 255f};

                                            if (ImGui.colorPicker3(colorSettingId, data))
                                                colorSetting.setValue(new ColorSetting.ColorData(data[0], data[1], data[2], data[3]));
                                        }

                                        case MultiSetting multiSetting -> {
                                            final String multiSettingId = String.format("###%s-ValueMulti", multiSetting.getUniqueIdentifier());

                                            ImGui.text(multiSetting.getDisplayName());

                                            if (ImGui.beginCombo(multiSettingId, String.format("%d Enabled", multiSetting.getValue().size()))) {
                                                for (String mode : multiSetting.modes) {
                                                    if (ImGui.selectable(mode, multiSetting.isEnabled(mode))) {
                                                        multiSetting.toggle(mode);
                                                    }
                                                }
                                                ImGui.endCombo();
                                            }
                                        }

                                        case TextInputSetting textInputSetting -> {
                                            final String textInputSettingId = String.format("###%s-ValueText", textInputSetting.getUniqueIdentifier());
                                            final ImString imString = new ImString(textInputSetting.getValue());

                                            ImGui.text(textInputSetting.getDisplayName());

                                            if (ImGui.inputText(textInputSettingId, imString)) {
                                                textInputSetting.setValue(imString.get());
                                            }
                                        }

                                        default -> throw new UnsupportedOperationException("Unsupported setting");
                                    }
                                }
                            }
                        }
                    }

                    ImGui.endChild();

                    ImGui.separator();
                    if (ImGui.button("Cancel")) {
                        expanded = null;
                        ImGui.closeCurrentPopup();
                    }

                    ImGui.endPopup();
                }
            }
        }

        ImGui.end();
    }

}
