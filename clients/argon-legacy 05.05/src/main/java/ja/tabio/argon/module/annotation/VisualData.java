package ja.tabio.argon.module.annotation;

import ja.tabio.argon.module.enums.HackCategory;
import ja.tabio.argon.module.enums.VisualCategory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface VisualData {

    VisualCategory visualCategory();

    boolean performance() default false;

}
