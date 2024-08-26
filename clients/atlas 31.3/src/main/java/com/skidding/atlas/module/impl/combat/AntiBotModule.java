package com.skidding.atlas.module.impl.combat;

import com.skidding.atlas.event.impl.client.TargetCheckEvent;
import com.skidding.atlas.event.impl.game.RunTickEvent;
import com.skidding.atlas.event.impl.game.LoadWorldEvent;
import com.skidding.atlas.event.impl.network.HandlePacketEvent;
import com.skidding.atlas.event.impl.player.action.AttackEntityEvent;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.setting.SettingFeature;
import com.skidding.atlas.util.minecraft.world.entity.EntityUtil;
import io.github.racoondog.norbit.EventHandler;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AntiBotModule extends ModuleFeature {

    public AntiBotModule() {
        super(new ModuleBuilder("AntiBot", "Attempts to remove bots that obstruct the player's view", ModuleCategory.COMBAT));
    }

    public final SettingFeature<Boolean> tabValue = check("Tab", true).build();
    public final SettingFeature<String> tabModeValue = mode("Tab mode", "Contains", new String[]{"Equals", "Contains"})
            .addDependency(tabValue).build();
    public final SettingFeature<Boolean> entityIDValue = check("Entity ID", true).build();
    public final SettingFeature<Boolean> colorValue = check("Color", false).build();
    public final SettingFeature<Boolean> livingTimeValue = check("Living time", false).build();
    public final SettingFeature<Float> livingTimeTicksValue = slider("Living time ticks", 40, 1, 200, 0)
            .addDependency(livingTimeValue).build();
    public final SettingFeature<Boolean> groundValue = check("Ground", true).build();
    public final SettingFeature<Boolean> airValue = check("Air", false).build();
    public final SettingFeature<Boolean> invalidGroundValue = check("Invalid ground", true).build();
    public final SettingFeature<Boolean> swingValue = check("Swing", false).build();
    public final SettingFeature<Boolean> healthValue = check("Health", false).build();
    public final SettingFeature<Boolean> derpValue = check("Derp", true).build();
    public final SettingFeature<Boolean> wasInvisibleValue = check("Was invisible", false).build();
    public final SettingFeature<Boolean> armorValue = check("Armor", false).build();
    public final SettingFeature<Boolean> pingValue = check("Ping", false).build();
    public final SettingFeature<Boolean> needHitValue = check("Need hit", false).build();
    public final SettingFeature<Boolean> duplicateInWorldValue = check("Duplicate in world", false).build();
    public final SettingFeature<Boolean> duplicateInTabValue = check("Duplicate in tab", false).build();
    public final SettingFeature<Boolean> matrixValue = check("Matrix", false).build();

    public final List<Integer> ground = new ArrayList<>();
    public final List<Integer> air = new ArrayList<>();
    public final Map<Integer, Integer> invalidGround = new HashMap<>();
    public final List<Integer> swing = new ArrayList<>();
    public final List<Integer> invisible = new ArrayList<>();
    public final List<Integer> hit = new ArrayList<>();
    public final List<Integer> matrix = new ArrayList<>();

    @EventHandler
    public void onPacket(HandlePacketEvent packetEvent) {
        if (getPlayer() == null || getWorld() == null)
            return;

        final Packet<?> packet = packetEvent.packet;

        if (packet instanceof S14PacketEntity packetEntity) {
            final Entity entity = packetEntity.getEntity(getWorld());

            if (entity instanceof EntityPlayer) {
                if (packetEntity.getOnGround() && !ground.contains(entity.getEntityId()))
                    ground.add(entity.getEntityId());

                if (!packetEntity.getOnGround() && !air.contains(entity.getEntityId()))
                    air.add(entity.getEntityId());

                if (packetEntity.getOnGround()) {
                    if (entity.prevPosY != entity.posY)
                        invalidGround.put(entity.getEntityId(), invalidGround.getOrDefault(entity.getEntityId(), 0) + 1);
                } else {
                    final int currentVL = invalidGround.getOrDefault(entity.getEntityId(), 0) / 2;

                    if (currentVL <= 0)
                        invalidGround.remove(entity.getEntityId());
                    else
                        invalidGround.put(entity.getEntityId(), currentVL);
                }

                if (entity.isInvisible() && !invisible.contains(entity.getEntityId()))
                    invisible.add(entity.getEntityId());
            }
        }

        if (packet instanceof S0BPacketAnimation packetAnimation) {
            final Entity entity = getWorld().getEntityByID(packetAnimation.getEntityID());

            if (entity instanceof EntityLivingBase && packetAnimation.getAnimationType() == 0 && !swing.contains(entity.getEntityId()))
                swing.add(entity.getEntityId());
        }
    }

    @EventHandler
    public void onAttack(AttackEntityEvent attackEntityEvent) {
        final Entity entity = attackEntityEvent.target;

        if (entity instanceof EntityLivingBase && !hit.contains(entity.getEntityId()))
            hit.add(entity.getEntityId());
    }

    @EventHandler
    public void onTick(RunTickEvent runTickEvent) {
        if (matrixValue.getValue()) {
            if (getPlayer().ticksExisted > 110) {
                for (final Entity entity : getWorld().loadedEntityList) {
                    if (entity instanceof EntityPlayer && entity != getPlayer() && entity.getCustomNameTag().isEmpty() && !matrix.contains(entity.getEntityId())) {
                        matrix.add(entity.getEntityId());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onWorldLoad(LoadWorldEvent loadWorldEvent) {
        clearAll();
    }

    @EventHandler
    public void onAllowTarget(TargetCheckEvent targetCheckEvent) {
        if (!isBot(targetCheckEvent.entityLivingBase))
            targetCheckEvent.allow = false;
    }

    public void clearAll() {
        hit.clear();
        swing.clear();
        ground.clear();
        invalidGround.clear();
        invisible.clear();
    }

    public boolean isBot(final EntityLivingBase entity) {
        if (!(entity instanceof EntityPlayer))
            return false;

        if (this.matrixValue.getValue() && this.matrix.contains(entity.getEntityId()))
            return true;

        if (this.colorValue.getValue() && !entity.getDisplayName().getFormattedText()
                .replace("§r", "").contains("§"))
            return true;

        if (this.livingTimeValue.getValue() && entity.ticksExisted < this.livingTimeTicksValue.getValue())
            return true;

        if (this.groundValue.getValue() && !this.ground.contains(entity.getEntityId()))
            return true;

        if (this.airValue.getValue() && !this.air.contains(entity.getEntityId()))
            return true;

        if (this.swingValue.getValue() && !this.swing.contains(entity.getEntityId()))
            return true;

        if (this.healthValue.getValue() && entity.getHealth() > 20F)
            return true;

        if (this.entityIDValue.getValue() && (entity.getEntityId() >= 1000000000 || entity.getEntityId() <= -1))
            return true;

        if (this.derpValue.getValue() && (entity.rotationPitch > 90F || entity.rotationPitch < -90F))
            return true;

        if (this.wasInvisibleValue.getValue() && this.invisible.contains(entity.getEntityId()))
            return true;

        if (this.armorValue.getValue()) {
            final EntityPlayer player = (EntityPlayer) entity;

            if (player.inventory.armorInventory[0] == null && player.inventory.armorInventory[1] == null &&
                    player.inventory.armorInventory[2] == null && player.inventory.armorInventory[3] == null)
                return true;
        }

        if (this.pingValue.getValue()) {
            EntityPlayer player = (EntityPlayer) entity;

            if (mc.getNetHandler().getPlayerInfo(player.getUniqueID()) == null || mc.getNetHandler().getPlayerInfo(player.getUniqueID()).getResponseTime() == 0)
                return true;
        }

        if (this.needHitValue.getValue() && !this.hit.contains(entity.getEntityId()))
            return true;

        if (this.invalidGroundValue.getValue() && this.invalidGround.getOrDefault(entity.getEntityId(), 0) >= 10)
            return true;

        if (this.tabValue.getValue()) {
            final boolean equals = this.tabModeValue.getValue().equalsIgnoreCase("Equals");
            final String targetName = StringUtils.stripControlCodes(entity.getDisplayName().getFormattedText());

            if (targetName != null) {
                for (final NetworkPlayerInfo networkPlayerInfo : mc.getNetHandler().getPlayerInfoMap()) {
                    final String networkName = StringUtils.stripControlCodes(EntityUtil.getName(networkPlayerInfo));

                    if (networkName == null)
                        continue;

                    if (equals ? targetName.equals(networkName) : targetName.contains(networkName))
                        return false;
                }

                return true;
            }
        }

        if (this.duplicateInWorldValue.getValue()) {
            if (getWorld().loadedEntityList.stream()
                    .filter(currEntity -> currEntity instanceof EntityPlayer && (currEntity)
                            .getDisplayName().equals((currEntity).getDisplayName()))
                    .count() > 1)
                return true;
        }

        if (this.duplicateInTabValue.getValue()) {
            if (mc.getNetHandler().getPlayerInfoMap().stream()
                    .filter(networkPlayer -> entity.getName().equals(StringUtils.stripControlCodes(EntityUtil.getName(networkPlayer))))
                    .count() > 1)
                return true;
        }

        return entity.getName().isEmpty() || entity.getName().equals(getPlayer().getName());
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        clearAll();
    }

}
