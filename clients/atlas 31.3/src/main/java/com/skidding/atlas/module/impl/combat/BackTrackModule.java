package com.skidding.atlas.module.impl.combat;

import com.skidding.atlas.event.Event;
import com.skidding.atlas.event.impl.game.GameLoopEvent;
import com.skidding.atlas.event.impl.network.HandlePacketEvent;
import com.skidding.atlas.module.ModuleCategory;
import com.skidding.atlas.module.ModuleFeature;
import com.skidding.atlas.processor.impl.storage.TargetStorage;
import com.skidding.atlas.setting.SettingFeature;
import com.skidding.atlas.util.system.TimerUtil;
import io.github.racoondog.norbit.EventHandler;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;

import java.util.ArrayList;

public class BackTrackModule extends ModuleFeature {
    private final ArrayList<Packet<?>> packets = new ArrayList<>();

    private WorldClient lastWorld;
    private EntityLivingBase entity;

    public TimerUtil timer = new TimerUtil();


    public final SettingFeature<Float> delay = slider("Delay", 400, 0, 1000, 0).build();
    public final SettingFeature<Float> range = slider("Range", 6, 0, 10, 1).build();
    public final SettingFeature<Boolean> onlyIfNeeded = check("Only if needed", true).build();

    public BackTrackModule() {
        super(new ModuleBuilder("BackTrack", "Delays target's position", ModuleCategory.COMBAT));
    }

    @EventHandler
    public final void onHandlePacket(HandlePacketEvent handlePacketEvent) {
        if(handlePacketEvent.eventType != Event.EventType.INCOMING)
            return;

        if(mc.thePlayer == null || mc.theWorld == null || mc.getNetHandler().getNetworkManager().getNetHandler() == null) {
            packets.clear();
            return;
        }

        switch (handlePacketEvent.packet) {
            case S14PacketEntity s14PacketEntity -> {
                Entity entity = mc.theWorld.getEntityByID(s14PacketEntity.entityId);

                if (entity instanceof EntityLivingBase entityLivingBase) {
                    entityLivingBase.realPosX += s14PacketEntity.func_149062_c();
                    entityLivingBase.realPosY += s14PacketEntity.func_149061_d();
                    entityLivingBase.realPosZ += s14PacketEntity.func_149064_e();
                }
            }
            case S18PacketEntityTeleport s18PacketEntityTeleport -> {
                final Entity entity = mc.theWorld.getEntityByID(s18PacketEntityTeleport.getEntityId());

                if (entity instanceof EntityLivingBase entityLivingBase) {
                    entityLivingBase.realPosX = s18PacketEntityTeleport.getX();
                    entityLivingBase.realPosY = s18PacketEntityTeleport.getY();
                    entityLivingBase.realPosZ = s18PacketEntityTeleport.getZ();
                }
            }
            default -> { }
        }

        this.entity = TargetStorage.target;

        if (lastWorld != mc.theWorld) {
            resetPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
            lastWorld = mc.theWorld;
            return;
        }


        if (entity == null) {
            resetPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
        } else {
            addPackets(handlePacketEvent.packet, handlePacketEvent);
        }
    }

    @EventHandler
    public final void onGameLoop(GameLoopEvent gameLoopEvent) {
        if(gameLoopEvent.eventType == Event.EventType.PRE) {
            if (entity != null && entity.getEntityBoundingBox() != null && mc.thePlayer != null && mc.theWorld != null
                    && entity.realPosX != 0 && entity.realPosY != 0 && entity.realPosZ != 0 && entity.width != 0
                    && entity.height != 0) {

                boolean work = false;
                double realX = entity.realPosX / 32;
                double realY = entity.realPosY / 32;
                double realZ = entity.realPosZ / 32;

                if(!onlyIfNeeded.getValue()) {
                    if(mc.thePlayer.getDistance(entity.posX, entity.posY, entity.posZ) > 3) {
                        if (mc.thePlayer.getDistance(entity.posX, entity.posY, entity.posZ) >= mc.thePlayer.getDistance(realX,
                                realY, realZ)) {
                            resetPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
                        }
                    }
                }else {
                    if (mc.thePlayer.getDistance(entity.posX, entity.posY, entity.posZ) >= mc.thePlayer.getDistance(realX,
                            realY, realZ)) {
                        resetPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
                    }
                }

                if (mc.thePlayer.getDistanceToEntity(entity) > 3)
                    work = true;

                if (!onlyIfNeeded.getValue())
                    work = true;

                if (!work) {
                    if (mc.thePlayer.getDistance(realX, realY, realZ) <= 3) {
                        resetPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
                    }
                    releasePacketsToDistance(mc.getNetHandler().getNetworkManager().getNetHandler());
                }

                if (mc.thePlayer.getDistance(realX, realY, realZ) > range.getValue()
                        || timer.hasElapsed(delay.getValue().longValue(), true)) {
                    resetPackets(mc.getNetHandler().getNetworkManager().getNetHandler());
                }
            }
        }
    }

    private void releasePacketsToDistance(INetHandler netHandler) {
        if(entity == null)return;

        double x = entity.posX;
        double y = entity.posY;
        double z = entity.posZ;

        if (!packets.isEmpty()) {
            synchronized (packets) {
                while (mc.thePlayer.getDistance(x, y, z) < 3 && !packets.isEmpty()) {
                    try {
                        ((Packet<INetHandler>) packets.getFirst()).processPacket(netHandler);
                        if (packets.getFirst() instanceof S14PacketEntity) {
                            S14PacketEntity packet = (S14PacketEntity) packets.getFirst();
                            final Entity entity = mc.theWorld.getEntityByID(packet.entityId);

                            if (entity instanceof EntityLivingBase) {
                                x += packet.func_149062_c();
                                y += packet.func_149061_d();
                                z += packet.func_149064_e();
                            }
                        }

                        if (packets.getFirst() instanceof S18PacketEntityTeleport packet) {
                            final Entity entity = mc.theWorld.getEntityByID(packet.getEntityId());

                            if (entity instanceof EntityLivingBase) {
                                x = packet.getX();
                                y = packet.getY();
                                z = packet.getZ();
                            }
                        }
                    } catch (Exception _) { }
                    packets.remove(packets.getFirst());
                }
            }
        }
    }

    private void resetPackets(INetHandler netHandler) {
        if (!packets.isEmpty()) {
            synchronized (packets) {
                while (!packets.isEmpty()) {
                    try {
                        ((Packet<INetHandler>) packets.getFirst()).processPacket(netHandler);
                    } catch (Exception _) { }
                    packets.remove(packets.getFirst());
                }
            }
        }
    }

    private void addPackets(Packet<?> packet, Event event) {
        if (event == null || packet == null)
            return;
        synchronized (packets) {
            if (this.blockPacket(packet)) {
                packets.add(packet);
                event.cancelled = true;
            }
        }
    }

    private boolean blockPacket(Packet<?> packet) {
        if (packet instanceof net.minecraft.network.play.server.S03PacketTimeUpdate)
            return true;
        if (packet instanceof net.minecraft.network.play.server.S00PacketKeepAlive)
            return true;
        if (packet instanceof net.minecraft.network.play.server.S12PacketEntityVelocity)
            return true;
        if (packet instanceof net.minecraft.network.play.server.S27PacketExplosion)
            return true;
        if (packet instanceof net.minecraft.network.play.server.S32PacketConfirmTransaction) {
            return true;
        }
        return (packet instanceof S14PacketEntity
                || packet instanceof net.minecraft.network.play.server.S19PacketEntityHeadLook
                || packet instanceof S18PacketEntityTeleport
                || packet instanceof net.minecraft.network.play.server.S0FPacketSpawnMob
                || packet instanceof net.minecraft.network.play.server.S08PacketPlayerPosLook);
    }

    @Override
    protected void onEnable() {
        packets.clear();
    }

    @Override
    protected void onDisable() {

    }
}
