package ja.tabio.argon.module.impl.hack.combat;

import io.github.racoondog.norbit.EventHandler;
import ja.tabio.argon.event.impl.ProcessPacketEvent;
import ja.tabio.argon.module.Module;
import ja.tabio.argon.module.annotation.HackData;
import ja.tabio.argon.module.annotation.ModuleData;
import ja.tabio.argon.module.enums.HackCategory;
import ja.tabio.argon.module.enums.ModuleCategory;
import ja.tabio.argon.processor.impl.CombatProcessor;
import ja.tabio.argon.setting.impl.BooleanSetting;
import ja.tabio.argon.setting.impl.NumberSetting;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.util.MathHelper;

import java.util.LinkedList;
import java.util.List;

@ModuleData(name = "AntiBot", category = ModuleCategory.HACK)
@HackData(hackCategory = HackCategory.COMBAT)
public class AntiBotHack extends Module {

    public final BooleanSetting ticksExistedCheck = new BooleanSetting("TicksExistedCheck", false);
    public final NumberSetting ticksExisted = new NumberSetting("TicksExisted", 20, 0, 200, 0)
            .visibility(ticksExistedCheck::getValue);
    public final BooleanSetting invalidGroundCheck = new BooleanSetting("InvalidGroundCheck", false);
    public final BooleanSetting rotationCheck = new BooleanSetting("RotationCheck", false);
    public final BooleanSetting soundCheck = new BooleanSetting("SoundCheck", false);
    public final BooleanSetting swungCheck = new BooleanSetting("SwungCheck", false);
    public final BooleanSetting invalidName = new BooleanSetting("InvalidName", false);
    public final BooleanSetting tabList = new BooleanSetting("TabList", false);
    public final BooleanSetting entityId = new BooleanSetting("EntityID", false);

    public final List<Entity> madeSound = new LinkedList<>();
    public final List<Entity> swung = new LinkedList<>();

    @EventHandler
    public final void onTargetValidation(CombatProcessor.ValidEntityEvent validEntityEvent) {
        final Entity entity = validEntityEvent.entity;

        if (entityId.getValue() && (entity.getEntityId() >= 1000000000 || entity.getEntityId() <= -1)) {
            validEntityEvent.valid = false;
            return;
        }

        if (rotationCheck.getValue() && !isWrongRotation(validEntityEvent.entity)) {
            validEntityEvent.valid = false;
            return;
        }

        if (ticksExistedCheck.getValue() && entity.ticksExisted < ticksExisted.getValue()) {
            validEntityEvent.valid = false;
            return;
        }

        if (invalidGroundCheck.getValue() && entity.onGround && mc.theWorld.getBlockState(entity.getPosition().add(0, -0.05, 0)).getBlock() == Blocks.air) {
            validEntityEvent.valid = false;
            return;
        }

        if (soundCheck.getValue() && !madeSound.contains(validEntityEvent.entity)) {
            validEntityEvent.valid = false;
            return;
        }

        if (swungCheck.getValue() && !swung.contains(entity)) {
            validEntityEvent.valid = false;
            return;
        }

        if (invalidName.getValue() && !validName(entity)) {
            validEntityEvent.valid = false;
            return;
        }

        if (tabList.getValue() && !isInTabList(entity)) {
            validEntityEvent.valid = false;
        }
    }

    @EventHandler
    public final void onPacket(ProcessPacketEvent packetEvent) {
        if (packetEvent.packet instanceof S29PacketSoundEffect packetSoundEffect) {
            for (Entity entity : mc.theWorld.loadedEntityList) {
                if (entity != mc.thePlayer && !madeSound.contains(entity) && entity.getDistance(packetSoundEffect.getX(), packetSoundEffect.getY(), packetSoundEffect.getZ()) <= 0.8)
                    madeSound.add(entity);
            }
        } else if (packetEvent.packet instanceof S0BPacketAnimation packetAnimation) {
            final Entity entity = mc.theWorld.getEntityByID(packetAnimation.getEntityID());
            if (!swung.contains(entity))
                swung.add(entity);
        }
    }

    private boolean isWrongRotation(EntityPlayer entity) {
        float curOffset = MathHelper.wrapAngleTo180_float(entity.rotationYaw - entity.renderYawOffset);
        curOffset = MathHelper.clamp_float(curOffset, -75, 75);

        float renderYawOffset = entity.rotationYaw - curOffset;

        if (curOffset * curOffset > 2500.0F) {
            renderYawOffset += curOffset * 0.2F;
        }

        final double distanceToHead = Math.abs(entity.rotationYaw - renderYawOffset);
        final boolean hasIllegalPitch = entity.rotationPitch > 90 || entity.rotationPitch < -90;

        final boolean wrongRotation = distanceToHead > 75;

        return wrongRotation || hasIllegalPitch;
    }


    private boolean validName(final Entity entity) {
        if (!(entity instanceof EntityPlayer player))
            return true;

        final String name = player.getGameProfile().getName();
        return name.length() <= 16 && name.length() >= 3 && name.matches("[a-zA-Z0-9_]*");
    }

    private boolean isInTabList(Entity entity) {
        if (mc.isSingleplayer())
            return true;

        for (final NetworkPlayerInfo playerInfo : mc.thePlayer.sendQueue.getPlayerInfoMap()) {
            if (playerInfo.getGameProfile().getId().equals(entity.getUniqueID()))
                return true;
        }

        return false;
    }

}
