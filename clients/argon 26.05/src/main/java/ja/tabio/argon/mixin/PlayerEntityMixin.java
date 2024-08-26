package ja.tabio.argon.mixin;

import ja.tabio.argon.Argon;
import ja.tabio.argon.event.impl.EntityReachEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "getEntityInteractionRange", at = @At("TAIL"), cancellable = true)
    public void injectReach(CallbackInfoReturnable<Double> returnable) {
        if (this == ((Object) MinecraftClient.getInstance().player)) {
            final EntityReachEvent entityReachEvent = new EntityReachEvent(returnable.getReturnValue());
            Argon.getInstance().eventBus.post(entityReachEvent);
            returnable.setReturnValue(entityReachEvent.reach);
            returnable.cancel();
        }
    }

}
