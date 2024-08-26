package ja.tabio.argon.config.impl;

import com.alibaba.fastjson2.JSONObject;
import ja.tabio.argon.config.Config;
import ja.tabio.argon.config.annotation.ConfigData;
import ja.tabio.argon.module.Module;

@ConfigData(name = "Modules")
public class ModulesConfig extends Config {

    @Override
    public JSONObject serialize() {
        final JSONObject jsonObject = new JSONObject();

        for (Module module : getModuleManager().moduleMap.values()) {
            jsonObject.put(module.name, module.serialize());
        }

        return jsonObject;
    }

    @Override
    public void deserialize(JSONObject jsonObject) {
        for (Module module : getModuleManager().moduleMap.values()) {
            if (jsonObject.containsKey(module.name)) {
                final JSONObject moduleJson = jsonObject.getJSONObject(module.name);
                module.deserialize(moduleJson);
            }
        }
    }
}
