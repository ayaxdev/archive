package lord.daniel.alexander.interfaces;

import lord.daniel.alexander.handler.player.PlayerHandler;

public interface IPlayer extends IMinecraft {

    default double getPosX() {
        return mc.thePlayer.posX;
    }

    default double getPosY() {
        return mc.thePlayer.posY;
    }

    default double getPosZ() {
        return mc.thePlayer.posZ;
    }

    default float getRotationYaw() {
        return PlayerHandler.yaw;
    }

    default float getRotationPitch() {
        return PlayerHandler.pitch;
    }

    default int getHurtTime() {
        return getPlayer().hurtTime;
    }

}
