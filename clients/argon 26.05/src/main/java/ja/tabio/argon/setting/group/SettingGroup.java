package ja.tabio.argon.setting.group;

import ja.tabio.argon.interfaces.Identifiable;
import ja.tabio.argon.interfaces.Nameable;
import ja.tabio.argon.setting.Setting;
import ja.tabio.argon.setting.interfaces.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SettingGroup implements Nameable, Identifiable, Settings {

    public final List<Supplier<Boolean>> visibility = new ArrayList<>();

    public final String name;
    public final Identifiable parent;

    public SettingGroup(String name, Identifiable parent) {
        this.name = name;
        this.parent = parent;
    }

    @SuppressWarnings("unchecked")
    public <T extends SettingGroup> T  visibility(Supplier<Boolean> supplier) {
        visibility.add(supplier);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends SettingGroup> T visibility(Setting<?> testSetting, Object value) {
        visibility.add(() -> testSetting.getValue().equals(value));
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends SettingGroup> T  visibility(Setting<?> testSetting) {
        visibility.add(() -> testSetting.getValue().equals(true));
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends SettingGroup> T  visibilityN(Supplier<Boolean> supplier) {
        visibility.add(() -> !supplier.get());
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends SettingGroup> T visibilityN(Setting<?> testSetting, Object value) {
        visibility.add(() -> !testSetting.getValue().equals(value));
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends SettingGroup> T  visibilityN(Setting<?> testSetting) {
        visibility.add(() -> testSetting.getValue().equals(false));
        return (T) this;
    }

    public void updateSettings(List<Setting<?>> settings) {
        for (Setting<?> setting : settings) {
            setting.visibility(() -> {
                for (Supplier<Boolean> supplier : visibility)
                    if (!supplier.get())
                        return false;

                return true;
            });
        }
    }

    public void updateSettings() {
        updateSettings(getSettings());
    }

    @Override
    public String getUniqueIdentifier() {
        return String.format("SettingGroup-%s-%s", parent.getUniqueIdentifier(), getName());
    }

    @Override
    public String getName() {
        return name;
    }
}
