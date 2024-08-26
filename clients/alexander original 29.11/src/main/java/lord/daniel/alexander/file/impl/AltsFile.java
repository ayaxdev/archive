package lord.daniel.alexander.file.impl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lord.daniel.alexander.draggable.Draggable;
import lord.daniel.alexander.file.abstracts.LocalFile;
import lord.daniel.alexander.file.data.FileData;
import lord.daniel.alexander.storage.impl.AltStorage;
import lord.daniel.alexander.storage.impl.DraggableStorage;

import java.util.Map;
import java.util.Set;

/**
 * Written by Daniel. on 07/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
@FileData(fileName = "alts")
public class AltsFile extends LocalFile {

    @Override
    public void save(Gson gson) {
        JsonObject object = new JsonObject();

        JsonObject altsObject = new JsonObject();

        for (AltStorage.Account account : AltStorage.getAltStorage().getList()) {
            String[] encrypted = account.getEncrypted();

            JsonObject altObject = new JsonObject();
            altObject.addProperty("pass", encrypted[1]);

            altsObject.add(encrypted[0], altObject);
        }

        object.add("Alts", altsObject);

        writeFile(gson.toJson(object), file);
    }

    @Override
    public void load(Gson gson) {
        if (!file.exists()) {
            return;
        }

        JsonObject object = gson.fromJson(readFile(file), JsonObject.class);
        if (object.has("Alts")){
            JsonObject altsObject = object.getAsJsonObject("Alts");
            Set<Map.Entry<String, JsonElement>> entries = altsObject.entrySet();

            for(Map.Entry<String, JsonElement> entry : entries) {
                JsonObject altObject = entry.getValue().getAsJsonObject();

                AltStorage.getAltStorage().add(entry.getKey(), altObject.get("pass").getAsString());
            }
        }
    }

}
