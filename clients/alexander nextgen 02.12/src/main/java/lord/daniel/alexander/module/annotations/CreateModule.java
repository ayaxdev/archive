package lord.daniel.alexander.module.annotations;

import lord.daniel.alexander.module.data.EnumModuleType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;

/**
 * Written by Daniel. on 21/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CreateModule {

    String name();
    String[] displayNames() default {};
    EnumModuleType category();
    EnumModuleType[] secondaryCategories() default {};
    int key() default 0;
    boolean enabledOnStart() default false;
    boolean alwaysRegistered() default false;
    boolean frozenState() default false;
    boolean loadFromConfig() default true;
    boolean saveToConfig() default true;
    boolean visibleInModuleList() default true;
    String[] allowedHWIDs() default {"*"};

}
