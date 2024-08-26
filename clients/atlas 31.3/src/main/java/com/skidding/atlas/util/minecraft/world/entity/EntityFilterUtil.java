package com.skidding.atlas.util.minecraft.world.entity;

import com.skidding.atlas.util.minecraft.IMinecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Arrays;
import java.util.List;

public class EntityFilterUtil implements IMinecraft {

    public static final List<Class<?>> BAD_ENTITIES = Arrays.asList(
            EntityXPOrb.class,
            EntityItem.class,
            EntityArmorStand.class,
            EntityBoat.class,
            EntityEnderCrystal.class,
            EntityExpBottle.class
    );

    public static boolean isBadEntity(Entity entity, boolean mobs) {
        return entity == mc.thePlayer ||
                BAD_ENTITIES.contains(entity.getClass()) ||
                (!mobs && !(entity instanceof EntityPlayer));
    }

}
