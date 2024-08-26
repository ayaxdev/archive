package lord.daniel.alexander.alexandergui;

import lord.daniel.alexander.Modification;
import lord.daniel.alexander.alexandergui.impl.*;
import lord.daniel.alexander.handler.game.SessionHandler;
import lord.daniel.alexander.module.impl.hud.MainMenuModule;
import lord.daniel.alexander.storage.impl.ModuleStorage;
import lord.daniel.alexander.util.render.RenderUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;

/**
 * Written by Daniel. on 05/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
public abstract class AlexanderGuiScreen extends GuiScreen {

    protected MainMenuModule mainMenuModule;

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(1, 5, 90, 90, 20, "Alt Manager"));
        this.buttonList.add(new GuiButton(2, 5, 90 + 24, 90, 20, "Proxy Manager"));
        this.buttonList.add(new GuiButton(3, 5, 90 + 24 * 2, 90, 20, "Protocol"));
        this.buttonList.add(new GuiButton(4, 5, 90 + 24 * 3, 90, 20, "Changelog"));
        this.buttonList.add(new GuiButton(5, 5, 90 + 24 * 4, 90, 20, "Background"));

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (mainMenuModule == null)
            mainMenuModule = ModuleStorage.getModuleStorage().getByClass(MainMenuModule.class);

        mainMenuModule.getBackground().render();

        RenderUtil.drawRect(100, 0, this.width - 100, 90, new Color(0, 0, 0, 120));
        RenderUtil.drawRect(0, 0, 100, this.height, new Color(0, 0, 0, 120));

        final ScaledResolution sr = new ScaledResolution(mc);

        GL11.glPushMatrix();
        GL11.glScaled(3.0, 3.0, 1.0);
        fontRendererObj.drawStringWithShadow(getName(),  100 / 3f + (this.width - 100) / 3.0f / 2.0f - this.fontRendererObj.getStringWidth(getName()) / 2.0f, sr.getScaledHeight() / 15f / 3.0f, -1);
        GlStateManager.scale(3.0f, 3.0f, 1.0f);
        GL11.glPopMatrix();

        // TODO: ViaMCP
        String protocol = "1.8.x";

        String[] ipParts = Modification.INSTANCE.getIp().split("\\.");

        for(int i = 0; i < ipParts.length; i++) {
            ipParts[i] = ipParts[i].substring(0, ipParts[i].length() - 1);
        }

        String renderIp = String.join("x.", ipParts);

        mc.fontRendererObj.drawStringWithShadow("Account: " + (SessionHandler.microsoft ? EnumChatFormatting.GREEN.toString() : EnumChatFormatting.RED.toString()) + mc.getSession().getUsername(), 1, 1, -1);
        mc.fontRendererObj.drawStringWithShadow("Proxy: " + (EnumChatFormatting.RED.toString()) + "Disconnected", 1, 2 + mc.fontRendererObj.FONT_HEIGHT, -1);
        mc.fontRendererObj.drawStringWithShadow("Protocol: " + (protocol.equals("1.8.x") ? EnumChatFormatting.GREEN.toString() : EnumChatFormatting.RED.toString()) + protocol, 1, 3 + 2 * mc.fontRendererObj.FONT_HEIGHT, -1);
        mc.fontRendererObj.drawStringWithShadow("IP: " + renderIp, 1, 4 + 3 * mc.fontRendererObj.FONT_HEIGHT, -1);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        GuiScreen screen = switch (button.id) {
            case 1 -> new AltManagerScreen();
            case 2 -> new ProxyManagerScreen();
            case 3 -> new ProtocolScreen();
            case 4 -> new ChangelogScreen();
            case 5 -> new BackgroundScreen();
            default -> null;
        };

        if(screen != null)
            mc.displayGuiScreen(screen);
    }

    public abstract String getName();

}