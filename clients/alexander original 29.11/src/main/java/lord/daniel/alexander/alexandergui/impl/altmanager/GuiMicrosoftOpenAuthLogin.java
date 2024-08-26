package lord.daniel.alexander.alexandergui.impl.altmanager;

import lombok.RequiredArgsConstructor;
import lord.daniel.alexander.alexandergui.impl.altmanager.auth.LegacyAuth;
import lord.daniel.alexander.alexandergui.impl.altmanager.auth.MicrosoftOpenAuthAuth;
import lord.daniel.alexander.module.impl.hud.MainMenuModule;
import lord.daniel.alexander.storage.impl.AltStorage;
import lord.daniel.alexander.storage.impl.ModuleStorage;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

@RequiredArgsConstructor
public class GuiMicrosoftOpenAuthLogin extends GuiScreen {
    private final boolean loggingIn;
    private final GuiScreen previousScreen;
    protected MainMenuModule mainMenuModule;
    private GuiTextField emailField = null, passwordField = null;

    public void initGui() {
        buttonList.add(new GuiButton(
                3, width / 2 - 102, height / 2 + 53, 100, 20, "Login"
        ));
        buttonList.add(new GuiButton(
                4, width / 2 + 2, height / 2 + 53, 100, 20, "Cancel"
        ));


        emailField = new GuiTextField(
                0, fontRendererObj, width / 2 - 75, height / 2 - 10, 150, 20
        );

        passwordField = new GuiTextField(
                1, fontRendererObj, width / 2 - 75, height / 2 + 13, 150, 20
        );

        emailField.setFocused(true);
        emailField.setMaxStringLength(16);

        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void updateScreen() {
        emailField.updateCursorCounter();
        passwordField.updateCursorCounter();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float renderPartialTicks) {
        if (mainMenuModule == null)
            mainMenuModule = ModuleStorage.getModuleStorage().getByClass(MainMenuModule.class);

        mainMenuModule.getBackground().render();
        emailField.drawTextBox();
        passwordField.drawTextBox();

        if(emailField.getText().isEmpty()) {
            mc.fontRendererObj.drawString("Email", emailField.xPosition + 5, emailField.yPosition + 5, -7829368);
        }
        if(passwordField.getText().isEmpty()) {
            mc.fontRendererObj.drawString("Password", passwordField.xPosition + 5, passwordField.yPosition + 5, -7829368);
        }

        super.drawScreen(mouseX, mouseY, renderPartialTicks);

        drawCenteredString(
                fontRendererObj, "OpenAuth Login", width / 2, height / 2 - 40, -1
        );

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        emailField.mouseClicked(mouseX, mouseY, mouseButton);
        passwordField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        switch (keyCode) {
            case Keyboard.KEY_ESCAPE: {
                this.mc.displayGuiScreen(previousScreen);
            }
            break;
            case Keyboard.KEY_RETURN: {
                if (emailField != null) {
                    if (!emailField.getText().contains(" ") && emailField.getText() != "") {
                        if(loggingIn) {
                            final Session session = LegacyAuth.login(emailField.getText());
                            mc.setSession(session);
                        } else {
                            AltStorage.getAltStorage().add(new AltStorage.Account(emailField.getText(), ""));
                        }
                        this.mc.displayGuiScreen(previousScreen);
                    }
                }
            }
            break;
            default: {
                emailField.textboxKeyTyped(typedChar, keyCode);
                passwordField.textboxKeyTyped(typedChar, keyCode);
            }
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        super.onGuiClosed();
    }


    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 3) {
            if (emailField != null) {
                if (!emailField.getText().contains(" ") && !emailField.getText().isEmpty()) {
                    if(loggingIn) {
                        final Session session = MicrosoftOpenAuthAuth.login(emailField.getText(), passwordField.getText());
                        mc.setSession(session);
                    } else {
                        AltStorage.getAltStorage().add(emailField.getText(), passwordField.getText());
                    }
                    this.mc.displayGuiScreen(previousScreen);
                }
            }
        } else if (button.id == 4) {
            this.mc.displayGuiScreen(previousScreen);
        }
    }
}
