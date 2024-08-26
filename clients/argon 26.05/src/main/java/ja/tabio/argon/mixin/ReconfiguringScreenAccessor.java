package ja.tabio.argon.mixin;

import net.minecraft.client.gui.screen.ReconfiguringScreen;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ReconfiguringScreen.class)
public interface ReconfiguringScreenAccessor {

    @Accessor("connection")
    ClientConnection getConnection();

}
