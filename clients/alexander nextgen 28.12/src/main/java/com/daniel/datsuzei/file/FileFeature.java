package com.daniel.datsuzei.file;

import com.daniel.datsuzei.DatsuzeiClient;
import com.daniel.datsuzei.feature.Feature;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;

import java.io.*;

@Getter
public abstract class FileFeature implements Feature {

    private final String name;
    private final File savedFile;

    public FileFeature(String name) {
        this.name = name;

        this.savedFile = new File(DatsuzeiClient.getSingleton().getDirectory(), STR."\{name}.json");
    }

    public void save(Gson gson) {
        JsonObject mainObject = new JsonObject();
        mainObject.add(getName(), serializeFeature());
        writeFile(gson.toJson(mainObject), savedFile);
    }

    public void load(Gson gson) {
        if (!savedFile.exists() || savedFile.isDirectory()) {
            return;
        }

        try {
            JsonObject object = gson.fromJson(readFile(savedFile), JsonObject.class);
            if (object.has(getName())) {
                deserializeFeature(object.getAsJsonObject(getName()));
            }
        } catch (JsonSyntaxException e) {
            DatsuzeiClient.getSingleton().getLogger().error(STR."File \{getName()} is not a valid json:", e);
        }
    }

    private void writeFile(String content, File file) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(content);
        } catch (IOException e) {
            DatsuzeiClient.getSingleton().getLogger().error(STR."Failed to write file \{getName()}:", e);
        }
    }

    private String readFile(File file) {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            DatsuzeiClient.getSingleton().getLogger().error(STR."Failed to read file \{getName()}:", e);
        }
        return builder.toString();
    }

}
