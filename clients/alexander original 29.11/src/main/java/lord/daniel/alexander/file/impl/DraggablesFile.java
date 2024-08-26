package lord.daniel.alexander.file.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lord.daniel.alexander.draggable.Draggable;
import lord.daniel.alexander.file.abstracts.LocalFile;
import lord.daniel.alexander.file.data.FileData;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.storage.impl.DraggableStorage;
import lord.daniel.alexander.storage.impl.ModuleStorage;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@FileData(fileName = "draggables")
public class DraggablesFile extends LocalFile {

    @Override
    public void save(Gson gson) {
        JsonObject object = new JsonObject();

        JsonObject draggablesObject = new JsonObject();

        for (Draggable draggable : DraggableStorage.getDraggableStorage().getList())
            draggablesObject.add(draggable.getName(), draggable.save());

        object.add("Draggables", draggablesObject);

        writeFile(gson.toJson(object), file);
    }

    @Override
    public void load(Gson gson) {
        if (!file.exists()) {
            return;
        }

        JsonObject object = gson.fromJson(readFile(file), JsonObject.class);
        if (object.has("Draggables")){
            JsonObject draggablesObject = object.getAsJsonObject("Draggables");

            for (Draggable draggable : DraggableStorage.getDraggableStorage().getList()) {
                if (draggablesObject.has(draggable.getName()))
                    draggable.load(draggablesObject.getAsJsonObject(draggable.getName()));
            }
        }
    }

}