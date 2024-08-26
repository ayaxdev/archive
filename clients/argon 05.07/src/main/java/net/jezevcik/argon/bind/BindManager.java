package net.jezevcik.argon.bind;

import meteordevelopment.orbit.EventHandler;
import net.jezevcik.argon.event.impl.KeyPressEvent;
import net.jezevcik.argon.system.minecraft.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BindManager implements Minecraft {

    private final Map<Supplier<Integer>, Runnable> map = new HashMap<>();

    public void addBind(Supplier<Integer> key, Runnable action) {
        map.put(key, action);
    }

    @EventHandler
    public final void onKey(KeyPressEvent keyPressEvent) {
        if (keyPressEvent.keyAction != GLFW.GLFW_PRESS)
            return;

        if (client.currentScreen != null)
            return;

        map.forEach((bind, action) -> {
            final Integer key = bind.get();

            if (key != null && key == keyPressEvent.key) {
                action.run();
            }
        });
    }

}
