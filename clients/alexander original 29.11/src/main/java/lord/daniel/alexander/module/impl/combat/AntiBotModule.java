package lord.daniel.alexander.module.impl.combat;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.event.impl.game.*;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.settings.impl.bool.BooleanValue;
import lord.daniel.alexander.settings.impl.mode.StringModeValue;
import lord.daniel.alexander.settings.impl.number.NumberValue;
import lord.daniel.alexander.storage.impl.ModuleStorage;
import lord.daniel.alexander.util.player.PlayerUtil;
import lord.daniel.alexander.util.render.color.ColorUtil;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S14PacketEntity;

import java.util.*;

/**
 * Written by Daniel. on 16/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
@ModuleData(name = "AntiBot", enumModuleType = EnumModuleType.COMBAT)
public class AntiBotModule extends AbstractModule {

    private final BooleanValue tabValue = new BooleanValue("Tab", this, true);
    private final StringModeValue tabModeValue = new StringModeValue("TabMode", this, new String[] {"Equals", "Contains"}, "Contains").addVisibleCondition(tabValue::getValue);
    private final BooleanValue entityIDValue = new BooleanValue("EntityID", this, true);
    private final BooleanValue colorValue = new BooleanValue("Color", this, false);
    private final BooleanValue livingTimeValue = new BooleanValue("LivingTime", this, false);
    private final NumberValue<Integer> livingTimeTicksValue = new NumberValue<>("LivingTimeTicks", this, 40, 1, 200).addVisibleCondition(livingTimeValue::getValue);
    private final BooleanValue groundValue = new BooleanValue("Ground", this, true);
    private final BooleanValue airValue = new BooleanValue("Air", this, false);
    private final BooleanValue invalidGroundValue = new BooleanValue("InvalidGround", this, true);
    private final BooleanValue swingValue = new BooleanValue("Swing", this, false);
    private final BooleanValue healthValue = new BooleanValue("Health", this, false);
    private final BooleanValue derpValue = new BooleanValue("Derp", this, true);
    private final BooleanValue wasInvisibleValue = new BooleanValue("WasInvisible", this, false);
    private final BooleanValue armorValue = new BooleanValue("Armor", this, false);
    private final BooleanValue pingValue = new BooleanValue("Ping", this, false);
    private final BooleanValue needHitValue = new BooleanValue("NeedHit", this, false);
    private final BooleanValue duplicateInWorldValue = new BooleanValue("DuplicateInWorld", this, false);
    private final BooleanValue duplicateInTabValue = new BooleanValue("DuplicateInTab", this, false);
    private final BooleanValue matrixValue = new BooleanValue("Matrix", this, false);

    private final List<Integer> ground = new ArrayList<>();
    private final List<Integer> air = new ArrayList<>();
    private final Map<Integer, Integer> invalidGround = new HashMap<>();
    private final List<Integer> swing = new ArrayList<>();
    private final List<Integer> invisible = new ArrayList<>();
    private final List<Integer> hit = new ArrayList<>();
    private final List<Integer> matrix = new ArrayList<>();

    @Override
    public void onEnable() {
        
    }

    @Override
    public void onDisable() {
        clearAll();
    }

    @EventLink
    public final Listener<PacketEvent> packetEventListener = packetEvent -> {
        if(mc.thePlayer == null || mc.theWorld == null)
            return;

        final Packet<?> packet = packetEvent.getPacket();

        if(packet instanceof S14PacketEntity) {
            final S14PacketEntity packetEntity = (S14PacketEntity) packetEvent.getPacket();
            final Entity entity = packetEntity.getEntity(mc.theWorld);

            if(entity instanceof EntityPlayer) {
                if(packetEntity.getOnGround() && !ground.contains(entity.getEntityId()))
                    ground.add(entity.getEntityId());

                if(!packetEntity.getOnGround() && !air.contains(entity.getEntityId()))
                    air.add(entity.getEntityId());

                if(packetEntity.getOnGround()) {
                    if(entity.prevPosY != entity.posY)
                        invalidGround.put(entity.getEntityId(), invalidGround.getOrDefault(entity.getEntityId(), 0) + 1);
                }else{
                    final int currentVL = invalidGround.getOrDefault(entity.getEntityId(), 0) / 2;

                    if(currentVL <= 0)
                        invalidGround.remove(entity.getEntityId());
                    else
                        invalidGround.put(entity.getEntityId(), currentVL);
                }

                if(entity.isInvisible() && !invisible.contains(entity.getEntityId()))
                    invisible.add(entity.getEntityId());
            }
        }

        if(packet instanceof S0BPacketAnimation) {
            final S0BPacketAnimation packetAnimation = (S0BPacketAnimation) packetEvent.getPacket();
            final Entity entity = mc.theWorld.getEntityByID(packetAnimation.getEntityID());

            if(entity instanceof EntityLivingBase && packetAnimation.getAnimationType() == 0 && !swing.contains(entity.getEntityId()))
                swing.add(entity.getEntityId());
        }  
    };

    @EventLink
    public final Listener<AttackEvent> attackEventListener = attackEvent -> {
        final Entity entity = attackEvent.getAttacking();

        if(entity instanceof EntityLivingBase && !hit.contains(entity.getEntityId()))
            hit.add(entity.getEntityId());
    };

    @EventLink
    public final Listener<OnTickEvent> onTickEventListener = onTickEvent -> {
        if(matrixValue.getValue()) {
            if (mc.thePlayer.ticksExisted > 110) {
                for (final Entity entity : mc.theWorld.loadedEntityList) {
                    if (entity instanceof EntityPlayer && entity != mc.thePlayer && entity.getCustomNameTag() == "" && !matrix.contains(entity.getEntityId())) {
                        matrix.add(entity.getEntityId());
                    }
                }
            }
        }
    };

    @EventLink
    public final Listener<WorldLoadEvent> worldLoadEventListener =  worldLoadEvent -> clearAll();

    private void clearAll() {
        hit.clear();
        swing.clear();
        ground.clear();
        invalidGround.clear();
        invisible.clear();
    }

    public static boolean isBot(final EntityLivingBase entity) {
        if (!(entity instanceof EntityPlayer))
            return false;

        final AntiBotModule antiBotModule = ModuleStorage.getModuleStorage().getByClass(AntiBotModule.class);

        if (antiBotModule == null || !antiBotModule.isEnabled())
            return false;

        if(antiBotModule.matrixValue.getValue() && antiBotModule.matrix.contains(entity.getEntityId()))
            return true;

        if (antiBotModule.colorValue.getValue() && !entity.getDisplayName().getFormattedText()
                .replace("§r", "").contains("§"))
            return true;

        if (antiBotModule.livingTimeValue.getValue() && entity.ticksExisted < antiBotModule.livingTimeTicksValue.getValue())
            return true;

        if (antiBotModule.groundValue.getValue() && !antiBotModule.ground.contains(entity.getEntityId()))
            return true;

        if (antiBotModule.airValue.getValue() && !antiBotModule.air.contains(entity.getEntityId()))
            return true;

        if(antiBotModule.swingValue.getValue() && !antiBotModule.swing.contains(entity.getEntityId()))
            return true;

        if(antiBotModule.healthValue.getValue() && entity.getHealth() > 20F)
            return true;

        if(antiBotModule.entityIDValue.getValue() && (entity.getEntityId() >= 1000000000 || entity.getEntityId() <= -1))
            return true;

        if(antiBotModule.derpValue.getValue() && (entity.rotationPitch > 90F || entity.rotationPitch < -90F))
            return true;

        if(antiBotModule.wasInvisibleValue.getValue() && antiBotModule.invisible.contains(entity.getEntityId()))
            return true;

        if(antiBotModule.armorValue.getValue()) {
            final EntityPlayer player = (EntityPlayer) entity;

            if (player.inventory.armorInventory[0] == null && player.inventory.armorInventory[1] == null &&
                    player.inventory.armorInventory[2] == null && player.inventory.armorInventory[3] == null)
                return true;
        }

        if(antiBotModule.pingValue.getValue()) {
            EntityPlayer player = (EntityPlayer) entity;

            if(mc.getNetHandler().getPlayerInfo(player.getUniqueID()) == null || mc.getNetHandler().getPlayerInfo(player.getUniqueID()).getResponseTime() == 0)
                return true;
        }

        if(antiBotModule.needHitValue.getValue() && !antiBotModule.hit.contains(entity.getEntityId()))
            return true;

        if(antiBotModule.invalidGroundValue.getValue() && antiBotModule.invalidGround.getOrDefault(entity.getEntityId(), 0) >= 10)
            return true;

        if(antiBotModule.tabValue.getValue()) {
            final boolean equals = antiBotModule.tabModeValue.getValue().equalsIgnoreCase("Equals");
            final String targetName = ColorUtil.stripColor(entity.getDisplayName().getFormattedText());

            if (targetName != null) {
                for (final NetworkPlayerInfo networkPlayerInfo : mc.getNetHandler().getPlayerInfoMap()) {
                    final String networkName = ColorUtil.stripColor(PlayerUtil.getName(networkPlayerInfo));

                    if (networkName == null)
                        continue;

                    if (equals ? targetName.equals(networkName) : targetName.contains(networkName))
                        return false;
                }

                return true;
            }
        }

        if(antiBotModule.duplicateInWorldValue.getValue()) {
            if (mc.theWorld.loadedEntityList.stream()
                    .filter(currEntity -> currEntity instanceof EntityPlayer && (currEntity)
                            .getDisplayName().equals((currEntity).getDisplayName()))
                    .count() > 1)
                return true;
        }

        if(antiBotModule.duplicateInTabValue.getValue()) {
            if (mc.getNetHandler().getPlayerInfoMap().stream()
                    .filter(networkPlayer -> entity.getCommandSenderName().equals(ColorUtil.stripColor(PlayerUtil.getName(networkPlayer))))
                    .count() > 1)
                return true;
        }

        return entity.getCommandSenderName().isEmpty() || entity.getCommandSenderName().equals(mc.thePlayer.getCommandSenderName());
    }

}