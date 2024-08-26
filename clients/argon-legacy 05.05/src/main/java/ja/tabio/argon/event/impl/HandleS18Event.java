package ja.tabio.argon.event.impl;

import ja.tabio.argon.event.enums.Stage;
import net.minecraft.entity.Entity;

public class HandleS18Event {
    public final Stage stage;
    public final Entity entity;
    public int posX, posY, posZ;
    public float rotationYaw, rotationPitch;
    public final float fixedYaw, fixedPitch;

    public HandleS18Event(Stage stage, Entity entity, int posX, int posY, int posZ, float rotationYaw, float rotationPitch, float fixedYaw, float fixedPitch) {
        this.stage = stage;
        this.entity = entity;
        this.fixedYaw = fixedYaw;
        this.fixedPitch = fixedPitch;
        this.rotationPitch = rotationPitch;
        this.rotationYaw = rotationYaw;
        this.posZ = posZ;
        this.posY = posY;
        this.posX = posX;
    }
}
