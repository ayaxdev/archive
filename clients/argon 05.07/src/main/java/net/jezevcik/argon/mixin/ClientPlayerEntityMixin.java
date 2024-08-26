package net.jezevcik.argon.mixin;

import com.mojang.authlib.GameProfile;
import net.jezevcik.argon.ParekClient;
import net.jezevcik.argon.event.impl.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.ClientPlayerTickable;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Shadow @Final protected MinecraftClient client;
    @Shadow @Final private List<ClientPlayerTickable> tickables;

    @Unique private ServerPlayerTickEvent argon_lastServerTick;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V", shift = At.Shift.BEFORE), cancellable = true)
    public void injectPreLocalUpdate(CallbackInfo callbackInfo) {
        final LocalPlayerTickEvent localPlayerTickEvent = new LocalPlayerTickEvent(true);
        ParekClient.getInstance().eventBus.post(localPlayerTickEvent);

        if (localPlayerTickEvent.cancelled)
            callbackInfo.cancel();
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V", shift = At.Shift.AFTER), cancellable = true)
    public void injectPostLocalUpdate(CallbackInfo callbackInfo) {
        final LocalPlayerTickEvent localPlayerTickEvent = new LocalPlayerTickEvent(false);
        ParekClient.getInstance().eventBus.post(localPlayerTickEvent);

        if (localPlayerTickEvent.cancelled)
            callbackInfo.cancel();

        ParekClient.getInstance().eventBus.post(argon_lastServerTick = new ServerPlayerTickEvent(true));

        if (argon_lastServerTick.cancelled && client.player.hasVehicle()) {
            callbackInfo.cancel();

            final Iterator var3 = this.tickables.iterator();

            while(var3.hasNext()) {
                ClientPlayerTickable clientPlayerTickable = (ClientPlayerTickable)var3.next();
                clientPlayerTickable.tick();
            }
        }
    }

    @Inject(method = "sendMovementPackets", at = @At("HEAD"), cancellable = true)
    public void injectServerTickCancel(CallbackInfo callbackInfo) {
        if (argon_lastServerTick != null && argon_lastServerTick.cancelled && argon_lastServerTick.pre) {
            callbackInfo.cancel();
            argon_lastServerTick = null;
        }
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Ljava/util/List;iterator()Ljava/util/Iterator;", shift = At.Shift.BEFORE))
    public void injectPostServerUpdate(CallbackInfo callbackInfo) {
        ParekClient.getInstance().eventBus.post(new ServerPlayerTickEvent(false));
    }

    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getYaw()F"))
    public float onGetYaw(ClientPlayerEntity instance) {
        SendYawEvent sendYawEvent = new SendYawEvent(instance.getYaw());
        ParekClient.getInstance().eventBus.post(sendYawEvent);
        return sendYawEvent.yaw;
    }

    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getPitch()F"))
    public float onGetPitch(ClientPlayerEntity instance) {
        SendPitchEvent sendPitchEvent = new SendPitchEvent(instance.getPitch());
        ParekClient.getInstance().eventBus.post(sendPitchEvent);
        return sendPitchEvent.pitch;
    }

    @Override
    protected void setRotation(float yaw, float pitch) {
        super.setRotation(yaw, pitch);
        ParekClient.getInstance().eventBus.post(new RotationSetEvent(getYaw(), getPitch(), true, true));
    }

    @Override
    public void move(MovementType movementType, Vec3d movement) {
        if (movementType == MovementType.SELF) {
            final MovementEvent movementEvent = new MovementEvent(movement.x, movement.y, movement.z);
            ParekClient.getInstance().eventBus.post(movementEvent);

            if (!movementEvent.cancelled)
                super.move(movementType, new Vec3d(movementEvent.velocityX, movementEvent.velocityY, movementEvent.velocityZ));
        }
    }

}
