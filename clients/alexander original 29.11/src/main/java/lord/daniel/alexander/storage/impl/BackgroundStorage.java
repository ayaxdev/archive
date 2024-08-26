package lord.daniel.alexander.storage.impl;

import lombok.Getter;
import lombok.Setter;
import lord.daniel.alexander.Modification;
import lord.daniel.alexander.storage.Storage;
import lord.daniel.alexander.util.os.FileUtils;
import lord.daniel.alexander.util.render.shader.shaders.ShaderBackground;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

/**
 * Written by Daniel. on 05/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
public class BackgroundStorage extends Storage<BackgroundStorage.Background> {

    @Getter
    @Setter
    private static BackgroundStorage backgroundStorage;

    @Override
    public void init() {
        try {
            for(String file : FileUtils.listFilesInResources(Modification.FULL_BACKGROUNDS_DIR)) {
                addShader(file);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addShader(String name) {
        ShaderBackground shaderBackground = new ShaderBackground(new ResourceLocation("alexander/shaders/fragment/backgrounds/" + name));
        shaderBackground.init();

        add(new Background(name.substring(0, 1).toUpperCase() + name.substring(1, name.length() - 5), shaderBackground));
    }

    public record Background(String name, ShaderBackground shaderBackground) { }

}
