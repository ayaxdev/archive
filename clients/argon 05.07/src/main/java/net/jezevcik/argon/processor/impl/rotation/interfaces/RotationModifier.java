package net.jezevcik.argon.processor.impl.rotation.interfaces;

import net.jezevcik.argon.system.minecraft.Minecraft;

public interface RotationModifier extends Minecraft {

    float[] modifier(float[] from, float[] to, float[] server, float partialTicks, boolean back);

    boolean isEnabled();

}
