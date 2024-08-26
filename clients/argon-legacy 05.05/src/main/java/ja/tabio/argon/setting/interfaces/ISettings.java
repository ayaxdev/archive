package ja.tabio.argon.setting.interfaces;

import ja.tabio.argon.Argon;
import ja.tabio.argon.interfaces.INameable;
import ja.tabio.argon.setting.Setting;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ISettings extends Argon.IArgonAccess {

    default void register() {
        getSettingManager().register(getSettingIdentifier());
    }

    default void addSetting(Setting<?> setting) {
        getSettingManager().addSetting(getSettingIdentifier(), setting);
        setting.owner = this;
        setting.postInit();
    }

    default List<Setting<?>> getSettings() {
        return getSettingManager().settingMap.get(getSettingIdentifier());
    }

    String getSettingIdentifier();

}
