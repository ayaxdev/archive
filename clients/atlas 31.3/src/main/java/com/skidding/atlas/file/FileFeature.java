package com.skidding.atlas.file;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.skidding.atlas.AtlasClient;
import com.skidding.atlas.feature.Feature;
import com.skidding.atlas.util.encryption.EncryptionUtil;
import com.skidding.atlas.util.java.object.EnumUtil;
import lombok.Getter;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;

@Getter
public abstract class FileFeature implements Feature {

    private final String name;
    private final File savedFile, oldVersionFile;
    private final EncryptionUtil.KeyLevel keyLevel;

    public FileFeature(String name, EncryptionUtil.KeyLevel keyLevel) {
        this.name = name;
        this.keyLevel = keyLevel;

        this.savedFile = new File(AtlasClient.getInstance().directory, STR."\{name}.data");
        this.oldVersionFile = new File(AtlasClient.getInstance().directory, STR."\{name}.json");
    }

    public void save(Gson gson) {
        if(savedFile.exists() && savedFile.isDirectory()) {
            try {
                FileUtils.deleteDirectory(savedFile);
            } catch (IOException e) {
                throw new RuntimeException(STR."Failed to delete directory in place of \{savedFile.getAbsolutePath()}", e);
            }
        }

        JsonObject mainObject = new JsonObject();
        mainObject.add(getName(), serialize());

        boolean saved = false;

        if(keyLevel != null) {
            try {
                writeFile(STR."\{keyLevel.name}<-:::->\{EncryptionUtil.INSTANCE.encrypt(gson.toJson(mainObject), keyLevel)}", savedFile);
                saved = true;
            } catch (Exception e) {
                AtlasClient.getInstance().logger.error("Failed to encrypt file, saving as plain text", e);
            }
        }

        if(!saved)
            writeFile(gson.toJson(mainObject), savedFile);
    }

    public void load(Gson gson) {
        if (!savedFile.exists() && !oldVersionFile.exists()) {
            return;
        }

        final File loadFile = savedFile.exists() ? savedFile : oldVersionFile;

        if(loadFile == oldVersionFile)
            AtlasClient.getInstance().logger.warn(STR."Loading legacy file \{loadFile.getAbsolutePath()}");

        if(!loadFile.exists()) {
            return;
        }

        if(loadFile.isDirectory()) {
            try {
                FileUtils.deleteDirectory(loadFile);
            } catch (IOException e) {
                throw new RuntimeException(STR."Failed to delete directory in place of \{savedFile.getAbsolutePath()}", e);
            }
        }

        try {
            String toParse = readFile(loadFile);

            if(toParse.contains("<-:::->")) {
                final String[] split = toParse.split("<-:::->");
                final EncryptionUtil.KeyLevel keyLevel = EnumUtil.getEnumConstantBasedOnString(EncryptionUtil.KeyLevel.class, split[0]);
                try {
                    toParse = EncryptionUtil.INSTANCE.decrypt(split[1], keyLevel);
                } catch (Exception e) {
                    AtlasClient.getInstance().logger.error(STR."Failed to parse encrypted file \{loadFile.getName()}", e);
                }
            }

            JsonObject object = gson.fromJson(toParse, JsonObject.class);
            if (object.has(getName())) {
                deserialize(object.getAsJsonObject(getName()));
            }
        } catch (JsonSyntaxException e) {
            AtlasClient.getInstance().logger.error(STR."File \{getName()} is not a valid json:", e);
        }

        if(oldVersionFile.exists()) {
            try {
                Files.delete(oldVersionFile.toPath());
            } catch (IOException e) {
                AtlasClient.getInstance().logger.error(STR."Failed to delete the legacy file \{oldVersionFile.getAbsolutePath()}!", e);
            }
        }
    }

    private void writeFile(String content, File file) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(content);
        } catch (IOException e) {
            AtlasClient.getInstance().logger.error(STR."Failed to write file \{getName()}:", e);
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
            AtlasClient.getInstance().logger.error(STR."Failed to read file \{getName()}:", e);
        }
        return builder.toString();
    }

}
