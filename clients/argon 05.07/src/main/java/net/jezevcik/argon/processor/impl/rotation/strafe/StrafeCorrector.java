package net.jezevcik.argon.processor.impl.rotation.strafe;

import net.jezevcik.argon.event.impl.StrafeInputEvent;
import net.jezevcik.argon.system.minecraft.Minecraft;

public interface StrafeCorrector extends Minecraft {

    void edit(float serverYaw, StrafeInputEvent event);

    default boolean fixYaw() {
        return true;
    }

    default void reset() { }

}
