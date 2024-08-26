package ja.tabio.argon.setting.manager;

import ja.tabio.argon.interfaces.Initializable;
import ja.tabio.argon.setting.Setting;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SettingManager implements Initializable {

    public final Map<Object, List<Setting<?>>> settingMap = new LinkedHashMap<>();

    @Override
    public void init(final String[] args) {

    }

    @Override
    public void start() {

    }

    public void register(Object o) {
        settingMap.put(o, new LinkedList<>());
    }

    public void addSetting(Object o, Setting<?> setting) {
        if (settingMap.containsKey(o)) {
            settingMap.get(o).add(setting);
        } else {
            final List<Setting<?>> settings = new LinkedList<>();
            settings.add(setting);

            settingMap.put(o, settings);
        }
    }

}
