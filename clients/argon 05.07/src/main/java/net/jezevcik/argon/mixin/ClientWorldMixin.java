package net.jezevcik.argon.mixin;

import net.jezevcik.argon.ParekClient;
import net.jezevcik.argon.event.impl.TickEntitiesEvent;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {

    @Inject(method = "tickEntities", at = @At("HEAD"))
    public void injectTickEntities(CallbackInfo callbackInfo) {
        ParekClient.getInstance().eventBus.post(new TickEntitiesEvent());
    }

}
