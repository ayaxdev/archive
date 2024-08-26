package ja.tabio.argon.mixin;

import com.mojang.authlib.GameProfile;
import ja.tabio.argon.Argon;
import ja.tabio.argon.event.enums.PlayerType;
import ja.tabio.argon.event.enums.Stage;
import ja.tabio.argon.event.impl.*;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import org.apache.commons.compress.harmony.pack200.NewAttributeBands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V", shift = At.Shift.BEFORE))
    public void injectLocalPlayerPreTick(CallbackInfo callbackInfo) {
        Argon.getInstance().eventBus.post(new PlayerTickEvent(Stage.PRE, PlayerType.LOCAL));
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V", shift = At.Shift.AFTER))
    public void injectLocalPlayerPostTick(CallbackInfo callbackInfo) {
        Argon.getInstance().eventBus.post(new PlayerTickEvent(Stage.POST, PlayerType.LOCAL));
    }

    @Inject(method = "sendMovementPackets", at = @At("HEAD"))
    public void injectServerPlayerPreTick(CallbackInfo callbackInfo) {
        Argon.getInstance().eventBus.post(new PlayerTickEvent(Stage.PRE, PlayerType.SERVER));
    }

    @Inject(method = "sendMovementPackets", at = @At("TAIL"))
    public void injectServerPlayerPostTick(CallbackInfo callbackInfo) {
        Argon.getInstance().eventBus.post(new PlayerTickEvent(Stage.POST, PlayerType.SERVER));
    }

    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getYaw()F"))
    public float onGetYaw(ClientPlayerEntity instance) {
        SendYawEvent sendYawEvent = new SendYawEvent(instance.getYaw());
        Argon.getInstance().eventBus.post(sendYawEvent);
        return sendYawEvent.yaw;
    }

    @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getPitch()F"))
    public float onGetPitch(ClientPlayerEntity instance) {
        SendPitchEvent sendPitchEvent = new SendPitchEvent(instance.getPitch());
        Argon.getInstance().eventBus.post(sendPitchEvent);
        return sendPitchEvent.pitch;
    }

    @Override
    protected void setRotation(float yaw, float pitch) {
        super.setRotation(yaw, pitch);
        Argon.getInstance().eventBus.post(new RotationSetEvent(getYaw(), getPitch(), true, true));
    }

}
