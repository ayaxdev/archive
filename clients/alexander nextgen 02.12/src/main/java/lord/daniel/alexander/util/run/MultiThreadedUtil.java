package lord.daniel.alexander.util.run;

import lombok.experimental.UtilityClass;
import org.lwjglx.Sys;

import java.util.function.Supplier;

/**
 * Written by Daniel. on 25/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
@UtilityClass
public class MultiThreadedUtil {

    public void runAsync(Runnable runnable) {
        new Thread(runnable).start();
    }

    public void runAsync(Runnable runnable, String name) {
        new Thread(runnable, name).start();
    }

    public void runAfter(Supplier<Boolean> condition, Runnable runnable) {
        runAsync(() -> {
            while (true) {
                if(condition.get()) {
                    runnable.run();
                    break;
                }
            }
        });
    }

    public void runAfter(Supplier<Boolean> condition, Runnable runnable, String name) {
        runAsync(() -> {
            while (true) {
                if(condition.get()) {
                    runnable.run();
                    break;
                }
            }
        }, name);
    }

}
