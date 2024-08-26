package ja.tabio.argon.setting.manager;

import ja.tabio.argon.interfaces.IClientInitializeable;
import ja.tabio.argon.setting.Setting;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SettingManager implements IClientInitializeable {

    public final Map<String, List<Setting<?>>> settingMap = new LinkedHashMap<>();

    @Override
    public void init() {

    }

    @Override
    public void start() {

    }

    public void register(String o) {
        settingMap.put(o, new LinkedList<>());
    }

    public void addSetting(String o, Setting<?> setting) {
        if (settingMap.containsKey(o)) {
            settingMap.get(o).add(setting);
        } else {
            final List<Setting<?>> settings = new LinkedList<>();
            settings.add(setting);

            settingMap.put(o, settings);
        }
    }

}
