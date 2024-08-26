package ja.tabio.argon.processor.impl.rotation.interfaces;

import ja.tabio.argon.interfaces.Minecraft;

public interface RotationModifier extends Minecraft {

    float[] modifier(float[] from, float[] to, float[] server, float partialTicks, boolean back);

    boolean isEnabled();

}
