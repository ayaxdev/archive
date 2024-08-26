package ja.tabio.argon.processor.impl.rotation.strafe;

import ja.tabio.argon.event.impl.StrafeInputEvent;
import ja.tabio.argon.interfaces.Minecraft;

public interface StrafeCorrector extends Minecraft {

    void edit(float serverYaw, StrafeInputEvent event);

    default boolean fixYaw() {
        return true;
    }

    default void reset() { }

}
