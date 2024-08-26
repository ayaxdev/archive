package net.jezevcik.argon.event.impl;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class RotationVectorEvent {

    public final Entity entity;
    public final float tickDelta;
    public Vec3d result;

    public RotationVectorEvent(Entity entity, float tickDelta, Vec3d result) {
        this.entity = entity;
        this.tickDelta = tickDelta;
        this.result = result;
    }
}
