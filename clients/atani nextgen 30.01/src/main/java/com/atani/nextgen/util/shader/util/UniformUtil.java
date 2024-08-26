package com.atani.nextgen.util.shader.util;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glUniform4i;

public class UniformUtil {

    public int getUniform(int id, String name) {
        return glGetUniformLocation(id, name);
    }

    public void setUniform(int id, String name, float... args) {
        int uniform = glGetUniformLocation(id, name);

        switch (args.length) {
            case 1 -> glUniform1f(uniform, args[0]);
            case 2 -> glUniform2f(uniform, args[0], args[1]);
            case 3 -> glUniform3f(uniform, args[0], args[1], args[2]);
            case 4 -> glUniform4f(uniform, args[0], args[1], args[2], args[3]);
        }
    }

    public void setUniform(int id, String name, int... args) {
        int uniform = glGetUniformLocation(id, name);

        switch (args.length) {
            case 1 -> glUniform1i(uniform, args[0]);
            case 2 -> glUniform2i(uniform, args[0], args[1]);
            case 3 -> glUniform3i(uniform, args[0], args[1], args[2]);
            case 4 -> glUniform4i(uniform, args[0], args[1], args[2], args[3]);
        }
    }

}
