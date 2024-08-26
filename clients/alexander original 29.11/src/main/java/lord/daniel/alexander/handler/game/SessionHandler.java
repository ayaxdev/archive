package lord.daniel.alexander.handler.game;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.event.impl.game.RunTickEvent;
import lord.daniel.alexander.interfaces.Methods;

/**
 * Written by Daniel. on 05/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
public class SessionHandler implements Methods {

    public static boolean microsoft;

    @EventLink
    public final Listener<RunTickEvent> runTickEventListener = runTickEvent -> {
        microsoft = mc.getSession() != null &&
                !(mc.getSession().getToken().isEmpty() || mc.getSession().getToken().equals("!")) &&
                !(mc.getSession().getPlayerID().isEmpty() || mc.getSession().getPlayerID().equals("!"));
    };

}
