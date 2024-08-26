package lord.daniel.alexander.file.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FileData {

    String fileName();

}
