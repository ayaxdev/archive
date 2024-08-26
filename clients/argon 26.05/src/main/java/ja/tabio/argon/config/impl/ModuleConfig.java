package ja.tabio.argon.config.impl;

import com.google.gson.JsonObject;
import ja.tabio.argon.Argon;
import ja.tabio.argon.config.Config;
import ja.tabio.argon.config.annotation.RegisterConfig;

@RegisterConfig
public class ModuleConfig extends Config {

    public ModuleConfig() {
        super("modules", true);
    }

    @Override
    protected JsonObject get() {
        final JsonObject modulesObject = new JsonObject();

        Argon.getInstance().moduleManager.moduleMap.values().forEach(module -> {
            final JsonObject moduleObject = new JsonObject();

            moduleObject.addProperty("enabled", module.isEnabled());
            moduleObject.addProperty("key", module.key);

            modulesObject.add(module.getUniqueIdentifier(), moduleObject);
        });

        return modulesObject;
    }

    @Override
    protected void set(JsonObject jsonObject) {
        Argon.getInstance().moduleManager.moduleMap.values().forEach(module -> {
            if (!jsonObject.has(module.getUniqueIdentifier()))
                return;

            final JsonObject moduleObject = jsonObject.getAsJsonObject(module.getUniqueIdentifier());

            module.setEnabled(moduleObject.get("enabled").getAsBoolean());
            module.key = moduleObject.get("key").getAsInt();
        });
    }
}
