package ja.tabio.argon.setting.interfaces;

import ja.tabio.argon.Argon;
import ja.tabio.argon.interfaces.Identifiable;
import ja.tabio.argon.interfaces.Nameable;
import ja.tabio.argon.setting.Setting;

import java.util.List;

public interface Settings {

    default void register() {
        Argon.getInstance().settingManager.register(getSettingIdentifier());
    }

    default void addSetting(Setting<?> setting) {
        Argon.getInstance().settingManager.addSetting(getSettingIdentifier(), setting);
    }

    default List<Setting<?>> getSettings() {
        return Argon.getInstance().settingManager.settingMap.get(getSettingIdentifier());
    }

    default void register(Object identifier) {
        Argon.getInstance().settingManager.register(identifier);
    }

    default void addSetting(Object identifier, Setting<?> setting) {
        Argon.getInstance().settingManager.addSetting(identifier, setting);
    }

    default List<Setting<?>> getSettings(Object identifier) {
        return Argon.getInstance().settingManager.settingMap.get(identifier);
    }
    
    default Object getSettingIdentifier() {
        return switch (this) {
            case Identifiable identifiable -> identifiable.getUniqueIdentifier();
            case Nameable nameable -> nameable.getName();
            default -> this;
        };
    }

}
