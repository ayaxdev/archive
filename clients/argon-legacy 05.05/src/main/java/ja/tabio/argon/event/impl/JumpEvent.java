package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.Event;
import net.minecraft.entity.Entity;

public class JumpEvent extends Event {
    public final Entity entity;
    public float rotationYaw;
    public double jumpHeight;
    public boolean jumpBoost,
            sprinting,
            updateState = true;

    public JumpEvent(Entity entity, float rotationYaw, double jumpHeight, boolean jumpBoost, boolean sprinting) {
        this.entity = entity;
        this.rotationYaw = rotationYaw;
        this.jumpHeight = jumpHeight;
        this.jumpBoost = jumpBoost;
        this.sprinting = sprinting;
    }
}
