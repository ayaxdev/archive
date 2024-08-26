package net.jezevcik.argon.screen.frames.compact.window;

import net.jezevcik.argon.config.Config;
import net.jezevcik.argon.config.interfaces.ConfigEntry;
import net.jezevcik.argon.config.interfaces.Configurable;
import net.jezevcik.argon.config.setting.Setting;
import net.jezevcik.argon.config.setting.impl.*;
import net.jezevcik.argon.renderer.UiBuilder;
import net.jezevcik.argon.screen.ScreenCalls;
import net.jezevcik.argon.screen.frames.compact.CompactFramesScreen;
import net.jezevcik.argon.system.identifier.IdentifierType;
import net.jezevcik.argon.system.minecraft.Minecraft;
import net.jezevcik.argon.utils.math.GeometryUtils;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Window implements ScreenCalls, Minecraft {

    public final static float WINDOW_WIDTH = 300
            , WINDOW_HEIGHT = 500;

    private final List<Setting<?>> interacting = new ArrayList<>();

    public final CompactFramesScreen parent;
    public final Object selected;

    public Window(CompactFramesScreen parent, Object selected) {
        this.parent = parent;
        this.selected = selected;
    }

    @Override
    public boolean run(ScreenCall screenCall) {
        final CallType callType = screenCall.call();
        final UiBuilder uiBuilder = screenCall.uiBuilder();

        // Back
        final UiBuilder.Rect back = uiBuilder.rect()
                .x().center().offset(-WINDOW_WIDTH / 2).finish()
                .y().center().offset(-WINDOW_HEIGHT / 2).finish()
                .width(WINDOW_WIDTH).height(WINDOW_HEIGHT)
                .color(CompactFramesScreen.BACK_COLOR);

        if (callType == CallType.DRAW_SCREEN) {
            // Left border
            uiBuilder.rect(back.getX(), back.getY(), 1.5f, back.getHeight(), Color.black.getRGB(), true);

            // Right border
            uiBuilder.rect(back.getX() + back.getWidth() - 1.5f, back.getY(), 1.5f, back.getHeight(), Color.black.getRGB(), true);

            // Top border
            uiBuilder.rect(back.getX(), back.getY(), back.getWidth(), 1.5f, Color.black.getRGB(), true);

            // Bottom
            uiBuilder.rect(back.getX(), back.getY() + back.getHeight() - 1.5f, back.getWidth(), 1.5f, Color.black.getRGB(), true);
        }

        if (callType == CallType.DRAW_SCREEN)
            back.draw();

        if (!(selected instanceof Configurable configurable))
            return false;

        synchronized (ConfigEntry.class) {
            final float initialX = back.getX() + 5;
            float entryX = initialX;
            float entryY = back.getY() + 5;

            entryLoop : for (ConfigEntry entry : configurable.getConfig().getListWithSubs()) {
                if (!entry.visible())
                    continue;

                if (entry instanceof Config config && config.hidden)
                    continue;

                final List<Config> parents = entry.getParents(true);

                entryX += entry.getParents(false).size() * 10;

                for (Config parent : parents) {
                    if (!parent.enabled || !parent.visible()) {
                        entryX = initialX;

                        continue entryLoop;
                    }
                }

                if (!parents.isEmpty() && !entry.getConfig().enabled) {
                    entryX = initialX;

                    continue;
                }

                final float entryHeight = client.textRenderer.fontHeight + 3;

                if (callType == CallType.DRAW_SCREEN)
                    uiBuilder.text(entry.getIdentifier(IdentifierType.DISPLAY))
                            .x(entryX).y().centered(entryY, entryHeight).finish()
                            .shadow().draw();

                switch (entry) {
                    case Config config -> {
                        final UiBuilder.Rect button = uiBuilder.rect()
                                .x().absolute(back.getX()).offset(WINDOW_WIDTH).offset(-15).finish()
                                .y().absolute(entryY).offset(entryHeight / 2).offset(-5).finish()
                                .color(Color.darkGray).width(10).height(10);

                        if (callType == CallType.DRAW_SCREEN) {
                            button.draw();
                        } else if (callType == CallType.MOUSE_CLICK && GeometryUtils.isInBounds(screenCall.mouseX(), screenCall.mouseY(), button.getX(), button.getY(), button.getWidth(), button.getHeight(), true)) {
                            config.enabled = !config.enabled;
                            return true;
                        }

                        if (!config.enabled) {
                            final float offset = 1.5f;

                            if (callType == CallType.DRAW_SCREEN)
                                uiBuilder.rect().x(button.getX() + offset)
                                        .y(button.getY() + offset)
                                        .width(button.getWidth() - offset * 2)
                                        .height(button.getHeight() - offset * 2)
                                        .color(CompactFramesScreen.BACK_COLOR).draw();
                        }
                    }

                    case BooleanSetting booleanSetting -> {
                        final UiBuilder.Rect button = uiBuilder.rect()
                                .x().absolute(back.getX()).offset(WINDOW_WIDTH).offset(-15).finish()
                                .y().absolute(entryY).offset(entryHeight / 2).offset(-5).finish()
                                .color(Color.darkGray).width(10).height(10);

                        if (callType == CallType.DRAW_SCREEN) {
                            button.draw();
                        } else if (callType == CallType.MOUSE_CLICK && GeometryUtils.isInBounds(screenCall.mouseX(), screenCall.mouseY(), button.getX(), button.getY(), button.getWidth(), button.getHeight(), true)) {
                            booleanSetting.setValue(!booleanSetting.getValue());
                            return true;
                        }

                        if (!booleanSetting.getValue()) {
                            final float offset = 1.5f;

                            if (callType == CallType.DRAW_SCREEN)
                                uiBuilder.rect().x(button.getX() + offset)
                                        .y(button.getY() + offset)
                                        .width(button.getWidth() - offset * 2)
                                        .height(button.getHeight() - offset * 2)
                                        .color(CompactFramesScreen.BACK_COLOR).draw();
                        }
                    }

                    case NumberSetting<?> numberSetting -> {
                        final float sliderWidth = 100;

                        final UiBuilder.Rect slider = uiBuilder.rect()
                                .x().absolute(back.getX()).offset(WINDOW_WIDTH).offset(-sliderWidth).offset(-5).finish()
                                .y().absolute(entryY).offset(entryHeight / 2).offset(-3).finish()
                                .color(CompactFramesScreen.BACK_COLOR).width(sliderWidth).height(6);

                        if (callType == CallType.DRAW_SCREEN)
                            slider.draw();

                        final boolean hovered = GeometryUtils.isInBounds(screenCall.mouseX(), screenCall.mouseY(), slider.getX() - 3, slider.getY() - 3, slider.getWidth() + 6, slider.getHeight() + 6, true);

                        if (callType == CallType.MOUSE_RELEASE) {
                            interacting.remove(numberSetting);
                        }

                        if (callType == CallType.MOUSE_CLICK && hovered) {
                            if (!interacting.contains(numberSetting))
                                interacting.add(numberSetting);
                            return true;
                        } else if (callType == CallType.DRAW_SCREEN && interacting.contains(numberSetting)) {
                            double percentage = screenCall.mouseX() - slider.getX();
                            percentage /= sliderWidth;
                            percentage = Math.max(Math.min(percentage, 1), 0);

                            numberSetting.setValue(numberSetting.min.doubleValue() + (numberSetting.max.doubleValue() - numberSetting.min.doubleValue()) * percentage);
                        }

                        final float selectedSliderWidth = MathHelper.floor((numberSetting.getValue().doubleValue() - numberSetting.min.doubleValue()) / (numberSetting.max.doubleValue() - numberSetting.min.doubleValue()) * sliderWidth);

                        final UiBuilder.Rect selectedSlider = uiBuilder.rect()
                                .x().absolute(slider.getX()).finish()
                                .y().absolute(slider.getY()).finish()
                                .width(selectedSliderWidth).height(slider.getHeight())
                                .color(Color.DARK_GRAY);

                        if (callType == CallType.DRAW_SCREEN) {
                            selectedSlider.draw();

                            uiBuilder.text(numberSetting.getValue().toString())
                                    .x().absolute(slider.getX()).offset(-5).back().finish()
                                    .y().centered(entryY, entryHeight).finish()
                                    .shadow().draw();
                        }
                    }

                    case ModeSetting modeSetting -> {
                        final float baseHeight = 10;
                        final String selected = modeSetting.getValue();
                        final String longest = Arrays.stream(modeSetting.modes).max(Comparator.comparingInt(client.textRenderer::getWidth)).orElse(selected);
                        final boolean expanded = this.interacting.contains(modeSetting);

                        final float rectWidth = client.textRenderer.getWidth(longest);

                        final UiBuilder.Rect textRect = uiBuilder.rect()
                                .x().absolute(back.getX()).offset(WINDOW_WIDTH).offset(-rectWidth - 4).offset(-5).finish()
                                .y().absolute(entryY).offset(entryHeight / 2).offset(-5).finish()
                                .width(rectWidth + 4).height(expanded ? baseHeight * (modeSetting.modes.length + 1) : baseHeight);

                        if (callType == CallType.DRAW_SCREEN) {
                            uiBuilder.text(selected)
                                    .x().centered(textRect.getX(), textRect.getWidth()).finish()
                                    .y().centered(textRect.getY(), baseHeight).finish()
                                    .shadow().color(Color.white).draw();
                        }

                        if (expanded) {
                            float modeY = textRect.getY() + baseHeight;

                            for (String mode : modeSetting.modes) {
                                if (callType == CallType.DRAW_SCREEN)
                                    uiBuilder.text(mode)
                                            .x().centered(textRect.getX(), textRect.getWidth()).finish()
                                            .y().centered(modeY, baseHeight).finish()
                                            .shadow().color(Color.white).draw();
                                else if (callType == CallType.MOUSE_CLICK)
                                    if (GeometryUtils.isInBounds(screenCall.mouseX(), screenCall.mouseY(), textRect.getX(), modeY, textRect.getWidth(), baseHeight, true)) {
                                        modeSetting.setValue(mode);
                                        this.interacting.remove(modeSetting);
                                        return true;
                                    }

                                modeY += baseHeight;
                                entryY += baseHeight;
                            }
                        }

                        if (callType == CallType.MOUSE_CLICK) {
                            final boolean hovered = GeometryUtils.isInBounds(screenCall.mouseX(), screenCall.mouseY(), textRect.getX(), textRect.getY()
                                    , textRect.getWidth(), baseHeight, true);

                            if (hovered) {
                                if (this.interacting.contains(modeSetting))
                                    this.interacting.remove(modeSetting);
                                else
                                    this.interacting.add(modeSetting);

                                return true;
                            }
                        }
                    }

                    case MultiSetting multiSetting -> {
                        final float baseHeight = 10;
                        final String selected = multiSetting.getValue().length + " Enabled";
                        final String longestMode = multiSetting.modes.stream().max(Comparator.comparingInt(client.textRenderer::getWidth)).orElse(selected);
                        final String longest = client.textRenderer.getWidth(selected) > client.textRenderer.getWidth(longestMode) ? selected : longestMode;
                        final boolean expanded = this.interacting.contains(multiSetting);

                        final float rectWidth = client.textRenderer.getWidth(longest);

                        final UiBuilder.Rect textRect = uiBuilder.rect()
                                .x().absolute(back.getX()).offset(WINDOW_WIDTH).offset(-rectWidth - 4).offset(-5).finish()
                                .y().absolute(entryY).offset(entryHeight / 2).offset(-5).finish()
                                .width(rectWidth + 4).height(expanded ? baseHeight * (multiSetting.modes.size() + 1) : baseHeight);

                        if (callType == CallType.DRAW_SCREEN) {
                            uiBuilder.text(selected)
                                    .x().centered(textRect.getX(), textRect.getWidth()).finish()
                                    .y().centered(textRect.getY(), baseHeight).finish()
                                    .shadow().color(Color.white).draw();
                        }

                        if (expanded) {
                            float modeY = textRect.getY() + baseHeight;

                            for (String mode : multiSetting.modes) {
                                if (callType == CallType.DRAW_SCREEN)
                                    uiBuilder.text(Text.literal(mode).setStyle(Style.EMPTY.withBold(multiSetting.isEnabled(mode))))
                                            .x().centered(textRect.getX(), textRect.getWidth()).finish()
                                            .y().centered(modeY, baseHeight).finish()
                                            .shadow().color(Color.white).draw();
                                else if (callType == CallType.MOUSE_CLICK)
                                    if (GeometryUtils.isInBounds(screenCall.mouseX(), screenCall.mouseY(), textRect.getX(), modeY, textRect.getWidth(), baseHeight, true)) {
                                        multiSetting.toggle(mode);
                                        return true;
                                    }

                                modeY += baseHeight;
                                entryY += baseHeight;
                            }
                        }

                        if (callType == CallType.MOUSE_CLICK) {
                            final boolean hovered = GeometryUtils.isInBounds(screenCall.mouseX(), screenCall.mouseY(), textRect.getX(), textRect.getY()
                                    , textRect.getWidth(), baseHeight, true);

                            if (hovered) {
                                if (this.interacting.contains(multiSetting))
                                    this.interacting.remove(multiSetting);
                                else
                                    this.interacting.add(multiSetting);

                                return true;
                            }
                        }
                    }

                    case TextSetting textSetting -> {
                        final String text = textSetting.getValue() + (interacting.contains(textSetting) ? "_" : "");
                        final float rectWidth = client.textRenderer.getWidth(text);

                        final UiBuilder.Rect textRect = uiBuilder.rect()
                                .x().absolute(back.getX()).offset(WINDOW_WIDTH).offset(-rectWidth - 4).offset(-5).finish()
                                .y().absolute(entryY).offset(entryHeight / 2).offset(-5).finish()
                                .width(rectWidth + 4).height(10);

                        if (callType == CallType.DRAW_SCREEN) {
                            uiBuilder.text(text)
                                    .x().centered(textRect.getX(), textRect.getWidth()).finish()
                                    .y().centered(textRect.getY(), 14).finish()
                                    .shadow().color(Color.white).draw();
                        } else if (callType == CallType.MOUSE_CLICK) {
                            if (GeometryUtils.isInBounds(screenCall.mouseX(), screenCall.mouseY(), textRect.getX(), textRect.getY(), textRect.getWidth(), textRect.getHeight(), true)) {
                                if (interacting.contains(textSetting))
                                    interacting.remove(textSetting);
                                else
                                    interacting.add(textSetting);

                                return true;
                            }
                        } else if (callType == CallType.CHAR_TYPED && interacting.contains(textSetting)) {
                            textSetting.setValue(textSetting.getValue() + screenCall.chr());
                        } else if (callType == CallType.KEY_TYPED) {
                            if (screenCall.key() == GLFW.GLFW_KEY_BACKSPACE && interacting.contains(textSetting)) {
                                final String value = textSetting.getValue();
                                textSetting.setValue(value.substring(0, Math.max(0, value.length() - 1)));
                            } else if (screenCall.key() == GLFW.GLFW_KEY_ENTER) {
                                interacting.remove(textSetting);
                            }
                        }

                    }

                    default -> {
                    }
                }

                entryX = initialX;
                entryY += entryHeight;
            }
        }

        return false;
    }
}
