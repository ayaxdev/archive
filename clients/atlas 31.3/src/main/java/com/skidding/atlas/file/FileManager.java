package com.skidding.atlas.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.skidding.atlas.AtlasClient;
import com.skidding.atlas.account.AccountManager;
import com.skidding.atlas.file.impl.HUDFile;
import com.skidding.atlas.hud.HUDManager;
import com.skidding.atlas.feature.Manager;
import com.skidding.atlas.file.impl.FeatureFile;
import com.skidding.atlas.keybind.KeybindingManager;
import com.skidding.atlas.module.ModuleManager;
import com.skidding.atlas.util.encryption.EncryptionUtil;

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
        map.put("hud", new HUDFile());

        super.preMinecraftLaunch();

        map.put("modules", new FeatureFile("modules", ModuleManager.getSingleton(), EncryptionUtil.KeyLevel.MEDIUM));
        map.put("keybindings", new FeatureFile("keybindings", KeybindingManager.getSingleton(), EncryptionUtil.KeyLevel.LOW));
        map.put("accounts", new FeatureFile("accounts", AccountManager.getSingleton(), EncryptionUtil.KeyLevel.MAXIMUM));
    }

    @Override
    public void postMinecraftLaunch() throws InvocationTargetException, NoSuchMethodException, InstantiationException {
        super.postMinecraftLaunch();

        for(FileFeature file : getFeatures()) {
            try {
                file.load(GSON);
            } catch (Exception e) {
                AtlasClient.getInstance().logger.error(STR."Failed to load \{file.getName()}", e);
            }
        }
    }

    public void save() {
        for(FileFeature file : getFeatures()) {
            file.save(GSON);
        }
    }

}
