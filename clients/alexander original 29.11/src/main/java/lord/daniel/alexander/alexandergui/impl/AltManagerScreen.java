package lord.daniel.alexander.alexandergui.impl;

import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import lord.daniel.alexander.alexandergui.AlexanderGuiScreen;
import lord.daniel.alexander.alexandergui.impl.altmanager.GuiCrackedLogin;
import lord.daniel.alexander.alexandergui.impl.altmanager.GuiMicrosoftOpenAuthLogin;
import lord.daniel.alexander.alexandergui.impl.altmanager.GuiMicrosoftWebLogin;
import lord.daniel.alexander.storage.impl.AltStorage;
import lord.daniel.alexander.util.math.time.TimeHelper;
import lord.daniel.alexander.util.render.RenderUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Written by Daniel. on 05/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
public class AltManagerScreen extends AlexanderGuiScreen {

    private String status = "Waiting...";

    private final TimeHelper actionTimer = new TimeHelper();

    @Override
    public void initGui() {
        super.initGui();

        this.buttonList.add(new GuiButton(1001, 105, 95, 150, 20, "Login Cracked"));
        this.buttonList.add(new GuiButton(1002, 105, 95 + 25, 150, 20, "Login Microsoft/Web"));
        this.buttonList.add(new GuiButton(1003, 105, 95 + 25 * 2, 150, 20, "Login Microsoft/OpenAuth"));

        this.buttonList.add(new GuiButton(1004, 105, 95 + 25 * 4, 150, 20, "Add Cracked"));
        this.buttonList.add(new GuiButton(1005, 105, 95 + 25 * 5, 150, 20, "Add Microsoft/OpenAuth"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if(actionTimer.hasReached(2500))
            status = "Waiting...";


        final float allowedHeight = this.height - 90;
        final float altHeight = (allowedHeight - 5) / 10;

        float startX = width - 130 - 5;
        float y = 95;

        for(AltStorage.Account account : AltStorage.getAltStorage().getList()) {
            if(y >= (height - 5)) {
                y = 94;
                startX -= 135;
            }

            RenderUtil.drawRect(startX, y, 130, altHeight - 5, RenderUtil.isHovered(mouseX, mouseY, startX, y, 130, altHeight - 5) ? new Color(0, 0, 0, 135) : new Color(0, 0, 0, 120));

            float nameX = 0;
            StringBuilder renderEmail = new StringBuilder();

            for(char c : account.email().toCharArray()) {
                if(nameX >= 115)
                    break;

                renderEmail.append(c);
                nameX += mc.fontRendererObj.getStringWidth(String.valueOf(c));
            }

            mc.fontRendererObj.drawStringWithShadow(renderEmail.toString(), startX + 3, y + 3, -1);

            if(!account.password().isEmpty()) {
                float passX = 0;
                StringBuilder renderPass = new StringBuilder();

                for(int i = 1; i <= account.password().length(); i++) {
                    if(passX >= 115)
                        break;

                    renderPass.append("*");
                    passX += mc.fontRendererObj.getStringWidth("*");
                }

                mc.fontRendererObj.drawStringWithShadow(renderPass.toString(), startX + 3, y + 3 + mc.fontRendererObj.FONT_HEIGHT + 2, -1);
            } else {
                mc.fontRendererObj.drawStringWithShadow(EnumChatFormatting.RED + "Cracked", startX + 3, y + 3 + mc.fontRendererObj.FONT_HEIGHT + 2, -1);
            }

            boolean hovered = RenderUtil.isHovered(mouseX, mouseY, startX + 130 - 3 - mc.fontRendererObj.getStringWidth("X"), y + altHeight - 6 - mc.fontRendererObj.FONT_HEIGHT, mc.fontRendererObj.getStringWidth("X"), mc.fontRendererObj.FONT_HEIGHT);
            mc.fontRendererObj.drawStringWithShadow( "X", startX + 130 - 3 - mc.fontRendererObj.getStringWidth("X"), y + altHeight - 6 - mc.fontRendererObj.FONT_HEIGHT, hovered ? Color.RED.getRGB() : -1);

            y += altHeight;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        final GuiScreen guiScreen = switch (button.id) {
            case 1001 -> new GuiCrackedLogin(true, this);
            case 1002 -> new GuiMicrosoftWebLogin(this);
            case 1003 -> new GuiMicrosoftOpenAuthLogin(true, this);
            case 1004 -> new GuiCrackedLogin(false, this);
            case 1005 -> new GuiMicrosoftOpenAuthLogin(false, this);
            default -> null;
        };

        if(guiScreen != null)
            mc.displayGuiScreen(guiScreen);

        super.actionPerformed(button);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        final float allowedHeight = this.height - 90;
        final float altHeight = (allowedHeight - 5) / 10;

        float startX = width - 130 - 5;
        float y = 95;

        final ArrayList<AltStorage.Account> delete = new ArrayList<>();

        for(AltStorage.Account account : AltStorage.getAltStorage().getList()) {

            boolean hovered = RenderUtil.isHovered(mouseX, mouseY, startX + 130 - 3 - mc.fontRendererObj.getStringWidth("X"), y + altHeight - 6 - mc.fontRendererObj.FONT_HEIGHT, mc.fontRendererObj.getStringWidth("X"), mc.fontRendererObj.FONT_HEIGHT);

            if(hovered) {
                delete.add(account);
                break;
            }

            if(RenderUtil.isHovered(mouseX, mouseY, startX, y, 130, altHeight - 5)) {
                if(account.password().isEmpty() || account.password().equals(" ")) {
                    mc.setSession(new Session(account.email(), "", "", "mojang"));
                    status = EnumChatFormatting.GREEN + "Logged in to " + account.email() + "!";
                    actionTimer.reset();
                } else {
                    MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
                    try {
                        MicrosoftAuthResult result = authenticator.loginWithCredentials(account.email(), account.password());
                        mc.setSession(new Session(result.getProfile().getName(), result.getProfile().getId(), result.getAccessToken(), "mojang"));
                        status = EnumChatFormatting.GREEN + "Logged in to " + result.getProfile().getName() + "!";
                        actionTimer.reset();
                    } catch (MicrosoftAuthenticationException e) {
                        e.printStackTrace();
                        status = EnumChatFormatting.RED + "Failed!";
                        actionTimer.reset();
                    }
                }
            }

            y += altHeight;
        }

        delete.forEach(account -> AltStorage.getAltStorage().remove(account));

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    @Override
    public String getName() {
        return "Alt Manager";
    }

}