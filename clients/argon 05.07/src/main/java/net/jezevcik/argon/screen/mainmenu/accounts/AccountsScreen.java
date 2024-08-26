package net.jezevcik.argon.screen.mainmenu.accounts;

import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import meteordevelopment.orbit.EventHandler;
import net.jezevcik.argon.ParekClient;
import net.jezevcik.argon.mixin.MinecraftClientAccessor;
import net.jezevcik.argon.mixin.TextFieldWidgetAccessor;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.session.Session;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.UUID;

public class AccountsScreen extends Screen {

    public AccountsScreen() {
        super(Text.literal("Accounts"));
    }

    @Override
    public void init() {
        final int spacingY = 24;
        final int y = this.height / 2 - (spacingY * 3) / 2;

        final TextFieldWidget email = new TextFieldWidget(client.textRenderer, this.width / 2 - 100, y, 200, 20, Text.literal("E-Mail"));
        this.addDrawableChild(email);

        final TextFieldWidget pass = new TextFieldWidget(client.textRenderer, this.width / 2 - 100, y + spacingY, 200, 20, Text.literal("Password"));
        this.addDrawableChild(pass);

        ((TextFieldWidgetAccessor) pass).setMaxLength(9999);

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Login"), (button) -> {
            try {
                final MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
                final MicrosoftAuthResult result = authenticator.loginWithCredentials(email.getText(), pass.getText());
                final UUID uuid =  UUID.fromString(result.getProfile().getId().replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
                final Session session = new Session(result.getProfile().getName(), uuid, result.getAccessToken(), Optional.empty(), Optional.empty(), Session.AccountType.MSA);
                ((MinecraftClientAccessor) client).setSession(session);
            } catch (Exception e) {
                ParekClient.LOGGER.error("Failed to log in", e);
            }
        }).dimensions(this.width / 2 - 100, y + spacingY * 2, 200, 20).build());
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);


    }

}
