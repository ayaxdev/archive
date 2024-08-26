package ja.tabio.argon.mixin;

import ja.tabio.argon.Argon;
import net.minecraft.client.main.Main;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public class MainMixin {

    @Inject(method = "main([Ljava/lang/String;)V", at = @At("HEAD"))
    private static void inject(String[] args, CallbackInfo callbackInfo) {
        Argon.Static.args = args;
    }

    @Redirect(method = "main([Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;)V", remap = false))
    private static void interceptLogger(Logger instance, String s, Object o) {
        if (!s.toLowerCase().contains("ignored arguments"))
            instance.info(s, o);
    }

}
