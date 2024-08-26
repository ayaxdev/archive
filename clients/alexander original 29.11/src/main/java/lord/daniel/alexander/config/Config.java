package lord.daniel.alexander.config;

import java.io.File;
import java.io.IOException;

import com.google.gson.JsonObject;
import lombok.Getter;
import lord.daniel.alexander.Modification;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.storage.impl.ModuleStorage;

@Getter
public final class Config {

    private final String name;
    private final File file;

    public Config(String name) {
        this.name = name;
        this.file = new File(Modification.INSTANCE.getConfigDir(), name);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException exception) {
                throw new RuntimeException(exception.getMessage());
            }
        }
    }

    public JsonObject save() {
        JsonObject jsonObject = new JsonObject();
        JsonObject modulesObject = new JsonObject();

        for (AbstractModule module : ModuleStorage.getModuleStorage().getList())
            if(module.isSaveToConfig())
                modulesObject.add(module.getName(), module.save());

        jsonObject.add("Modules", modulesObject);

        return jsonObject;
    }

    public void load(JsonObject object) {
        if (object.has("Modules")) {
            JsonObject modulesObject = object.getAsJsonObject("Modules");

            for (AbstractModule module : ModuleStorage.getModuleStorage().getList()) {
                if(!module.isLoadToConfig())
                    continue;

                if (modulesObject.has(module.getName()))
                    module.load(modulesObject.getAsJsonObject(module.getName()));
            }
        }
    }

    public void load(JsonObject object, AbstractModule abstractModule) {
        if (object.has("Modules")) {
            JsonObject modulesObject = object.getAsJsonObject("Modules");

            if (modulesObject.has(abstractModule.getName()))
                abstractModule.load(modulesObject.getAsJsonObject(abstractModule.getName()));
        }
    }
}
