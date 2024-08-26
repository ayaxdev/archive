package net.jezevcik.argon.renderer;

import net.jezevcik.argon.mixin.DrawContextAccessor;
import net.jezevcik.argon.system.minecraft.Minecraft;
import net.jezevcik.argon.utils.render.RenderCoordinateUtils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ColorHelper;
import org.joml.Matrix4f;

import java.awt.*;

public class UiBuilder implements Minecraft {

    public static final UiBuilder NO_DRAW = new UiBuilder(null);

    private final DrawContext currentContext;

    public UiBuilder(DrawContext currentContext) {
        this.currentContext = currentContext;
    }

    public MatrixStack getMatrices() {
        return currentContext.getMatrices();
    }

    public void push() {
        getMatrices().push();
    }

    public void pop() {
        getMatrices().pop();
    }

    public void scale(float x, float y, float z) {
        getMatrices().scale(x, y, z);
    }

    public void scale(float x, float y) {
        getMatrices().scale(x, y, 1);
    }

    public void translate(float x, float y, float z) {
        getMatrices().translate(x, y, z);
    }

    public void translate(float x, float y) {
        getMatrices().translate(x, y, 0);
    }

    public Text text() {
        return new Text();
    }

    public Text text(String text) {
        return new Text().value(text);
    }

    public Text text(net.minecraft.text.Text text) {
        return new Text().value(text);
    }

    public Rect rect() {
        return new Rect();
    }

    public UiBuilder rect(float x, float y, float x2, float y2, int color, boolean relative) {
        if (currentContext == null)
            throw new MissingContextException();

        if (relative) {
            x2 += x;
            y2 += y;
        }

        final Matrix4f matrix4f = currentContext.getMatrices().peek().getPositionMatrix();

        float i;
        if (x < x2) {
            i = x;
            x = x2;
            x2 = i;
        }

        if (y < y2) {
            i = y;
            y = y2;
            y2 = i;
        }

        final float alpha = (float) ColorHelper.Argb.getAlpha(color) / 255.0F;
        final float red = (float) ColorHelper.Argb.getRed(color) / 255.0F;
        final float green = (float) ColorHelper.Argb.getGreen(color) / 255.0F;
        final float blue = (float) ColorHelper.Argb.getBlue(color) / 255.0F;

        final VertexConsumer vertexConsumer = currentContext.getVertexConsumers().getBuffer(RenderLayer.getGuiOverlay());
        vertexConsumer.vertex(matrix4f, x, y, 0f).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, x, y2, 0f).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, x2, y2, 0f).color(red, green, blue, alpha).next();
        vertexConsumer.vertex(matrix4f, x2, y, 0f).color(red, green, blue, alpha).next();

        ((DrawContextAccessor) currentContext).invokeTryDraw();

        return this;
    }

    public UiBuilder text(String text, float x, float y, int color, boolean shadow) {
        if (currentContext == null)
            throw new MissingContextException();

        if (text != null) {
            client.textRenderer.draw(text, x, y, color, shadow, currentContext.getMatrices().peek().getPositionMatrix(), currentContext.getVertexConsumers(), TextRenderer.TextLayerType.NORMAL, 0, 15728880, client.textRenderer.isRightToLeft());
            ((DrawContextAccessor) currentContext).invokeTryDraw();
        }

        return this;
    }

    public UiBuilder text(net.minecraft.text.Text text, float x, float y, int color, boolean shadow) {
        if (currentContext == null)
            throw new MissingContextException();

        if (text != null) {
            client.textRenderer.draw(text.asOrderedText(), x, y, color, shadow, currentContext.getMatrices().peek().getPositionMatrix(), currentContext.getVertexConsumers(), TextRenderer.TextLayerType.NORMAL, 0, 15728880);
            ((DrawContextAccessor) currentContext).invokeTryDraw();
        }

        return this;
    }

    public float getWidth() {
        return currentContext.getScaledWindowWidth();
    }

    public float getHeight() {
        return currentContext.getScaledWindowHeight();
    }

    public class Text {

        private net.minecraft.text.Text text = net.minecraft.text.Text.empty();
        private float x = 0F, y = 0F;
        private int color = -1;
        private boolean shadow = false;

        public Text value(String text) {
            this.text = net.minecraft.text.Text.literal(text);
            return this;
        }

        public Text value(net.minecraft.text.Text text) {
            this.text = text;
            return this;
        }

        public Text x(float x) {
            this.x = x;
            return this;
        }

        public Text y(float y) {
            this.y = y;
            return this;
        }

        public TextCoordinate x() {
            return new TextCoordinate(RenderCoordinateUtils.Coordinate.X);
        }

        public TextCoordinate y() {
            return new TextCoordinate(RenderCoordinateUtils.Coordinate.Y);
        }

        public Text color(int color) {
            this.color = color;
            return this;
        }

        public Text color(Color color) {
            this.color = color.getRGB();
            return this;
        }

        public Text shadow() {
            this.shadow = !shadow;
            return this;
        }

        public UiBuilder draw() {
            return UiBuilder.this.text(text, x, y, color, shadow);
        }

        public net.minecraft.text.Text getText() {
            return text;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public int getColor() {
            return color;
        }

        public boolean isShadow() {
            return shadow;
        }

        public class TextCoordinate extends Coordinate<TextCoordinate> {
            
            public TextCoordinate(RenderCoordinateUtils.Coordinate coordinate) {
                this.coordinate = coordinate;
            }

            public TextCoordinate centered(float base, float space) {
                float size;

                if (coordinate == RenderCoordinateUtils.Coordinate.X) {
                    size = client.textRenderer.getWidth(Text.this.text);
                } else {
                    size = client.textRenderer.fontHeight;
                }

                this.value = RenderCoordinateUtils.getCentered(base, size, space);

                return this;
            }

            public TextCoordinate back() {
                float size;

                if (coordinate == RenderCoordinateUtils.Coordinate.X) {
                    size = client.textRenderer.getWidth(Text.this.text);
                } else {
                    size = client.textRenderer.fontHeight;
                }

                this.value -= size;

                return this;
            }

            public Text finish() {
                if (coordinate == RenderCoordinateUtils.Coordinate.X)
                    Text.this.x = value;
                else if (coordinate == RenderCoordinateUtils.Coordinate.Y)
                    Text.this.y = value;

                return Text.this;
            }

        }

    }

    public class Rect {
        private float x = 0F, y = 0F, x2 = 0F, y2 = 0F;
        private int color = -1;

        public Rect x(float x) {
            this.x = x;
            return this;
        }

        public Rect y(float y) {
            this.y = y;
            return this;
        }

        public Rect x2(float x) {
            this.x2 = x;
            return this;
        }

        public Rect y2(float y) {
            this.y2 = y;
            return this;
        }

        public Rect width(float width) {
            this.x2 = x + width;
            return this;
        }

        public Rect height(float height) {
            this.y2 = y + height;
            return this;
        }

        public Rect color(int color) {
            this.color = color;
            return this;
        }

        public Rect color(Color color) {
            this.color = color.getRGB();
            return this;
        }

        public RectCoordinate x() {
            return new RectCoordinate(RenderCoordinateUtils.Coordinate.X);
        }

        public RectCoordinate x2() {
            return new RectCoordinate(RenderCoordinateUtils.Coordinate.X2);
        }

        public RectCoordinate y() {
            return new RectCoordinate(RenderCoordinateUtils.Coordinate.Y);
        }

        public RectCoordinate y2() {
            return new RectCoordinate(RenderCoordinateUtils.Coordinate.Y2);
        }

        public UiBuilder draw() {
            return UiBuilder.this.rect(x, y, x2, y2, color, false);
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

        public float getX2() {
            return x2;
        }

        public float getY2() {
            return y2;
        }

        public float getWidth() {
            return x2 - x;
        }

        public float getHeight() {
            return y2 - y;
        }

        public int getColor() {
            return color;
        }

        public class RectCoordinate extends Coordinate<RectCoordinate> {
            
            public RectCoordinate(RenderCoordinateUtils.Coordinate coordinate) {
                this.coordinate = coordinate;
            }

            public RectCoordinate relative(float value) {
                this.value = switch (coordinate) {
                    case X -> Rect.this.x2 - value;
                    case Y -> Rect.this.y2 - value;
                    case X2 -> Rect.this.x + value;
                    case Y2 -> Rect.this.y + value;
                };
                return this;
            }
            
            public Rect finish() {
                if (coordinate == RenderCoordinateUtils.Coordinate.X)
                    Rect.this.x = value;
                else if (coordinate == RenderCoordinateUtils.Coordinate.Y)
                    Rect.this.y = value;
                else if (coordinate == RenderCoordinateUtils.Coordinate.X2)
                    Rect.this.x2 = value;
                else if (coordinate == RenderCoordinateUtils.Coordinate.Y2)
                    Rect.this.y2 = value;

                return Rect.this;
            }
            
        }
        
    }
    
    @SuppressWarnings("unchecked")
    public class Coordinate<T extends Coordinate<?>> {
        
        protected RenderCoordinateUtils.Coordinate coordinate;
        protected float value;

        public T absolute(float value) {
            this.value = value;
            return (T) this;
        }
        
        public T center() {
            this.value = switch (coordinate) {
                case X, X2 -> client.getWindow().getScaledWidth() / 2f;
                case Y, Y2 -> client.getWindow().getScaledHeight() / 2f;
            };

            return (T) this;
        }

        public T offset(float v) {
            this.value += v;
            return (T) this;
        }
    }

}
