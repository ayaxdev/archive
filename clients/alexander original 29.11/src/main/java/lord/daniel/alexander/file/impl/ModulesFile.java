package lord.daniel.alexander.file.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lord.daniel.alexander.file.abstracts.LocalFile;
import lord.daniel.alexander.file.data.FileData;
import lord.daniel.alexander.module.abstracts.AbstractModule;
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
            modulesObject.add(module.getIdentifier(), module.save());

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
                if (modulesObject.has(module.getIdentifier()))
                    module.load(modulesObject.getAsJsonObject(module.getIdentifier()));
            }
        }
    }

}