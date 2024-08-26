package lord.daniel.alexander.alexandergui.impl.altmanager;

import lombok.RequiredArgsConstructor;
import lord.daniel.alexander.alexandergui.impl.altmanager.auth.LegacyAuth;
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
public class GuiCrackedLogin extends GuiScreen {
    private final boolean loggingIn;
    private final GuiScreen previousScreen;
    protected MainMenuModule mainMenuModule;
    private GuiTextField usernameField = null;

    public void initGui() {
        buttonList.add(new GuiButton(
                2, width / 2 - 102, height / 2 + 31, 100, 20, "Login"
        ));
        buttonList.add(new GuiButton(
                3, width / 2 + 2, height / 2 + 31, 100, 20, "Cancel"
        ));


        usernameField = new GuiTextField(
                0, fontRendererObj, width / 2 - 75, height / 2 - 10, 150, 20
        );
        usernameField.setFocused(true);
        usernameField.setMaxStringLength(16);

        Keyboard.enableRepeatEvents(true);
    }

    @Override
    public void updateScreen() {
        usernameField.updateCursorCounter();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float renderPartialTicks) {
        if (mainMenuModule == null)
            mainMenuModule = ModuleStorage.getModuleStorage().getByClass(MainMenuModule.class);

        mainMenuModule.getBackground().render();
        usernameField.drawTextBox();

        super.drawScreen(mouseX, mouseY, renderPartialTicks);

        if(usernameField.getText().isEmpty()) {
            mc.fontRendererObj.drawString("Username", usernameField.xPosition + 5, usernameField.yPosition + 5, -7829368);
        }

        drawCenteredString(
                fontRendererObj, "Cracked Login", width / 2, height / 2 - 40, -1
        );

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        usernameField.mouseClicked(mouseX, mouseY, mouseButton);
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
                if (usernameField != null) {
                    if (!usernameField.getText().contains(" ") && usernameField.getText() != "") {
                        if(loggingIn) {
                            final Session session = LegacyAuth.login(usernameField.getText());
                            mc.setSession(session);
                        } else {
                            AltStorage.getAltStorage().add(new AltStorage.Account(usernameField.getText(), ""));
                        }
                        this.mc.displayGuiScreen(previousScreen);
                    }
                }
            }
            break;
            default: {
                usernameField.textboxKeyTyped(typedChar, keyCode);
            }
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        super.onGuiClosed();
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 2) {
            if (usernameField != null) {
                if (!usernameField.getText().contains(" ") && !usernameField.getText().isEmpty()) {
                    if(loggingIn) {
                        final Session session = LegacyAuth.login(usernameField.getText());
                        mc.setSession(session);
                    } else {
                        AltStorage.getAltStorage().add(usernameField.getText(), "");
                    }
                    this.mc.displayGuiScreen(previousScreen);
                }
            }
        } else if (button.id == 3) {
            this.mc.displayGuiScreen(previousScreen);
        }
    }
}
