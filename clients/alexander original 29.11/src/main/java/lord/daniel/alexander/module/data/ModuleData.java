package lord.daniel.alexander.module.data;

import lord.daniel.alexander.module.enums.EnumModuleType;

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
public @interface ModuleData {
    String name();
    String[] aliases() default {};
    String identifier() default "";
    EnumModuleType enumModuleType() default EnumModuleType.COMBAT;
    EnumModuleType[] categories() default {};
    String[] supportedIPs() default {""};
    int key() default 0;
    boolean enabled() default false;
    boolean alwaysRegistered() default false;
    boolean frozenState() default false;

}
