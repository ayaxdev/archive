package lord.daniel.alexander.util.player;

import lombok.experimental.UtilityClass;
import lord.daniel.alexander.interfaces.Methods;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.MathHelper;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@UtilityClass
public class PlayerUtil implements Methods {

    public static boolean canBuildForward() {
        final float yaw = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw);
        return (yaw > 77.5 && yaw < 102.5)
                || (yaw > 167.5 || yaw < -167.0f)
                || (yaw < -77.5 && yaw > -102.5)
                || (yaw > -12.5 && yaw < 12.5);
    }

    public static String getName(final NetworkPlayerInfo networkPlayerInfoIn) {
        return networkPlayerInfoIn.getDisplayName() != null ? networkPlayerInfoIn.getDisplayName().getFormattedText() :
                ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());
    }

}
