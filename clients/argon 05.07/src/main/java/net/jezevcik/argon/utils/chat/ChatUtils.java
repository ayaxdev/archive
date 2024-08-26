package net.jezevcik.argon.utils.chat;

import net.jezevcik.argon.system.minecraft.Minecraft;
import net.jezevcik.argon.utils.game.TextUtils;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.awt.*;

/**
 * A set of methods used for interacting with the chat ui
 */
public class ChatUtils implements Minecraft {

    public static final Text PREFIX = TextUtils.gradient("Argon", Style.EMPTY, new Color(255, 220, 0), new Color(255, 188, 0));
    public static final Text ERROR_PREFIX = TextUtils.gradient("Error!", Style.EMPTY, new Color(255, 0, 0), new Color(225, 0, 0));
    public static final Text SUCCESS_PREFIX = TextUtils.gradient("Success!", Style.EMPTY, new Color(0, 223, 0), new Color(0, 165, 0));

    public enum Prefix {
        NORMAL(PREFIX), ERROR(ERROR_PREFIX), SUCCESS(SUCCESS_PREFIX);

        public final Text text;

        Prefix(Text text) {;
            this.text = text;
        }
    }

    /**
     * Pushes a text message to the UI.
     *
     * @param text The text message.
     */
    public static void push(Text text) {
        client.inGameHud.getChatHud().addMessage(text);
    }

    /**
     * Pushes a text message to the UI.
     *
     * @param text The text message.
     */
    public static void push(String text) {
        ChatUtils.push(Text.literal(text));
    }

    /**
     * Pushes a text message with the provided prefix to the UI.
     *
     * @param text The text message.
     * @param prefix The provided prefix.
     */
    public static void pushWithPrefix(Text text, Prefix prefix) {
        final Text drawn = prefix.text.copy().append(" ").append(text);
        client.inGameHud.getChatHud().addMessage(drawn);
    }

    /**
     * Pushes a text message with the provided prefix to the UI.
     *
     * @param text The text message.
     * @param prefix The provided prefix.
     */
    public static void pushWithPrefix(String text, Prefix prefix) {
        final Text drawn = prefix.text.copy().append(" ").append(text);
        ChatUtils.push(drawn);
    }

    /**
     * Pushes a text message with the client's prefix to the UI.
     *
     * @param text The text message.
     */
    public static void pushWithPrefix(Text text) {
        pushWithPrefix(text, Prefix.NORMAL);
    }

    /**
     * Pushes a text message with the client's prefix to the UI.
     *
     * @param text The text message.
     */
    public static void pushWithPrefix(String text) {
        pushWithPrefix(text, Prefix.NORMAL);
    }

}
