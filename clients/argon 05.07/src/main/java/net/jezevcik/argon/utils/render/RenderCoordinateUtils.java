package net.jezevcik.argon.utils.render;

import net.jezevcik.argon.system.minecraft.Minecraft;

/**
 * A set of methods for calculating coordinates of UI elements.
 */
public class RenderCoordinateUtils implements Minecraft {

    /**
     * Returns the coordinate of a centered text.
     *
     * @param text The text being drawn.
     * @param x The X value of the element in which the text will be centered.
     * @param width The width of the element in which the text will be centered.
     * @return The centered coordinate.
     */
    public static float getFontCenteredX(String text, float x, float width) {
        return getCentered(x, client.textRenderer.getWidth(text), width);
    }

    /**
     * Returns the coordinate of a centered text.
     *
     * @param text The text being drawn.
     * @param y The Y value of the element in which the text will be centered.
     * @param height The height of the element in which the text will be centered.
     * @return The centered coordinate.
     */
    public static float getFontCenteredY(String text, float y, float height) {
        return getCentered(y, client.textRenderer.fontHeight, height);
    }

    /**
     * Returns the coordinate of a centered text.
     *
     * @param text The text being drawn.
     * @param middleX The middle of the element in which the text will be centered
     * @return The centered coordinate.
     */
    public static float getFontCenteredX(String text, float middleX) {
        return getCentered(middleX, client.textRenderer.getWidth(text));
    }

    /**
     * Returns the coordinate of a centered text.
     *
     * @param text The text being drawn.
     * @param middleY The middle of the element in which the text will be centered
     * @return The centered coordinate.
     */
    public static float getFontCenteredY(String text, float middleY) {
        return getCentered(middleY, client.textRenderer.fontHeight);
    }

    /**
     * Returns the coordinate of a centered element
     *
     * @param middle The middle of the element in which the element will be centered
     * @param elementSize The size of the element
     * @return The centered coordinate
     */
    public static float getCentered(float middle, float elementSize) {
        return middle - elementSize / 2f;
    }

    /**
     * Returns the coordinate of a centered element
     *
     * @param base The coordinate of the element in which the element will be centered
     * @param elementSize The size of the element
     * @param spaceSize The size of the element in which the element will be centered
     * @return The centered coordinate
     */
    public static float getCentered(float base, float elementSize, float spaceSize) {
        return (base + spaceSize / 2f) - (elementSize / 2f);
    }

    public enum Coordinate {
        X, Y, X2, Y2;
    }

}
