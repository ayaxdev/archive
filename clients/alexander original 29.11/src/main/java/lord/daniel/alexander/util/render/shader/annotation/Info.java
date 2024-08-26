package lord.daniel.alexander.util.render.shader.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Info {

    String vert() default "vertex/vertex.vsh";

    String frag();

}
