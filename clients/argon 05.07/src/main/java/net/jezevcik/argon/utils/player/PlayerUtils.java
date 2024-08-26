package net.jezevcik.argon.utils.player;

import net.jezevcik.argon.system.minecraft.Minecraft;
import net.jezevcik.argon.utils.math.MathUtils;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

/**
 * A set of methods used for simplifying code revolving around the player and his actions.
 */
public class PlayerUtils implements Minecraft {

    /**
     * Finds the distance of the player to the provided location.
     *
     * @param x The X coordinate.
     * @param y The Y coordinate.
     * @param z The Z coordinate.
     * @return The distance between the player and the provided location.
     */
    public static double getDistanceTo(final double x, final double y, final double z) {
        if (!Minecraft.inGame())
            throw new IllegalStateException("Cannot be called while not in-game!");

        assert client.player != null;

        return MathUtils.getDistance(client.player.getX(), client.player.getY(), client.player.getZ(), x, y, z);
    }

    /**
     * Finds the distance of the player to the provided vector.
     *
     * @param vec3d The vector.
     * @return The distance between the player and the provided vector.
     */
    public static double getDistanceTo(final Vec3d vec3d) {
        return getDistanceTo(vec3d.x, vec3d.y, vec3d.z);
    }

    /**
     * Finds the distance of the player to the provided Entity.
     *
     * @param entity The entity.
     * @return The distance between the player and the provided Entity.
     */
    public static double getDistanceTo(final Entity entity) {
        if (!Minecraft.inGame())
            throw new IllegalStateException("Cannot be called while not in-game!");

        assert client.player != null;

        final Vec3d eyes = client.player.getEyePos();

        final Vec3d closest = MathUtils.closestPointInBox(eyes, entity.getBoundingBox());

        return MathUtils.getDistance(eyes, closest);
    }

}
