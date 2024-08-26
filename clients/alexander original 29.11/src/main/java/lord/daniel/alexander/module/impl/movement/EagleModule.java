package lord.daniel.alexander.module.impl.movement;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.event.impl.game.LivingUpdateEvent;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.settings.impl.bool.BooleanValue;
import net.minecraft.block.material.Material;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.BlockPos;

/**
 * Written by Daniel. on 04/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
@ModuleData(name = "Eagle", categories = {EnumModuleType.MOVEMENT, EnumModuleType.PLAYER, EnumModuleType.GHOST})
public class EagleModule extends AbstractModule {

    private final BooleanValue movingBackwards = new BooleanValue("OnlyWhenMovingBackwards", this, false);

    boolean sneaked = false;

    @EventLink
    public final Listener<LivingUpdateEvent> livingUpdateEventListener = livingUpdateEvent -> {
        boolean should = mc.thePlayer.onGround && mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ)).getBlock().getMaterial() == Material.air;;

        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindSneak) && (!movingBackwards.getValue() || GameSettings.isKeyDown(mc.gameSettings.keyBindBack))) {
            mc.gameSettings.keyBindSneak.pressed = should;
            sneaked = mc.gameSettings.keyBindSneak.pressed;
        }

        if(sneaked && !should) {
            sneaked = false;
            mc.gameSettings.keyBindSneak.pressed = false;
        }
    };

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindSneak))
            mc.gameSettings.keyBindSneak.pressed = false;
    }

}
