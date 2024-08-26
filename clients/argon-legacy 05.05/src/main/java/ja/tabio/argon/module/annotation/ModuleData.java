package ja.tabio.argon.module.annotation;

import ja.tabio.argon.module.enums.ModuleCategory;
import ja.tabio.argon.module.enums.HackCategory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleData {

    String name();

    String displayName() default "name";

    int key() default 0;

    boolean enabled() default false;

    boolean alwaysRegistered() default false;

    ModuleCategory category();


}
