package lord.daniel.alexander.util.render.shader.exception;

public class ShaderCompilationFailException extends IllegalStateException {

    public ShaderCompilationFailException(int id) {
        super(String.format("Failed to compile shader %d", id));
    }

}
