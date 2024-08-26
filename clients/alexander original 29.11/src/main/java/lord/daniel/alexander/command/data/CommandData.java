package lord.daniel.alexander.command.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Written by Daniel. on 04/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandData {
    String name();
    String usage();
    String[] aliases() default {};
}
