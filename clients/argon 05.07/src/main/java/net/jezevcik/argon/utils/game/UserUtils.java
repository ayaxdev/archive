package net.jezevcik.argon.utils.game;

import net.jezevcik.argon.mixin.MinecraftClientAccessor;
import net.jezevcik.argon.system.minecraft.Minecraft;
import net.minecraft.client.session.Session;

import java.util.Optional;
import java.util.UUID;

/**
 * A set of methods used for handling the users and their sessions
 */
public class UserUtils implements Minecraft {

    /**
     * Converts a UUID from the Minecraft format to the UUID object
     *
     * @param uuid UUID in Minecraft format
     * @return UUID object version of the provided UUID
     */
    public static UUID fromString(final String uuid) {
        return UUID.fromString(uuid.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
    }

    /**
     * Sets the Minecraft session to the provided data of an offline account
     *
     * @param name The profile name
     */
    public static void login(String name) {
        login(name, UUID.randomUUID(), "", Session.AccountType.MOJANG);
    }

    /**
     * Sets the Minecraft session to the provided data
     *
     * @param name The profile name
     * @param id The profile's uuid
     * @param accessToken The profile's access token
     * @param accountType The type of the provided profile
     */
    public static void login(String name, String id, String accessToken, Session.AccountType accountType) {
        login(name, UUID.fromString(id), accessToken, accountType);
    }

    /**
     * Sets the Minecraft session to the provided data
     *
     * @param name The profile name
     * @param id The profile's uuid
     * @param accessToken The profile's access token
     * @param accountType The type of the provided profile
     */
    public static void login(String name, UUID id, String accessToken, Session.AccountType accountType) {
        final Session session = new Session(name, id, accessToken, Optional.empty(), Optional.empty(), accountType);

        ((MinecraftClientAccessor) client).setSession(session);
    }

}
