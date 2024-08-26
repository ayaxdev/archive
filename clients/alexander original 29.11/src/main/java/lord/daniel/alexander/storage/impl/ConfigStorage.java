package lord.daniel.alexander.storage.impl;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.Setter;
import lord.daniel.alexander.Modification;
import lord.daniel.alexander.config.Config;
import lord.daniel.alexander.interfaces.Methods;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.storage.Storage;
import org.apache.commons.io.FilenameUtils;

import java.io.*;

/**
 * Written by Daniel. on 04/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
public class ConfigStorage extends Storage<Config> implements Methods {

    @Getter
    @Setter
    private static ConfigStorage configStorage;

    @Override
    public void init() {
        loadConfigs();
    }

    private void loadConfigs() {
        File[] files = Modification.INSTANCE.getConfigDir().listFiles();
        if (files != null) {
            for (File file : files) {
                this.add(new Config(FilenameUtils.removeExtension(file.getName())));
            }
        }
    }

    public void reloadConfigs() {
        this.getList().clear();
        sendMessage("Cleared Configs");
        loadConfigs();
        sendMessage("Loaded " + getList().size() + " Configs");
    }

    public boolean loadConfig(String configName) {
        if (configName == null)
            return false;
        Config config = findConfig(configName);
        if (config == null)
            return false;
        try {
            FileReader reader = new FileReader(config.getFile());
            JsonParser parser = new JsonParser();
            JsonObject object = (JsonObject) parser.parse(reader);
            config.load(object);
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    public boolean loadConfig(String configName, AbstractModule abstractModule) {
        if (configName == null)
            return false;
        Config config = findConfig(configName);
        if (config == null)
            return false;
        try {
            FileReader reader = new FileReader(config.getFile());
            JsonParser parser = new JsonParser();
            JsonObject object = (JsonObject) parser.parse(reader);
            config.load(object, abstractModule);
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    public boolean saveConfig(String configName) {
        if (configName == null)
            return false;
        Config config;
        if ((config = findConfig(configName)) == null) {
            Config newConfig = (config = new Config(configName));
            getList().add(newConfig);
        }

        String contentPrettyPrint = new GsonBuilder().setPrettyPrinting().create().toJson(config.save());
        try {
            FileWriter writer = new FileWriter(config.getFile());
            writer.write(contentPrettyPrint);
            writer.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public Config findConfig(String configName) {
        if (configName == null) return null;
        for (Config config : getList()) {
            if (config.getName().equalsIgnoreCase(configName))
                return config;
        }

        if (new File(Modification.INSTANCE.getConfigDir(), configName).exists())
            return new Config(configName);

        return null;
    }

    public boolean deleteConfig(String configName) {
        if (configName == null)
            return false;
        Config config;
        if ((config = findConfig(configName)) != null) {
            final File f = config.getFile();
            getList().remove(config);
            return f.exists() && f.delete();
        }
        return false;
    }

}
