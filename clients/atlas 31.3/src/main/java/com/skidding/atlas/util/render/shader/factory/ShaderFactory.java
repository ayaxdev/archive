package com.skidding.atlas.util.render.shader.factory;

import com.skidding.atlas.util.minecraft.IMinecraft;
import com.skidding.atlas.util.render.shader.Shader;
import lombok.experimental.UtilityClass;
import net.minecraft.util.ResourceLocation;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@UtilityClass
public class ShaderFactory implements IMinecraft {

    public Shader createUsingPath(String fragmentShaderPath, String vertexShaderPath) {
        try {
            return new Shader(mc.getResourceManager().getResource(new ResourceLocation(fragmentShaderPath)).getInputStream(), mc.getResourceManager().getResource(new ResourceLocation(vertexShaderPath)).getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(STR."Failed to read shaders \{fragmentShaderPath} and \{vertexShaderPath}", e);
        }
    }

    public Shader createUsingSource(String fragmentShaderSource, String vertexShaderSource) {
        return new Shader(new ByteArrayInputStream(fragmentShaderSource.getBytes()), new ByteArrayInputStream(vertexShaderSource.getBytes()));
    }

}
