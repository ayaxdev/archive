package lord.daniel.alexander.util.render.shader;

import lombok.Getter;
import lord.daniel.alexander.interfaces.IMinecraft;
import lord.daniel.alexander.util.render.shader.exception.ShaderCompilationFailException;
import lord.daniel.alexander.util.render.shader.exception.ShaderLinkFailException;
import lord.daniel.alexander.util.render.shader.util.ShaderUtil;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram implements IMinecraft {

    @Getter
    private final int programId;

    public ShaderProgram(String fragmentShaderSource) {
        this(fragmentShaderSource, ShaderUtil.VERTEX_SHADER);
    }

    public ShaderProgram(String fragmentShaderSource, String vertexShaderSource) {
        int program = glCreateProgram();

        int fragmentShaderId = compileShader(GL_FRAGMENT_SHADER, fragmentShaderSource);
        glAttachShader(program, fragmentShaderId);

        int vertexShaderId = compileShader(GL_VERTEX_SHADER, vertexShaderSource);
        glAttachShader(program, vertexShaderId);

        glLinkProgram(program);
        checkShaderLinkStatus(program);
        this.programId = program;
    }

    public void use() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public int getUniformLocation(String uniformName) {
        return glGetUniformLocation(programId, uniformName);
    }

    public void setUniformFloat(String uniformName, float... values) {
        int location = getUniformLocation(uniformName);
        switch (values.length) {
            case 1:
                glUniform1f(location, values[0]);
                break;
            case 2:
                glUniform2f(location, values[0], values[1]);
                break;
            case 3:
                glUniform3f(location, values[0], values[1], values[2]);
                break;
            case 4:
                glUniform4f(location, values[0], values[1], values[2], values[3]);
                break;
        }
    }

    public void setUniformInt(String uniformName, int... values) {
        int location = getUniformLocation(uniformName);
        if (values.length > 1) {
            glUniform2i(location, values[0], values[1]);
        } else {
            glUniform1i(location, values[0]);
        }
    }

    private int compileShader(int shaderType, String shaderSource) {
        int shaderId = glCreateShader(shaderType);
        glShaderSource(shaderId, shaderSource);
        glCompileShader(shaderId);
        checkShaderCompilationStatus(shaderId);
        return shaderId;
    }

    private void checkShaderCompilationStatus(int shaderId) {
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            System.out.println(glGetShaderInfoLog(shaderId, 4096));
            throw new ShaderCompilationFailException(shaderId);
        }
    }

    private void checkShaderLinkStatus(int programId) {
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            System.out.println(glGetProgramInfoLog(programId, 4096));
            throw new ShaderLinkFailException(programId);
        }
    }

}
