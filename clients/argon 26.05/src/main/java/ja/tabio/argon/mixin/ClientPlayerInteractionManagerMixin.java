package ja.tabio.argon.mixin;

import ja.tabio.argon.Argon;
import ja.tabio.argon.event.impl.AttackEvent;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
    public void injectOnAttack(PlayerEntity player, Entity entity, CallbackInfo callbackInfo) {
        final AttackEvent attackEvent = new AttackEvent(player, entity);
        Argon.getInstance().eventBus.post(attackEvent);

        if (attackEvent.cancelled)
            callbackInfo.cancel();
    }

}