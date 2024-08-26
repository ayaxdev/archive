package com.skidding.atlas.screen.alts;

import com.skidding.atlas.AtlasClient;
import com.skidding.atlas.account.AccountFeature;
import com.skidding.atlas.account.AccountManager;
import com.skidding.atlas.account.util.LoginResult;
import com.skidding.atlas.font.ClientFontRenderer;
import com.skidding.atlas.font.FontManager;
import com.skidding.atlas.screen.elements.ButtonElement;
import com.skidding.atlas.screen.elements.TextFieldElement;
import com.skidding.atlas.util.animation.Animation;
import com.skidding.atlas.util.animation.Direction;
import com.skidding.atlas.util.animation.impl.DecelerateAnimation;
import com.skidding.atlas.util.math.MathUtil;
import com.skidding.atlas.util.math.random.RandomValueUtil;
import com.skidding.atlas.util.minecraft.game.SessionUtil;
import com.skidding.atlas.util.render.DrawUtil;
import com.skidding.atlas.util.render.color.ColorUtil;
import com.skidding.atlas.util.system.FileUtil;
import de.florianmichael.rclasses.math.integration.Boundings;
import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AltManagerUI extends GuiScreen {
    private final ClientFontRenderer roboto36 = FontManager.getSingleton().get("Roboto", 36);
    private final ClientFontRenderer roboto20 = FontManager.getSingleton().get("Roboto", 20);

    public Animation xPos = new DecelerateAnimation(1000, 0, Direction.FORWARDS);
    public Animation yPos = new DecelerateAnimation(1000, 0, Direction.FORWARDS);
    public List<ButtonElement> guiButtons = new ArrayList<>();
    public List<TextFieldElement> guiTextFields = new ArrayList<>();

    private String status = "waiting...";

    public void initGui() {
        this.xPos.reset();
        this.yPos.reset();

        this.guiButtons.clear();
        this.guiButtons.add(new ButtonElement(-1, 0, 42, "Add"));
        this.guiButtons.add(new ButtonElement(0, 0, 64, "Login"));
        this.guiButtons.add(new ButtonElement(1, 0, 86, "Random cracked alt"));
        this.guiButtons.add(new ButtonElement(2, 0, 108, "Load from .txt file"));
        this.guiButtons.add(new ButtonElement(3, 0, 130, "Back"));

        this.guiTextFields.add(new TextFieldElement(0, 0, -9, "", "Email/username"));
        this.guiTextFields.add(new TextFieldElement(0, 0, 13, "", "Password"));
        Keyboard.enableRepeatEvents(true);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        final ScaledResolution sr = new ScaledResolution(this.mc);

        xPos.endPoint = sr.getScaledWidth_double();
        yPos.endPoint = sr.getScaledHeight_double() / 4 * 3;

        Gui.drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), new Color(25, 25, 25).getRGB());

        float maximumWidth = 100;

        for(final AccountFeature accountFeature : AccountManager.getSingleton().getFeatures()) {
            final float nameWidth = roboto20.getStringWidth(accountFeature.getName().replace("<--:-->", " : ")) + 14;
            final float typeWidth = roboto20.getStringWidth(accountFeature.getType()) + 4;
            final float removeWidth = roboto20.getStringWidth("Remove") + 9;
            final float loginWidth = roboto20.getStringWidth("Login") + 9;

            maximumWidth = MathUtil.max(maximumWidth, nameWidth, typeWidth, removeWidth, loginWidth);
        }

        final float accountX = (float) (xPos.getOutput() - sr.getScaledWidth_double() + 12);
        float accountY = (float) (yPos.getOutput() - (sr.getScaledHeight_double() / 4 * 3) + 5.5f);

        for(final AccountFeature accountFeature : AccountManager.getSingleton().getFeatures()) {
            final float singularHeight = roboto20.getHeight();
            final float height = singularHeight * 4 + 2;

            DrawUtil.drawRectRelative(accountX - 7, accountY, maximumWidth, height, new Color(20, 20, 20, 100).getRGB());

            accountY += 1;

            roboto20.drawString(accountFeature.getName().replace("<--:-->", " : "), 7, accountY, Boundings.isInBounds(mouseX, mouseY, accountX - 7, accountY - 1, maximumWidth, height) ? new Color(255,255,255).getRGB() : new Color(200,200,200).getRGB());
            accountY += singularHeight;

            roboto20.drawString(accountFeature.getType(), accountX - 5, accountY, Boundings.isInBounds(mouseX, mouseY, accountX - 7, accountY - 1 - singularHeight, maximumWidth, height) ? new Color(255,255,255).getRGB() : new Color(200,200,200).getRGB());
            accountY += singularHeight;

            roboto20.drawString("Login", accountX, accountY, Boundings.isInBounds(mouseX, mouseY, accountX, accountY, roboto20.getStringWidth("Remove"), roboto20.getHeight()) ? new Color(0,255,0).getRGB() : Boundings.isInBounds(mouseX, mouseY, accountX - 7, accountY - 1 - singularHeight - singularHeight, maximumWidth, height) ? new Color(255,255,255).getRGB() : new Color(200,200,200).getRGB());
            accountY += singularHeight;

            roboto20.drawString("Remove", accountX, accountY, Boundings.isInBounds(mouseX, mouseY, accountX, accountY, roboto20.getStringWidth("Remove"), roboto20.getHeight()) ? new Color(255,0,0).getRGB() : Boundings.isInBounds(mouseX, mouseY, accountX - 7, accountY - 1 - singularHeight - singularHeight - singularHeight, maximumWidth, height) ? new Color(255,255,255).getRGB() : new Color(200,200,200).getRGB());
            accountY += singularHeight;

            accountY += 1;

            // Margin
            accountY += 5;
        }

        roboto36.drawCenteredString("Alt Manager", (float)(this.xPos.getOutput() / 2.0), (float)(this.yPos.getOutput() / 2.0 - 35.0), ColorUtil.getRainbow(4.0f, 0.5f, 1.0f).getRGB());
        roboto20.drawCenteredString(status, (float)(this.xPos.getOutput() / 2.0), (float)(this.yPos.getOutput() + 45), Color.LIGHT_GRAY.getRGB());

        for (ButtonElement btn : this.guiButtons) {
            btn.draw(mouseX, mouseY, (int)(this.xPos.getOutput() / 2.0 - 100.0), (int)(this.yPos.getOutput() / 2.0));
        }

        for (TextFieldElement field : this.guiTextFields) {
            field.draw(mouseX, mouseY, (int)(this.xPos.getOutput() / 2.0 - 100.0), (int)(this.yPos.getOutput() / 2.0));
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        final ScaledResolution sr = new ScaledResolution(this.mc);

        final float accountX = (float) (xPos.getOutput() - sr.getScaledWidth_double() + 12);
        float accountY = (float) (yPos.getOutput() - (sr.getScaledHeight_double() / 4 * 3) + 5.5f);

        for(int i = 0; i < AccountManager.getSingleton().getFeatures().size(); i++) {
            final AccountFeature accountFeature = AccountManager.getSingleton().getFeatures().get(i);

            accountY += 1;

            final float singularHeight = roboto20.getHeight();

            accountY += singularHeight * 2;

            if(Boundings.isInBounds(mouseX, mouseY, accountX, accountY, roboto20.getStringWidth("Login"), roboto20.getHeight())) {
                final LoginResult loginResult = accountFeature.login();

                status = switch (loginResult.result()) {
                    case SUCCESS -> {
                        mc.session = loginResult.session();
                        yield STR."logged in as: \{accountFeature.getName()}";
                    }
                    case INVALID_ARGUMENTS -> {
                        if(loginResult.cause() != null)
                            AtlasClient.getInstance().logger.error("Login fail", loginResult.cause());
                        yield  "Failed to login due to invalid arguments!";
                    }
                    default -> {
                        if(loginResult.cause() != null)
                            AtlasClient.getInstance().logger.error("Login fail", loginResult.cause());
                        yield  "Failed to login!";
                    }
                };
            }

            accountY += singularHeight;

            if(Boundings.isInBounds(mouseX, mouseY, accountX, accountY, roboto20.getStringWidth("Remove"), roboto20.getHeight()))
                AccountManager.getSingleton().getMap().remove(STR."\{accountFeature.getType()}:\{accountFeature.getName()}");

            accountY += singularHeight;

            accountY += 1;

            // Margin
            accountY += 5;
        }

        for (ButtonElement btn : this.guiButtons) {
            if (btn.click(mouseX, mouseY, mouseButton)) {
                switch (btn.displayString) {
                    case "Add" -> {
                        final String username = guiTextFields.getFirst().displayString;
                        final String password = guiTextFields.getLast().displayString;

                        if (username == null || username.isEmpty()) {
                            status = "username cannot be empty!";
                            return;
                        }

                        if (password == null || password.isEmpty()) {
                            AccountManager.getSingleton().addByType("Cracked", username);
                            status = STR."Added: \{username}";
                        } else {
                            AccountManager.getSingleton().addByType("OpenAuth", username, password);
                            status = STR."Added: \{username}:\{password}";
                        }
                    }
                    case "Login" -> {
                        final String username = guiTextFields.getFirst().displayString;
                        final String password = guiTextFields.getLast().displayString;

                        if (username == null || username.isEmpty()) {
                            status = "username cannot be empty!";
                            return;
                        }

                        if (password == null || password.isEmpty()) {
                            mc.session = new Session(username, "", "", "mojang");
                            status = STR."logged in as: \{username}";
                        } else {
                            new Thread(() -> {
                                MicrosoftAuthenticator microsoftAuthenticator = new MicrosoftAuthenticator();
                                try {
                                    MicrosoftAuthResult microsoftAuthResult = microsoftAuthenticator.loginWithCredentials(username, password);
                                    mc.session = new Session(microsoftAuthResult.getProfile().getName(), microsoftAuthResult.getProfile().getId(), microsoftAuthResult.getAccessToken(), "mojang");
                                    status = STR."logged in as: \{microsoftAuthResult.getProfile().getName()}";
                                } catch (MicrosoftAuthenticationException e) {
                                    status = STR."failed to login: \{e.getMessage()}";
                                }
                            }).start();
                        }
                    }
                    case "Random cracked alt" -> {
                        String randomUsername = STR."Atlas\{RandomValueUtil.getRandomString(6)}";
                        mc.session = new Session(randomUsername, "", "", "mojang");
                        status = STR."logged in as: \{randomUsername}";
                    }
                    case "Load from .txt file" -> new Thread(() -> {
                        List<SessionUtil.LoadedAccount> loadedAccounts = SessionUtil.INSTANCE.loadAccounts(FileUtil.getFileFromDialog());
                        if (!loadedAccounts.isEmpty()) {
                            for (SessionUtil.LoadedAccount account : loadedAccounts) {
                                AccountManager.getSingleton().addByType("OpenAuth", account.username, account.password);
                            }
                            status = "successfully loaded alt(s)";
                        } else {
                            status = "no accounts loaded from the selected file.";
                        }
                    }).start();
                    case "Back" -> mc.displayGuiScreen(new GuiMainMenu());
                }
            }
        }
        for (TextFieldElement field : this.guiTextFields) {
            field.click(mouseX, mouseY, mouseButton);
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        for (TextFieldElement field : this.guiTextFields) {
            if(field.type(typedChar, keyCode))
                return;
        }

        if(keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(new GuiMainMenu());
        }
    }
}
