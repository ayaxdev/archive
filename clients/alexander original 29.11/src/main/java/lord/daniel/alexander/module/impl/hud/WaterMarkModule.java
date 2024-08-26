package lord.daniel.alexander.module.impl.hud;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import lord.daniel.alexander.draggable.Draggable;
import lord.daniel.alexander.draggable.annotations.DraggableInfo;
import lord.daniel.alexander.draggable.interfaces.IDraggableElement;
import lord.daniel.alexander.event.impl.game.Render2DEvent;
import lord.daniel.alexander.module.abstracts.AbstractModule;
import lord.daniel.alexander.module.data.ModuleData;
import lord.daniel.alexander.module.enums.EnumModuleType;
import lord.daniel.alexander.settings.impl.bool.BooleanValue;
import lord.daniel.alexander.settings.impl.mode.FontValue;
import lord.daniel.alexander.settings.impl.mode.StringModeValue;
import lord.daniel.alexander.settings.impl.number.NumberValue;
import lord.daniel.alexander.settings.impl.number.color.ColorValue;
import lord.daniel.alexander.settings.impl.string.StringValue;
import lord.daniel.alexander.util.java.StringUtil;
import lord.daniel.alexander.util.render.RenderUtil;
import lord.daniel.alexander.util.render.shader.render.ingame.ShaderRenderer;
import net.minecraft.client.gui.FontRenderer;

import java.awt.*;

/**
 * Written by 'Daniel.' for Alexander client.
 * Please don't use the code.
 *
 * @author Daniel.
 */
@DraggableInfo
@ModuleData(name = "WaterMark", aliases = "ClientName", enumModuleType = EnumModuleType.HUD)
public class WaterMarkModule extends AbstractModule implements IDraggableElement {

    private final StringModeValue mode = new StringModeValue("Mode", this, "Custom", new String[]{"Custom", "Corner"});
    private final StringValue text = new StringValue("Text", this, "AlexanderClient b%VERSION%").addVisibleCondition(() -> mode.is("Custom") || mode.is("Corner"));
    private final FontValue fontRenderer = new FontValue("Font", this).addVisibleCondition(() -> mode.is("Custom") || mode.is("Corner"));
    private final BooleanValue autoScale = new BooleanValue("AutoScale", this, true).addVisibleCondition(() -> mode.is("Custom"));
    private final NumberValue<Float> textX = new NumberValue<>("AddTextX", this, 0f, -10f, 10f, 1, false).addVisibleCondition(() -> mode.is("Custom"));
    private final NumberValue<Float> textY = new NumberValue<>("AddTextY", this, 0f, -10f, 10f, 1, false).addVisibleCondition(() -> mode.is("Custom"));
    private final BooleanValue renderBackground = new BooleanValue("RenderBackground", this, true);
    private final ColorValue backgroundColor = new ColorValue("BackgroundColor", this, new Color(0, 0, 0, 130)).addVisibleCondition(() -> mode.is("Custom") || mode.is("Corner") && renderBackground.getValue());
    private final ColorValue textColor = new ColorValue("TextColor", this, new Color(255, 255, 255, 255)).addVisibleCondition(() -> mode.is("Custom") || mode.is("Corner"));
    private final BooleanValue textShadow = new BooleanValue("TextShadow", this, true);

    private final Draggable draggable = new Draggable("WaterMark", 4, 4f, -1, -1, true, () -> this.isEnabled() && mode.is("Custom"));
    private final Draggable[] draggables = new Draggable[] { this.draggable };

    private String lastRendered = null;
    private FontRenderer lastFont = null;

    @EventLink
    public final Listener<Render2DEvent> render2DEventListener = render2DEvent -> {
        final FontRenderer fontRenderer = this.fontRenderer.getValue();
        final String text = StringUtil.format(this.text.getValueAsString());

        switch (mode.getValue()) {
            case "Corner" -> {
                ShaderRenderer.render(shader -> {
                    if(renderBackground.getValue())
                        RenderUtil.drawRect(0, 0, fontRenderer.getStringWidth(text) + 2, fontRenderer.FONT_HEIGHT + 2, backgroundColor.getValue());
                });
                fontRenderer.drawStringWithShadow(text, 1, 1.5f, textColor.getValue().getRGB());
            }

            case "Custom" -> {
                if(draggable.getWidth() == -1 || draggable.getHeight() == -1) {
                    draggable.setWidth(fontRenderer.getStringWidth(text) + 5);
                    draggable.setHeight(fontRenderer.FONT_HEIGHT + 3);
                }

                if(autoScale.getValue()) {
                    if(lastRendered != null && lastFont != null && (!lastRendered.equals(text) || !lastFont.equals(fontRenderer))) {
                        float diffX = fontRenderer.getStringWidth(text) - lastFont.getStringWidth(lastRendered);
                        this.draggable.setWidth(draggable.getWidth() + diffX);

                        float diffY = fontRenderer.FONT_HEIGHT - lastFont.FONT_HEIGHT;
                        this.draggable.setHeight(draggable.getHeight() + diffY);
                    }
                }

                lastFont = fontRenderer;
                lastRendered = text;

                ShaderRenderer.render(shader -> {
                    if(renderBackground.getValue())
                        RenderUtil.drawRect(draggable.getPosX(), draggable.getPosY(), draggable.getWidth(), draggable.getHeight(), backgroundColor.getValue().getRGB());

                    if(!shader) {
                        if(textShadow.getValue())
                            fontRenderer.drawStringWithShadow(text, draggable.getPosX() + draggable.getWidth() / 2 - fontRenderer.getStringWidth(text) / 2f + textX.getValue(), draggable.getPosY() + draggable.getHeight() / 2 - fontRenderer.FONT_HEIGHT / 2f + textY.getValue(), textColor.getValue().getRGB());
                        else
                            fontRenderer.drawString(text, draggable.getPosX() + draggable.getWidth() / 2 - fontRenderer.getStringWidth(text) / 2f + textX.getValue(), draggable.getPosY() + draggable.getHeight() / 2 - fontRenderer.FONT_HEIGHT / 2f + textY.getValue(), textColor.getValue().getRGB());
                    }
                });
            }
        }
    };

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public Draggable[] getDraggableElements() {
        return draggables;
    }
}
