package net.jezevcik.argon.mixin;

import net.jezevcik.argon.ParekClient;
import net.jezevcik.argon.event.impl.ChatEvent;
import net.jezevcik.argon.event.impl.RotationSetEvent;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

    @Shadow public abstract void clearWorld();

    @Inject(method = "onGameJoin", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;setYaw(F)V"))
    public void onFlipPlayer(GameJoinS2CPacket packet, CallbackInfo ci) {
        ParekClient.getInstance().eventBus.post(new RotationSetEvent(-180, 0, true, false));
    }

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void sendChatMessageHook(String message, CallbackInfo ci) {
        final ChatEvent chatEvent = new ChatEvent(message);
        ParekClient.getInstance().eventBus.post(chatEvent);

        if (chatEvent.cancelled)
            ci.cancel();
    }
}