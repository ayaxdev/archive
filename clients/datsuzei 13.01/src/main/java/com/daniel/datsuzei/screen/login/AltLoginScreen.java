package com.daniel.datsuzei.screen.login;

import com.daniel.datsuzei.DatsuzeiClient;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.Session;

import java.io.IOException;

public class AltLoginScreen extends GuiScreen {

    private GuiTextField usernameField, passwordField;
    private String status = "Waiting";

    @Override
    public void initGui() {
        this.usernameField = new GuiTextField(0, Minecraft.getMinecraft().fontRendererObj, 5, 5, 100, 20);
        this.passwordField = new GuiTextField(1, Minecraft.getMinecraft().fontRendererObj, 5, 35, 100, 20);
        this.buttonList.add(new GuiButton(2, 5, 60, "Login"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawBackground(0);

        this.usernameField.drawTextBox();
        this.passwordField.drawTextBox();

        mc.fontRendererObj.drawStringWithShadow(mc.session.getUsername(), 110, 5, -1);
        mc.fontRendererObj.drawStringWithShadow(status, 110, 16, -1);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if(button.id == 2) {
            if(passwordField.getText() == null || passwordField.getText().isEmpty()) {
                mc.session = new Session(this.usernameField.getText(), "", "", "mojang");
                status = "Success";
            } else {
                MicrosoftAuthenticator microsoftAuthenticator = new MicrosoftAuthenticator();
                try {
                    MicrosoftAuthResult result = microsoftAuthenticator.loginWithCredentials(this.usernameField.getText(), this.passwordField.getText());
                    mc.session = new Session(result.getProfile().getName(), result.getProfile().getId(), result.getAccessToken(), "mojang");
                    status = "Success";
                } catch (MicrosoftAuthenticationException e) {
                    DatsuzeiClient.getSingleton().getLogger().error("There was an error logging in:", e);
                    status = "Error";
                }
            }
        }
    }

    @Override
    public void keyTyped(char character, int keyCode) throws IOException {
        super.keyTyped(character, keyCode);

        this.usernameField.textboxKeyTyped(character, keyCode);
        this.passwordField.textboxKeyTyped(character, keyCode);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int action) throws IOException {
        super.mouseClicked(mouseX, mouseY, action);

        this.usernameField.mouseClicked(mouseX, mouseY, action);
        this.passwordField.mouseClicked(mouseX, mouseY, action);
    }

}
