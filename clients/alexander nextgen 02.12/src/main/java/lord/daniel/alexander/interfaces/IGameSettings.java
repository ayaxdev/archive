package lord.daniel.alexander.interfaces;

import net.minecraft.client.settings.KeyBinding;
import org.lwjglx.input.Keyboard;

public interface IGameSettings extends IMinecraft {

    default boolean isKeyDown(int key) {
        return Keyboard.isKeyDown(key);
    }

    default boolean isKeyDown(KeyBinding keyBinding) {
        return isKeyDown(keyBinding.getKeyCode());
    }

}
