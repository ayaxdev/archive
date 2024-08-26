package lord.daniel.alexander.module.impl.combat;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lombok.Getter;
import lord.daniel.alexander.event.impl.game.PacketEvent;
import lord.daniel.alexander.event.impl.game.RunTickEvent;
import lord.daniel.alexander.handler.plaxer.TargetHandler;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.settings.impl.bool.BooleanValue;
import lord.daniel.alexander.settings.impl.number.NumberValue;
import lord.daniel.alexander.util.math.time.TimeHelper;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.ThreadQuickExitException;
import net.minecraft.network.play.server.*;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import java.util.LinkedList;
import java.util.List;


@ModuleData(name = "Backtrack", categories = {EnumModuleType.COMBAT, EnumModuleType.GHOST})
public class BacktrackModule extends AbstractModule {

    private final TimeHelper timeHelper = new TimeHelper();
    private final List<Packet<INetHandler>> packets = new LinkedList<>();

    public NumberValue<Float> minimumRange = new NumberValue<>("MinimumRange", this, 3.0f, 1.0f, 6.0f, 1);
    public NumberValue<Float> maximumRange = new NumberValue<>("MaximumRange", this, 6.0f, 3.0f, 6.0f, 1);
    public NumberValue<Long> maximumTime = new NumberValue<>("MaximumTime", this, 400L, 0L, 1500L, 0);
    public BooleanValue onlyKillAura = new BooleanValue("OnlyKillAura", this, true);

    @Getter
    private EntityLivingBase entity;
    @Getter
    private boolean blockPackets;
    private WorldClient lastWorld;
    private INetHandler packetListener;

    @Override
    public void onEnable() {
        this.blockPackets = false;
        if (mc.theWorld != null && mc.thePlayer != null) {
            for (final Entity entity : mc.theWorld.loadedEntityList) {
                if (entity instanceof EntityLivingBase entityLivingBase) {
                    entityLivingBase.realPosX = entityLivingBase.serverPosX;
                    entityLivingBase.realPosZ = entityLivingBase.serverPosZ;
                    entityLivingBase.realPosY = entityLivingBase.serverPosY;
                }
            }
        }
    }

    @Override
    public void onDisable() {
        if (!this.packets.isEmpty() && this.packetListener != null) {
            this.resetPackets(this.packetListener);
        }
        this.packets.clear();
    }

    @EventLink
    public final Listener<RunTickEvent> runTickEventListener = runTickEvent -> {
        if(mc.theWorld == null || mc.thePlayer == null)
            return;

        entity = (EntityLivingBase) TargetHandler.getEntity(onlyKillAura.getValue());

        if (this.entity != null && this.packetListener != null) {
            final double d0 = this.entity.realPosX / 32.0;
            final double d2 = this.entity.realPosY / 32.0;
            final double d3 = this.entity.realPosZ / 32.0;
            final double d4 = this.entity.serverPosX / 32.0;
            final double d5 = this.entity.serverPosY / 32.0;
            final double d6 = this.entity.serverPosZ / 32.0;
            final float f = this.entity.width / 2.0f;
            final AxisAlignedBB entityServerPos = new AxisAlignedBB(d4 - f, d5, d6 - f, d4 + f, d5 + this.entity.height, d6 + f);
            final Vec3 positionEyes = mc.thePlayer.getPositionEyes(mc.timer.renderPartialTicks);
            final double currentX = MathHelper.clamp_double(positionEyes.xCoord, entityServerPos.minX, entityServerPos.maxX);
            final double currentY = MathHelper.clamp_double(positionEyes.yCoord, entityServerPos.minY, entityServerPos.maxY);
            final double currentZ = MathHelper.clamp_double(positionEyes.zCoord, entityServerPos.minZ, entityServerPos.maxZ);
            final AxisAlignedBB entityPosMe = new AxisAlignedBB(d0 - f, d2, d3 - f, d0 + f, d2 + this.entity.height, d3 + f);
            final double realX = MathHelper.clamp_double(positionEyes.xCoord, entityPosMe.minX, entityPosMe.maxX);
            final double realY = MathHelper.clamp_double(positionEyes.yCoord, entityPosMe.minY, entityPosMe.maxY);
            final double realZ = MathHelper.clamp_double(positionEyes.zCoord, entityPosMe.minZ, entityPosMe.maxZ);
            double distance = this.maximumRange.getValue();
            if (!mc.thePlayer.canEntityBeSeen(this.entity)) {
                distance = (Math.min(distance, 3.0));
            }
            final double collision = this.entity.getCollisionBorderSize();
            final double width = mc.thePlayer.width / 2.0f;
            final double mePosXForPlayer = mc.thePlayer.getLastServerPosition().xCoord + (mc.thePlayer.getSeverPosition().xCoord - mc.thePlayer.getLastServerPosition().xCoord) / MathHelper.clamp_int(mc.thePlayer.rotIncrement, 1, 3);
            final double mePosYForPlayer = mc.thePlayer.getLastServerPosition().yCoord + (mc.thePlayer.getSeverPosition().yCoord - mc.thePlayer.getLastServerPosition().yCoord) / MathHelper.clamp_int(mc.thePlayer.rotIncrement, 1, 3);
            final double mePosZForPlayer = mc.thePlayer.getLastServerPosition().zCoord + (mc.thePlayer.getSeverPosition().zCoord - mc.thePlayer.getLastServerPosition().zCoord) / MathHelper.clamp_int(mc.thePlayer.rotIncrement, 1, 3);
            AxisAlignedBB mePosForPlayerBox = new AxisAlignedBB(mePosXForPlayer - width, mePosYForPlayer, mePosZForPlayer - width, mePosXForPlayer + width, mePosYForPlayer + mc.thePlayer.height, mePosZForPlayer + width);
            mePosForPlayerBox = mePosForPlayerBox.expand(collision, collision, collision);
            final Vec3 entityPosEyes = new Vec3(d4, d5 + this.entity.getEyeHeight(), d6);
            final double bestX = MathHelper.clamp_double(entityPosEyes.xCoord, mePosForPlayerBox.minX, mePosForPlayerBox.maxX);
            final double bestY = MathHelper.clamp_double(entityPosEyes.yCoord, mePosForPlayerBox.minY, mePosForPlayerBox.maxY);
            final double bestZ = MathHelper.clamp_double(entityPosEyes.zCoord, mePosForPlayerBox.minZ, mePosForPlayerBox.maxZ);
            boolean b = entityPosEyes.distanceTo(new Vec3(bestX, bestY, bestZ)) > minimumRange.getValue() || (mc.thePlayer.hurtTime < 8 && mc.thePlayer.hurtTime > 3);
            if (b && positionEyes.distanceTo(new Vec3(realX, realY, realZ)) > positionEyes.distanceTo(new Vec3(currentX, currentY, currentZ)) && mc.thePlayer.getSeverPosition().distanceTo(new Vec3(d0, d2, d3)) < distance && !this.timeHelper.hasReached(this.maximumTime.getValue())) {
                this.blockPackets = true;
            }
            else {
                this.blockPackets = false;
                this.resetPackets(this.packetListener);
                this.timeHelper.reset();
            }
        }
    };

    @EventLink
    public final Listener<PacketEvent> packetEventListener = packetEvent -> {
        if (packetEvent.getINetHandler() != null) {
            this.packetListener = packetEvent.getINetHandler();
        }
        if (packetEvent.getDirection() != EnumPacketDirection.CLIENTBOUND) {
            return;
        }
        final Packet<?> p = packetEvent.getPacket();
        if (p instanceof S08PacketPlayerPosLook) {
            this.resetPackets(packetEvent.getINetHandler());
        }
        if (p instanceof S14PacketEntity packet) {
            final Entity entity1 = mc.theWorld.getEntityByID(packet.getEntityId());
            if (entity1 instanceof EntityLivingBase) {
                final EntityLivingBase entityLivingBase2;
                final EntityLivingBase entityLivingBase = entityLivingBase2 = (EntityLivingBase)entity1;
                entityLivingBase2.realPosX += packet.func_149062_c();
                entityLivingBase.realPosY += packet.func_149061_d();
                entityLivingBase.realPosZ += packet.func_149064_e();
            }
        }
        if (p instanceof S18PacketEntityTeleport packet2) {
            final Entity entity1 = mc.theWorld.getEntityByID(packet2.getEntityId());
            if (entity1 instanceof EntityLivingBase entityLivingBase) {
                entityLivingBase.realPosX = packet2.getX();
                entityLivingBase.realPosY = packet2.getY();
                entityLivingBase.realPosZ = packet2.getZ();
            }
        }
        if (this.entity == null) {
            this.resetPackets(packetEvent.getINetHandler());
            return;
        }
        if (mc.theWorld != null && mc.thePlayer != null) {
            if (this.lastWorld != mc.theWorld) {
                this.resetPackets(packetEvent.getINetHandler());
                this.lastWorld = mc.theWorld;
                return;
            }
            this.addPackets((Packet<INetHandler>) p, packetEvent);
        }
        this.lastWorld = mc.theWorld;
    };

    private void resetPackets(final INetHandler netHandler) {
        try {
            if (!this.packets.isEmpty()) {
                while (!this.packets.isEmpty()) {
                    final Packet<INetHandler> packet = this.packets.get(0);
                    try {
                        if (packet != null) {
                            packet.processPacket(netHandler);
                        }
                    }
                    catch (ThreadQuickExitException ignored) {}
                    this.packets.remove(packet);
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void addPackets(final Packet<INetHandler> packet, final PacketEvent eventReadPacket) {
        synchronized (this.packets) {
            if (this.delayPackets(packet)) {
                this.packets.add(packet);
                eventReadPacket.setCancelled(true);
            }
        }
    }

    private boolean delayPackets(final Packet<?> packet) {
        if (mc.currentScreen != null) {
            return false;
        }
        if (packet instanceof S03PacketTimeUpdate) {
            return true;
        } else if (packet instanceof S00PacketKeepAlive) {
            return true;
        } else if (packet instanceof S12PacketEntityVelocity || packet instanceof S27PacketExplosion) {
            return true;
        } else {
            return packet instanceof S32PacketConfirmTransaction || packet instanceof S14PacketEntity || packet instanceof S19PacketEntityStatus || packet instanceof S19PacketEntityHeadLook || packet instanceof S18PacketEntityTeleport || packet instanceof S0FPacketSpawnMob;
        }
    }
    
}
