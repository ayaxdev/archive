package lord.daniel.alexander.util.render.shader.exception;

public class ShaderLinkFailException extends IllegalStateException {

    public ShaderLinkFailException(int id) {
        super(String.format("Failed to link shader %d", id));
    }

}
