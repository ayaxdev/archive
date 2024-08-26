package ja.tabio.argon.config.impl;

import com.alibaba.fastjson2.JSONObject;
import ja.tabio.argon.config.Config;
import ja.tabio.argon.config.annotation.ConfigData;
import ja.tabio.argon.setting.Setting;

import java.util.List;
import java.util.Map;

@ConfigData(name = "Settings")
public class SettingsConfig extends Config {

    @Override
    public JSONObject serialize() {
        final JSONObject jsonObject = new JSONObject();

        for (Map.Entry<String, List<Setting<?>>> entry : getSettingManager().settingMap.entrySet()) {
            final JSONObject entryObject = new JSONObject();

            for (Setting<?> setting : entry.getValue()) {
                entryObject.put(setting.name, setting.serialize());
            }

            jsonObject.put(entry.getKey(), entryObject);
        }

        return jsonObject;
    }

    @Override
    public void deserialize(JSONObject jsonObject) {
        for (Map.Entry<String, List<Setting<?>>> entry : getSettingManager().settingMap.entrySet()) {
            if (jsonObject.containsKey(entry.getKey())) {
                final JSONObject entryObject = jsonObject.getJSONObject(entry.getKey());

                for (Setting<?> setting : entry.getValue()) {
                    if (entryObject.containsKey(setting.getName())) {
                        final JSONObject settingObject = entryObject.getJSONObject(setting.getName());
                        setting.deserialize(settingObject);
                    }
                }
            }
        }
    }

}
