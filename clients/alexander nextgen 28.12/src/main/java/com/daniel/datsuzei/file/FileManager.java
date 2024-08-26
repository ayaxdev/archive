package com.daniel.datsuzei.file;

import com.daniel.datsuzei.feature.Feature;
import com.daniel.datsuzei.feature.Manager;
import com.daniel.datsuzei.file.impl.FeatureFile;
import com.daniel.datsuzei.module.ModuleManager;
import com.daniel.datsuzei.settings.SettingManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.InvocationTargetException;

public class FileManager extends Manager<FileFeature> {

    private static volatile FileManager fileManager;

    public static FileManager getSingleton() {
        if(fileManager == null)
            fileManager = new FileManager();

        return fileManager;
    }

    private final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    public FileManager() {
        super(FileFeature.class);
    }

    @Override
    public void preMinecraftLaunch() {
        super.preMinecraftLaunch();

        map.put("modules", new FeatureFile("modules", ModuleManager.getSingleton()));
        map.put("settings", new FeatureFile("settings", SettingManager.getSingleton()));
    }

    @Override
    public void postMinecraftLaunch() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        super.postMinecraftLaunch();

        for(FileFeature file : getFeatures()) {
            file.load(GSON);
        }
    }

    public void save() {
        for(FileFeature file : getFeatures()) {
            file.save(GSON);
        }
    }

}
