package ja.tabio.argon.event.impl;

public class StrafeInputEvent {
    public float moveForward, moveStrafe, rotationYaw;
    public final float changeYaw;
    public boolean movementFix, fixYaw, sneak;

    public StrafeInputEvent(float moveForward, float moveStrafe, float rotationYaw, float changeYaw, boolean movementFix, boolean fixYaw, boolean sneak) {
        this.moveForward = moveForward;
        this.moveStrafe = moveStrafe;
        this.rotationYaw = rotationYaw;
        this.changeYaw = changeYaw;
        this.movementFix = movementFix;
        this.fixYaw = fixYaw;
        this.sneak = sneak;
    }
}
