package lord.daniel.alexander.handler.event;

import io.github.nevalackin.radbus.Listen;
import lord.daniel.alexander.Modification;
import lord.daniel.alexander.event.Event;
import lord.daniel.alexander.event.impl.player.PlayerUpdateEvent;
import lord.daniel.alexander.event.impl.player.UpdateEvent;
import lord.daniel.alexander.event.impl.player.WalkingUpdateEvent;

/**
 * Written by Daniel. on 22/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
public class EventHandler {

    @Listen
    public final void onWalking(WalkingUpdateEvent walkingUpdateEvent) {
        Modification.getModification().getPubSub().publish(new UpdateEvent(walkingUpdateEvent.getStage() == Event.Stage.PRE ? Event.Stage.MID : Event.Stage.POST, walkingUpdateEvent.isOnGround(), walkingUpdateEvent.getPosX(), walkingUpdateEvent.getPosY(), walkingUpdateEvent.getPosZ()));
    }

    @Listen
    public final void onUpdate(PlayerUpdateEvent playerUpdateEvent) {
        if(playerUpdateEvent.getStage() != Event.Stage.POST)
            Modification.getModification().getPubSub().publish(new UpdateEvent(Event.Stage.PRE, playerUpdateEvent.isOnGround(), playerUpdateEvent.getPosX(), playerUpdateEvent.getPosY(), playerUpdateEvent.getPosZ()));
    }

}
