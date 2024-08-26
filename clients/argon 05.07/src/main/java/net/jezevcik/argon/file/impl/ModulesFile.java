package net.jezevcik.argon.file.impl;

import com.alibaba.fastjson2.JSONObject;
import net.jezevcik.argon.ParekClient;
import net.jezevcik.argon.config.interfaces.ConfigEntry;
import net.jezevcik.argon.file.DataFile;
import net.jezevcik.argon.file.interfaces.Savable;
import net.jezevcik.argon.module.Module;
import net.jezevcik.argon.system.identifier.IdentifierType;
import net.jezevcik.argon.system.toggle.RedundantCallException;

public class ModulesFile extends DataFile {

    public ModulesFile() {
        super("modules");
    }

    @Override
    public JSONObject getData() {
        final JSONObject outputObject = new JSONObject();

        for (final Module module : ParekClient.getInstance().modules) {
            final JSONObject moduleObject = new JSONObject();

            moduleObject.put("enabled", module.isEnabled());
            moduleObject.put("key", module.key);

            try {
                moduleObject.put("config", module.config.getData());
            } catch (Exception e) {
                ParekClient.LOGGER.error("Failed to save config data of {}", module.moduleParams.name(), e);
            }

            outputObject.put(module.getIdentifier(IdentifierType.UNIQUE_NORMAL), moduleObject);
        }

        return outputObject;
    }

    @Override
    public void setData(JSONObject object) {
        for (final Module module : ParekClient.getInstance().modules) {
            final String moduleIdentifier = module.getIdentifier(IdentifierType.UNIQUE_NORMAL);

            if (!object.containsKey(moduleIdentifier))
                continue;

            final JSONObject moduleObject = object.getJSONObject(moduleIdentifier);

            if (moduleObject.containsKey("enabled")) {
                try {
                    module.setEnabled(moduleObject.getBooleanValue("enabled"));
                } catch (RedundantCallException ignored) { }
            }

            if (moduleObject.containsKey("key"))
                module.key = moduleObject.getIntValue("key");

            if (!moduleObject.containsKey("config"))
                continue;

            final JSONObject configObject = moduleObject.getJSONObject("config");

            module.config.setData(configObject);
        }
    }

}
