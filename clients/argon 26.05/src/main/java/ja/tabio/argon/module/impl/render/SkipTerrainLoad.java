package ja.tabio.argon.module.impl.render;

import io.github.racoondog.norbit.EventHandler;
import ja.tabio.argon.event.impl.SetScreenEvent;
import ja.tabio.argon.event.impl.WorldResetScreenEvent;
import ja.tabio.argon.mixin.ReconfiguringScreenAccessor;
import ja.tabio.argon.module.Module;
import ja.tabio.argon.module.annotation.RegisterModule;
import ja.tabio.argon.module.enums.ModuleCategory;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.gui.screen.ReconfiguringScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;

@RegisterModule
public class SkipTerrainLoad extends Module {

    public SkipTerrainLoad() {
        super(ModuleParams.builder()
                .name("SkipTerrainLoad")
                .category(ModuleCategory.RENDER)
                .build());
    }

    @EventHandler
    public final void onSetScreen(SetScreenEvent setScreenEvent) {
        if (setScreenEvent.screen instanceof DownloadingTerrainScreen) {
            setScreenEvent.screen = null;
        } else if (setScreenEvent.screen instanceof ReconfiguringScreen reconfiguringScreen) {
            final ReconfiguringScreenAccessor accessor = (ReconfiguringScreenAccessor) reconfiguringScreen;
            setScreenEvent.screen = new ReconfigBridgeScreen(accessor.getConnection());
        }
    }

    @EventHandler
    public final void onWorldReset(WorldResetScreenEvent worldResetScreenEvent) {
        worldResetScreenEvent.screen = new JoiningWorldBridgeScreen();
    }

    public static final class ReconfigBridgeScreen extends Screen {
        private final ClientConnection connection;

        public ReconfigBridgeScreen(final ClientConnection connection) {
            super(Text.literal("weee"));
            this.connection = connection;
        }

        @Override
        public void render(final DrawContext drawContext, final int i, final int j, final float f) {
        }

        @Override
        public void renderBackground(final DrawContext drawContext, final int i, final int j, final float f) {
        }

        @Override
        public void tick() {
            if (connection == null)
                return;

            if (this.connection.isOpen()) {
                this.connection.tick();
            } else {
                this.connection.handleDisconnection();
            }
        }
    }

    public static final class JoiningWorldBridgeScreen extends Screen {

        public JoiningWorldBridgeScreen() {
            super(Text.literal("wooo"));
        }

        @Override
        public void render(final DrawContext drawContext, final int i, final int j, final float f) {
        }

        @Override
        public void renderBackground(final DrawContext drawContext, final int i, final int j, final float f) {
        }
    }

}
