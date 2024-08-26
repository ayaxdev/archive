package net.jezevcik.argon.screen.mainmenu;

import com.mojang.authlib.minecraft.BanDetails;
import net.jezevcik.argon.renderer.UiBuilder;
import net.jezevcik.argon.screen.mainmenu.accounts.AccountsScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.AccessibilityOnboardingButtons;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextIconButtonWidget;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.text.Text;

public class MainMenuScreen extends Screen {

    public MainMenuScreen() {
        super(Text.literal("Title"));
    }

    @Override
    public void init() {
        assert this.client != null;

        final int spacingY = 24;
        final int y = this.height / 2 - (spacingY * 5) / 2;

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.singleplayer"), (button) -> {
            this.client.setScreen(new SelectWorldScreen(this));
        }).dimensions(this.width / 2 - 100, y, 200, 20).build());

        final Text disabledMultiplayerText = this.getMultiplayerDisabledText();
        final boolean multiplayerEnabled = disabledMultiplayerText == null;
        final Tooltip tooltip = disabledMultiplayerText != null ? Tooltip.of(disabledMultiplayerText) : null;

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.multiplayer"), (button) -> {
            this.client.setScreen(new MultiplayerScreen(this));
        }).dimensions(this.width / 2 - 100, y + spacingY, 200, 20).tooltip(tooltip).build()).active = multiplayerEnabled;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Accounts"), (button) -> {
            this.client.setScreen(new AccountsScreen());
        }).dimensions(this.width / 2 - 100, y + spacingY * 2, 200, 20).tooltip(tooltip).build());

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.online"), (buttonWidget) -> {
            this.client.setScreen(new RealmsMainScreen(this));
        }).dimensions(this.width / 2 - 100, y + spacingY * 3, 200, 20).tooltip(tooltip).build()).active = multiplayerEnabled;

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.options"), (button) -> {
            this.client.setScreen(new OptionsScreen(this, this.client.options));
        }).dimensions(this.width / 2 - 100, y + spacingY * 4, 98, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.quit"), (button) -> {
            this.client.scheduleStop();
        }).dimensions(this.width / 2 + 2, y + spacingY * 4, 98, 20).build());

        final TextIconButtonWidget languageButton = this.addDrawableChild(AccessibilityOnboardingButtons.createLanguageButton(20, (button) -> {
            this.client.setScreen(new LanguageOptionsScreen(this, this.client.options, this.client.getLanguageManager()));
        }, true));

        languageButton.setPosition(2, this.height - 22);

        final TextIconButtonWidget kriplButton = this.addDrawableChild(AccessibilityOnboardingButtons.createAccessibilityButton(20, (button) -> {
            this.client.setScreen(new AccessibilityOptionsScreen(this, this.client.options));
        }, true));

        kriplButton.setPosition(24, this.height - 22);
    }

    private Text getMultiplayerDisabledText() {
        assert this.client != null;

        if (this.client.isMultiplayerEnabled()) {
            return null;
        } else if (this.client.isUsernameBanned()) {
            return Text.translatable("title.multiplayer.disabled.banned.name");
        } else {
            BanDetails banDetails = this.client.getMultiplayerBanDetails();
            if (banDetails != null) {
                return banDetails.expires() != null ? Text.translatable("title.multiplayer.disabled.banned.temporary") : Text.translatable("title.multiplayer.disabled.banned.permanent");
            } else {
                return Text.translatable("title.multiplayer.disabled");
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        assert this.client != null;

        final UiBuilder uiBuilder = new UiBuilder(context);

        final float scale = 5;

        final float width = client.textRenderer.getWidth("Argon")
                , height = client.textRenderer.fontHeight;

        uiBuilder.push();

        uiBuilder.translate(this.width / 2f - (width * scale) / 2f
                , this.height / 4f - (height * scale) / 2f);
        uiBuilder.scale(scale, scale);

        uiBuilder.text("Argon", 0, 0, -1, true);

        uiBuilder.pop();
    }

}
