package lord.daniel.alexander.event.impl.player;

import lord.daniel.alexander.event.Event;

/**
 * Written by Daniel. on 22/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
public class UpdateEvent extends Event {

    boolean onGround;
    double posX, posY, posZ;

    public UpdateEvent(Stage stage, boolean onGround, double posX, double posY, double posZ) {
        super(stage);
        this.onGround = onGround;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
    }

}
