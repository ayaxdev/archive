package com.atani.nextgen.util.shader;

import static org.lwjgl.opengl.GL20.*;

import com.atani.nextgen.util.minecraft.MinecraftClient;
import com.atani.nextgen.util.system.FileUtil;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;

public class Shader implements MinecraftClient {

    private final int programID;

    public Shader(String frag, String vert) {
        int program = glCreateProgram();

        try {
            int fragmentShaderID = create(mc.getResourceManager().getResource(new ResourceLocation(frag)).getInputStream(), GL_FRAGMENT_SHADER);
            int vertexShaderID = create(mc.getResourceManager().getResource(new ResourceLocation(vert)).getInputStream(), GL_VERTEX_SHADER);

            glAttachShader(program, fragmentShaderID);
            glAttachShader(program, vertexShaderID);
        } catch (IOException ignored) {
            throw new RuntimeException("Failed to create shader!");
        }

        glLinkProgram(program);
        int status = glGetProgrami(program, GL_LINK_STATUS);

        if (status == 0) {
            throw new RuntimeException("Failed to link shader!");
        }

        this.programID = program;
    }

    public Shader(String frag) {
        this(frag, "client/shader/vertex.vsh");
    }

    public void use(Runnable runnable) {
        glUseProgram(programID);
        runnable.run();
        glUseProgram(0);
    }

    private int create(InputStream inputStream, int shaderType) {
        int shader = glCreateShader(shaderType);
        glShaderSource(shader, FileUtil.readInputStream(inputStream));
        glCompileShader(shader);

        if (glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
            throw new RuntimeException(STR."Failed to compile \{shaderType} shader!");
        }

        return shader;
    }

}