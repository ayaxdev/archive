package lord.daniel.alexander.file.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lord.daniel.alexander.file.abstracts.LocalFile;
import lord.daniel.alexander.file.data.FileData;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.settings.AbstractSetting;
import lord.daniel.alexander.storage.impl.ModuleStorage;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@FileData(fileName = "modules")
public class ModulesFile extends LocalFile {

    @Override
    public void save(Gson gson) {
        JsonObject object = new JsonObject();

        JsonObject modulesObject = new JsonObject();

        for (AbstractModule module : ModuleStorage.getModuleStorage().getList())
            modulesObject.add(module.getName(), save(module));

        object.add("Modules", modulesObject);

        writeFile(gson.toJson(object), file);
    }

    @Override
    public void load(Gson gson) {
        if (!file.exists()) {
            return;
        }

        JsonObject object = gson.fromJson(readFile(file), JsonObject.class);
        if (object.has("Modules")){
            JsonObject modulesObject = object.getAsJsonObject("Modules");

            for (AbstractModule module : ModuleStorage.getModuleStorage().getList()) {
                if (modulesObject.has(module.getName()))
                    load(module, modulesObject.getAsJsonObject(module.getName()));
            }
        }
    }

    public JsonObject save(AbstractModule abstractModule) {
        JsonObject object = new JsonObject();
        object.addProperty("Enabled", abstractModule.isEnabled());
        if (!abstractModule.getSettings().isEmpty()) {
            JsonObject propertiesObject = new JsonObject();
            for (AbstractSetting<?> property : abstractModule.getSettings()) {
                if(property.getValueAsString() == null)
                    continue;

                propertiesObject.addProperty(property.getName(), property.getValueAsString());
            }
            object.add("Values", propertiesObject);
        }
        return object;
    }

    public void load(AbstractModule abstractModule, JsonObject object) {
        try {
            if (object.has("Enabled"))
                abstractModule.setEnabled(object.get("Enabled").getAsBoolean());
        } catch (Exception e) {
            // Ignored
        }
        if (object.has("Values") && !abstractModule.getSettings().isEmpty()) {
            JsonObject propertiesObject = object.getAsJsonObject("Values");
            for (AbstractSetting<?> property : abstractModule.getSettings()) {
                if (propertiesObject.has(property.getName())) {
                    property.setValueByString(propertiesObject.get(property.getName()).getAsString());
                }
            }
        }
    }

}